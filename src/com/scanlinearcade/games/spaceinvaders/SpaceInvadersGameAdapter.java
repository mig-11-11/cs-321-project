package com.scanlinearcade.games.spaceinvaders;

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

/**
 * Adapter Class: SpaceInvadersGameAdapter
 *
 * <p>Intent: Adapts the {@code Board} game implementation to the {@code ArcadeGame} interface,
 * integrating pause/resume, game over handling, and layered UI components (pause panel, game over panel).
 * Manages the overall game flow including music playback callbacks.
 */
public class SpaceInvadersGameAdapter implements ArcadeGame
{
    private final Board panel;
    private final JLayeredPane layeredPane;
    private final PausePanel pausePanel;
    private final GameOverPanel gameOverPanel;
    private final Runnable onExitToMenu;
    private final GameSettings settings;
    private final MusicPlayer musicPlayer;
    
    /**
     * Creates a fully integrated Space Invaders game with UI layering and music support.
     *
     * @param settings game settings for difficulty and display options
     * @param musicPlayer music player for sound effects and background music
     * @param onExitToMenu callback to execute when returning to the main menu
     */
    public SpaceInvadersGameAdapter(GameSettings settings, MusicPlayer musicPlayer, Runnable onExitToMenu)
    {
        this.settings = settings;
        this.musicPlayer = musicPlayer;
        this.onExitToMenu = onExitToMenu;

        panel = new Board(onExitToMenu, this::showGameOver, this.settings);

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
            "spaceinvaders",
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
        
        addMusicListener();
    }

    /**
     * Resumes the gameplay from pause menu. Hides the pause menu and continues the gameplay.
     */
    private void resumeFromPause()
    {
        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    /**
     * Restarts the gameplay from pause menu. Hides the pause menu and restarts the gameplay.
     */
    private void restartFromPause()
    {
        panel.resetGame();
        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    /**
     * Returns back to main menu from pause.
     */
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

    /**
     * Hides pause menu and displays instructions screen.
     */
    private void showInstructionsFromPause()
    {
        pausePanel.setVisible(false);
        panel.showInstructionsCard();
        panel.requestFocusInWindow();
    }

    /**
     * Displays the game over panel with the result and score.
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
     * Restarts the game from the game over screen.
     */
    private void restartFromGameOver()
    {
        gameOverPanel.setVisible(false);
        panel.resetGame();
        panel.showFirstEntryInstructionsIfPending();
        panel.startGameLoop();
        panel.requestFocusInWindow();
        musicPlayer.playMusic("/com/scanlinearcade/assets/music/spaceinvaders.wav"); 
    }

    /**
     * Returns to the main menu from the game over screen.
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
     * Configures the pause key (Escape) to toggle pause menu.
     */
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
    
    /**
     * Adds a listener to play music whenever the game over panel is displayed
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
     * Returns the unique card name for this game in the arcade.
    
     * @return card identifier "invaders"
     */
    @Override
    public String getCardName()
    {
        return "invaders";
    }

    /**
     * Returns the display title for this game.
     
     * @return display title "Space Invaders"
     */
    @Override
    public String getDisplayTitle()
    {
        return "Space Invaders";
    }

    /**
     * Returns the root UI component for rendering the game.
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
     */
    @Override
    public void resetGame()
    {
        gameOverPanel.setVisible(false);
        panel.resetGame();
        pausePanel.setVisible(false);
    }

    /**
     * Starts the game loop and displays first-entry instructions if applicable.
     */
    @Override
    public void startGameLoop()
    {
        gameOverPanel.setVisible(false);
        panel.showFirstEntryInstructionsIfPending();

        pausePanel.setVisible(false);
        panel.startGameLoop();
        panel.requestFocusInWindow();
    }

    /**
     * Stops the game loop.
     */
    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}