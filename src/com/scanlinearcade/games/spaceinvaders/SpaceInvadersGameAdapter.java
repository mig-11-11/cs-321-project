/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.GameOverPanel;
import com.scanlinearcade.app.PausePanel;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

public class SpaceInvadersGameAdapter implements ArcadeGame
{
    private final Board panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final GameOverPanel gameOverPanel;
    private final Runnable onExitToMenu;

    public SpaceInvadersGameAdapter(Runnable onExitToMenu)
    {
        this.onExitToMenu = onExitToMenu;

        panel = new Board(onExitToMenu, this::showGameOver);

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
            this::showInstructionsFromPause,
            this::returnToMenuFromPause
        );

        gameOverPanel = new GameOverPanel(
            "invaders",
            this::restartFromGameOver,
            this::returnToMenuFromGameOver
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
                gameOverPanel.setBounds(0, 0, w, h);
            }
        });

        layeredPane.add(panel, Integer.valueOf(0));
        layeredPane.add(pausePanel, Integer.valueOf(1));
        layeredPane.add(gameOverPanel, Integer.valueOf(2));

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

    private void showInstructionsFromPause()
    {
        pausePanel.setVisible(false);
        panel.showInstructionsCard();
        panel.requestFocusInWindow();
    }

    private void showGameOver(String resultText, int score, String runToken)
    {
        pausePanel.setVisible(false);
        gameOverPanel.showResult(resultText, score, runToken);
    }

    private void restartFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.resetGame();
        panel.showFirstEntryInstructionsIfPending();
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    private void returnToMenuFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.stopGameLoop();
        panel.resetGame();

        if (onExitToMenu != null)
        {
            onExitToMenu.run();
        }
    }

    private void setupPauseKey()
    {
        AbstractAction pauseAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if (pausePanel.isVisible())
                {
                    return;
                }

                if (gameOverPanel.isVisible())
                {
                    return;
                }

                if (panel.isShowingInstructionsCard())
                {
                    return;
                }

                if (panel.shouldSuppressPauseToggle())
                {
                    return;
                }

                pausePanel.setVisible(true);
                panel.stopGameLoop();
            }
        };

        layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        layeredPane.getActionMap().put("pause", pauseAction);

        panel.getInputMap(JComponent.WHEN_FOCUSED)
             .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        panel.getActionMap().put("pause", pauseAction);
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
        gameOverPanel.setVisible(false);
        panel.resetGame();
        pausePanel.setVisible(false);
    }

    @Override
    public void startGameLoop()
    {
        gameOverPanel.setVisible(false);
        panel.showFirstEntryInstructionsIfPending();

        board.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        board.stopGameLoop();
    }
}
