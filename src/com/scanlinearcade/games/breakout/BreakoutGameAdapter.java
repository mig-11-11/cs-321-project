/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.breakout;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.GameOverPanel;
import com.scanlinearcade.app.GameSettings;
import com.scanlinearcade.app.MusicPlayer;
import com.scanlinearcade.app.PausePanel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import java.awt.event.HierarchyEvent;


public class BreakoutGameAdapter implements ArcadeGame
{
    private final BreakPanel panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final GameOverPanel gameOverPanel;
    private final Runnable onExitToMenu;
    private final GameSettings settings;
    private final MusicPlayer musicPlayer;

    public BreakoutGameAdapter(GameSettings settings, MusicPlayer musicPlayer, Runnable onExitToMenu)
    {
        this.settings = settings;
        this.musicPlayer = musicPlayer;
        this.onExitToMenu = onExitToMenu;
        panel = new BreakPanel(onExitToMenu, this::showGameOver, settings);
        
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

        // Game panel
        layeredPane.add(panel, Integer.valueOf(0));

        // Pause panel
        pausePanel = new PausePanel(
            this::resumeFromPause,
            this::restartFromPause,
            this::showInstructionsFromPause,
            this::returnToMenuFromPause
        );

        gameOverPanel = new GameOverPanel(
            "breakout",
            this::restartFromGameOver,
            this::returnToMenuFromGameOver
        );

        pausePanel.setVisible(false);
        gameOverPanel.setVisible(false);
        layeredPane.add(pausePanel, Integer.valueOf(1));
        layeredPane.add(gameOverPanel, Integer.valueOf(2));

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                panel.setBounds(0, 0, w, h);
                pausePanel.setBounds(0, 0, w, h);
                gameOverPanel.setBounds(0, 0, w, h);
            }
        });

        setupPauseKey();
        
        addMusicListener();
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
                    stopGameLoop();
                }
            });
        }
        
    private void addMusicListener()
    {   
        gameOverPanel.addComponentListener(new ComponentAdapter() 
        {
            @Override
            public void componentShown(ComponentEvent e) {
                // Code to start music
                musicPlayer.playMusic("src/com/scanlinearcade/assets/music/gameover.wav"); 
            }
        });
    }

    private void showGameOver(String resultText, int score, String runToken)
    {
        pausePanel.setVisible(false);
        gameOverPanel.showResult(resultText, score, runToken);
    }

    private void resumeFromPause()
    {
        pausePanel.setVisible(false);
        startGameLoop();
        panel.requestFocusInWindow();
    }

    private void restartFromPause()
    {
        resetGame();
        pausePanel.setVisible(false);
        startGameLoop();
        panel.requestFocusInWindow();
    }

    private void returnToMenuFromPause()
    {
        pausePanel.setVisible(false);
        stopGameLoop();
        resetGame();
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

    private void restartFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.resetGame();
        panel.showFirstEntryInstructionsIfPending();
        panel.startGameLoop();
        panel.requestFocusInWindow();
        musicPlayer.playMusic("src/com/scanlinearcade/assets/music/breakout.wav"); 
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
          gameOverPanel.setVisible(false);
          panel.resetGame();
    }

    @Override
    public void startGameLoop()
    {
        gameOverPanel.setVisible(false);
        panel.showFirstEntryInstructionsIfPending();

        panel.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}