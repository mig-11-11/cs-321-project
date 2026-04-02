/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.breakout;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.PausePanel;

import javax.swing.*;
import java.awt.event.HierarchyEvent;


public class BreakoutGameAdapter implements ArcadeGame
{
    private final BreakPanel panel;

    private boolean firstEntryInstructionsPending = true;

    private final JLayeredPane layeredPane;
    private PausePanel pausePanel = null;


    public BreakoutGameAdapter(Runnable onExitToMenu)
    {
        panel = new BreakPanel(onExitToMenu);
        
        //Makes game controls still work
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && panel.isShowing())
            {
                SwingUtilities.invokeLater(() -> panel.requestFocusInWindow());
            }
        });

        // Create layered container
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // IMPORTANT for manual positioning

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                panel.setBounds(0, 0, w, h);
                pausePanel.setBounds(0, 0, w, h);
            }
        });

        // Game panel
        layeredPane.add(panel, Integer.valueOf(0));

        // Pause panel
        pausePanel = new PausePanel(

            // Resume
            () -> {
                pausePanel.setVisible(false);
                startGameLoop();
                panel.requestFocusInWindow();
            },

            // Restart
            () -> {
                resetGame();
                pausePanel.setVisible(false);
                startGameLoop();
                panel.requestFocusInWindow();
            },

            // Main Menu
            () -> {
                pausePanel.setVisible(false);
                stopGameLoop();
                resetGame();
                onExitToMenu.run(); // tells ArcadeFrame to switch
            }
        );

        pausePanel.setVisible(false);
        layeredPane.add(pausePanel, Integer.valueOf(1));

        setupPauseKey();
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
                    stopGameLoop();
                }
            });
        }

    
    
    
    
    
    
    
    @Override
    public String getCardName()
    {
        return "breakout";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Breakout";
    }

    @Override
    public JComponent getView()
    {
        return panel;
    }

    @Override
    public void resetGame()
    {

          panel.resetGame();

    }

    @Override
    public void startGameLoop()
    {
        if (firstEntryInstructionsPending)
        {
            panel.showInstructionsCard();
            firstEntryInstructionsPending = false;
        }

        panel.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}
