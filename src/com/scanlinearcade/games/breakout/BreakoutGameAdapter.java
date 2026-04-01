/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.breakout;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.GameOverPanel;
import com.scanlinearcade.app.PausePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;


public class BreakoutGameAdapter implements ArcadeGame
{
    private final BreakPanel panel;
    private final JLayeredPane layeredPane;
    private PausePanel pausePanel = null;
    private GameOverPanel gameOverPanel = null;
    private boolean firstEntryInstructionsPending = true;

    public BreakoutGameAdapter(Runnable onExitToMenu)
    {
        panel = new BreakPanel(() -> exitToMenu(onExitToMenu), this::showGameOverOverlay);
        
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

                if (gameOverPanel != null)
                {
                    gameOverPanel.setBounds(0, 0, w, h);
                }
            }
        });

        // Game panel
        layeredPane.add(panel, Integer.valueOf(0));

        // Pause panel
        pausePanel = new PausePanel(

            // Resume
            this::hidePauseOverlay,

            // Restart
            () -> {
                resetGame();
                hidePauseOverlay();
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

        gameOverPanel = new GameOverPanel(
            "breakout",
            () -> {
                gameOverPanel.setVisible(false);
                resetGame();
                startGameLoop();
                panel.requestFocusInWindow();
            },
            () -> {
                gameOverPanel.setVisible(false);
                exitToMenu(onExitToMenu);
            }
        );
        gameOverPanel.setVisible(false);
        layeredPane.add(gameOverPanel, Integer.valueOf(2));

        setupPauseKey();
    }

    private void setupPauseKey()
    {
        layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("SPACE"), "pause");

        layeredPane.getActionMap().put("pause", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                togglePauseOverlay();
            }
        });
    }

    private void togglePauseOverlay()
    {
        if (gameOverPanel.isVisible())
        {
            return;
        }

        if (panel.shouldSuppressPauseToggle())
        {
            return;
        }

        if (pausePanel.isVisible())
        {
            hidePauseOverlay();
            return;
        }

        showPauseOverlay();
    }

    private void showPauseOverlay()
    {
        pausePanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        pausePanel.setVisible(true);
        stopGameLoop();
        SwingUtilities.invokeLater(() -> pausePanel.requestFocusInWindow());
    }

    private void showGameOverOverlay(String resultText, int score, String runToken)
    {
        pausePanel.setVisible(false);
        gameOverPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        gameOverPanel.showResult(resultText, score, runToken);
        SwingUtilities.invokeLater(() -> gameOverPanel.requestFocusInWindow());
    }

    private void hidePauseOverlay()
    {
        pausePanel.setVisible(false);
        startGameLoop();
        panel.requestFocusInWindow();
    }

    private void exitToMenu(Runnable onExitToMenu)
    {
        pausePanel.setVisible(false);
        if (gameOverPanel != null)
        {
            gameOverPanel.setVisible(false);
        }

        stopGameLoop();
        resetGame();
        onExitToMenu.run();
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
        return layeredPane;
    }

    @Override
    public void resetGame()
    {
          pausePanel.setVisible(false);
          gameOverPanel.setVisible(false);
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
