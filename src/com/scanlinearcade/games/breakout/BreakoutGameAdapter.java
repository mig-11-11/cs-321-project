//*****************************************************************************************************
// 
// Program Title: BreakoutGameAdapter.java
// Project File: Breakout
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 03/2026
//
//*****************************************************************************************************
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

/**
 * Adapter Class: BreakoutGameAdapter
 *
 * <p>Intent: Adapts the {@code BreakPanel} game implementation to the {@code ArcadeGame} interface,
 * integrating pause/resume, game over handling, and layered UI components (pause panel, game over panel).
 * Manages the overall game flow including music playback callbacks.
 */
public class BreakoutGameAdapter implements ArcadeGame
{
    private final BreakPanel panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final GameOverPanel gameOverPanel;
    private final Runnable onExitToMenu;
    private final GameSettings settings;
    private final MusicPlayer musicPlayer;

    /**
     * Creates a fully integrated Breakout game with UI layering and music support.
     * Signature: {@code public BreakoutGameAdapter(GameSettings settings, MusicPlayer musicPlayer, Runnable onExitToMenu)}
     *
     * @param settings game settings for difficulty and display options
     * @param musicPlayer music player for sound effects and background music
     * @param onExitToMenu callback to execute when returning to the main menu
     */
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


/**
     * Configures the pause key (Escape) to toggle pause menu visibility.
     * Signature: {@code private void setupPauseKey()}
     */
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
        
    /**
     * Adds a listener to play music when the game over panel is shown.
     * Signature: {@code private void addMusicListener()}
     */
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

    /**
     * Displays the game over panel with the result and score.
     * Signature: {@code private void showGameOver(String resultText, int score, String runToken)}
     *
     * @param resultText message to display (e.g., "You Win!" or "Game Over!")
     * @param score final score achieved
     * @param runToken unique identifier for this game session
     */
    private void showGameOver(String resultText, int score, String runToken)
    {
        pausePanel.setVisible(false);
        gameOverPanel.showResult(resultText, score, runToken);
    }

    /**
     * Resumes gameplay from the pause menu.
     * Signature: {@code private void resumeFromPause()}
     */
    private void resumeFromPause()
    {
        pausePanel.setVisible(false);
        startGameLoop();
        panel.requestFocusInWindow();
    }

    /**
     * Restarts the game from the pause menu.
     * Signature: {@code private void restartFromPause()}
     */
    private void restartFromPause()
    {
        resetGame();
        pausePanel.setVisible(false);
        startGameLoop();
        panel.requestFocusInWindow();
    }

    /**
     * Returns to the main menu from the pause menu.
     * Signature: {@code private void returnToMenuFromPause()}
     */
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

    /**
     * Shows the instructions card from the pause menu.
     * Signature: {@code private void showInstructionsFromPause()}
     */
    private void showInstructionsFromPause()
    {
        pausePanel.setVisible(false);
        panel.showInstructionsCard();
        panel.requestFocusInWindow();
    }

    /**
     * Restarts the game from the game over screen.
     * Signature: {@code private void restartFromGameOver()}
     */
    private void restartFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.resetGame();
        panel.showFirstEntryInstructionsIfPending();
        panel.startGameLoop();
        panel.requestFocusInWindow();
        musicPlayer.playMusic("/com/scanlinearcade/assets/music/breakout.wav"); 
    }

    /**
     * Returns to the main menu from the game over screen.
     * Signature: {@code private void returnToMenuFromGameOver()}
     */
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

    
    
    
    
    
    
    
    /**
     * Returns the unique card name for this game in the arcade.
     * Signature: {@code public String getCardName()}
     *
     * @return card identifier "breakout"
     */
    @Override
    public String getCardName()
    {
        return "breakout";
    }

    /**
     * Returns the display title for this game.
     * Signature: {@code public String getDisplayTitle()}
     *
     * @return display title "Breakout"
     */
    @Override
    public String getDisplayTitle()
    {
        return "Breakout";
    }

    /**
     * Returns the root UI component for rendering the game.
     * Signature: {@code public JComponent getView()}
     *
     * @return the layered pane containing game panel and overlay panels
     */
    @Override
    public JComponent getView()
    {
        return layeredPane;
    }

    /**
     * Resets the game state and hides all overlay panels.
     * Signature: {@code public void resetGame()}
     */
    @Override
    public void resetGame()
    {
          gameOverPanel.setVisible(false);
          panel.resetGame();
    }

    /**
     * Starts the game loop and displays first-entry instructions if applicable.
     * Signature: {@code public void startGameLoop()}
     */
    @Override
    public void startGameLoop()
    {
        gameOverPanel.setVisible(false);
        panel.showFirstEntryInstructionsIfPending();

        panel.startGameLoop();
    }

    /**
     * Stops the game loop.
     * Signature: {@code public void stopGameLoop()}
     */
    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}