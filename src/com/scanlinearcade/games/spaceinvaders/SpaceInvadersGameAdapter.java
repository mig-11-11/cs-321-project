/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.PausePanel;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

public class SpaceInvadersGameAdapter implements ArcadeGame
{
    private final Board panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final Runnable onExitToMenu;

    private boolean firstEntryInstructionsPending = true;

    public SpaceInvadersGameAdapter(Runnable onExitToMenu)
    {
        this.onExitToMenu = onExitToMenu;

        panel = new Board(onExitToMenu);

        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && panel.isShowing())
            {
                SwingUtilities.invokeLater(() -> panel.requestFocusInWindow());
            }
        });

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        pausePanel = new PausePanel(
            this::resumeFromPause,
            this::restartFromPause,
            this::returnToMenuFromPause
        );

        pausePanel.setVisible(false);

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e)
            {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                panel.setBounds(0, 0, w, h);
                pausePanel.setBounds(0, 0, w, h);
            }
        });

        layeredPane.add(panel, Integer.valueOf(0));
        layeredPane.add(pausePanel, Integer.valueOf(1));

        setupPauseKey();
    }

    private void resumeFromPause()
    {
        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    private void restartFromPause()
    {
        panel.resetGame();
        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    private void returnToMenuFromPause()
    {
        pausePanel.setVisible(false);
        panel.stopGameLoop();
        panel.resetGame();

        if (onExitToMenu != null)
        {
            onExitToMenu.run();
        }
    }

    private void setupPauseKey()
    {
        layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");

        layeredPane.getActionMap().put("pause", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if (pausePanel.isVisible())
                {
                    return;
                }

                if (panel.isShowingInstructionsCard())
                {
                    return;
                }

                pausePanel.setVisible(true);
                panel.stopGameLoop();
            }
        });
    }

    @Override
    public String getCardName()
    {
        return "invaders";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Space Invaders";
    }

    @Override
    public JComponent getView()
    {
        return board;
    }

    @Override
    public void resetGame()
    {
        panel.resetGame();
        pausePanel.setVisible(false);
    }

    @Override
    public void startGameLoop()
    {
        if (firstEntryInstructionsPending)
        {
            panel.showInstructionsCard();
            firstEntryInstructionsPending = false;
        }

        board.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        board.stopGameLoop();
    }
}
