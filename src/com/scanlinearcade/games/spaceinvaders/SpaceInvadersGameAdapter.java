/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.PausePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;


public class SpaceInvadersGameAdapter implements ArcadeGame
{
    private final Board panel;
    private final JLayeredPane layeredPane;
    private PausePanel pausePanel = null;

    public SpaceInvadersGameAdapter(Runnable onExitToMenu)
    {
        panel = new Board();
        
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

        // ⏸️ Pause panel
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
                onExitToMenu.run(); // 👈 tells ArcadeFrame to switch
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
                pausePanel.setVisible(true);
                stopGameLoop();
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
        return layeredPane;
    }

    @Override
    public void resetGame()
    {
        panel.resetGame();
    }

    @Override
    public void startGameLoop()
    {
        panel.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}