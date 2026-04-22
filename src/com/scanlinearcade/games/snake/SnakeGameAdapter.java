/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.snake;

import com.scanlinearcade.app.ArcadeGame;
import com.scanlinearcade.app.GameOverPanel;
import com.scanlinearcade.app.GameSettings;
import com.scanlinearcade.app.MusicPlayer;
import com.scanlinearcade.app.PausePanel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.HierarchyEvent;

public class SnakeGameAdapter implements ArcadeGame
{
    private final SnakePanel panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final GameOverPanel gameOverPanel;
    private final Runnable returnToHubAction;
    private final GameSettings settings;
    private final MusicPlayer musicPlayer;

    public SnakeGameAdapter(GameSettings settings, MusicPlayer musicPlayer, Runnable returnToHubAction)
    {
        this.settings = settings;
        this.musicPlayer = musicPlayer;
        this.returnToHubAction = returnToHubAction;
        

        panel = new SnakePanel(returnToHubAction, this::showGameOver, settings);

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
            "snake",
            this::restartFromGameOver,
            this::returnToMenuFromGameOver
        );

        pausePanel.setVisible(false);

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter()
        {
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
        
        addMusicListener();
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

        panel.showFirstEntryInstructionsIfPending();

        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    private void returnToMenuFromPause()
    {
        pausePanel.setVisible(false);
        panel.stopGameLoop();
        panel.resetGame();

        if (returnToHubAction != null)
        {
            returnToHubAction.run();
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
        musicPlayer.playMusic("/com/scanlinearcade/assets/music/snake.wav");
    }

    private void returnToMenuFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.stopGameLoop();
        panel.resetGame();

        if (returnToHubAction != null)
        {
            returnToHubAction.run();
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
        });
    }
    
    private void addMusicListener()
    {
        
        gameOverPanel.addComponentListener(new ComponentAdapter() 
        {
            @Override
            public void componentShown(ComponentEvent e) {
                // Code to start music
                musicPlayer.playMusic("/com/scanlinearcade/assets/music/gameover.wav"); 
            }
        });
    }

    @Override
    public String getCardName()
    {
        return "snake";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Snake";
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
        gameOverPanel.setVisible(false);
        panel.showFirstEntryInstructionsIfPending();

        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}