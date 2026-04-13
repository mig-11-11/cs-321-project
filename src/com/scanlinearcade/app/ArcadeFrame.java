package com.scanlinearcade.app;

import com.scanlinearcade.games.breakout.BreakoutGameAdapter;
import com.scanlinearcade.games.snake.SnakeGameAdapter;
import com.scanlinearcade.games.spaceinvaders.SpaceInvadersGameAdapter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class ArcadeFrame extends JFrame
{
    private static final String MENU_CARD = "menu";
    private static final String SCORES_CARD = "scores";
    private static final String SETTINGS_CARD = "settings";
    
    private static final String INVADERS_MUSICPATH = "src/com/scanlinearcade/assets/music/spaceinvaders.wav";
    private static final String BREAKOUT_MUSICPATH = "src/com/scanlinearcade/assets/music/breakout.wav";
    private static final String SNAKE_MUSICPATH = "src/com/scanlinearcade/assets/music/snake.wav";

    private final GameSettings settings = new GameSettings();
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final Map<String, ArcadeGame> games = new LinkedHashMap<>();
    
    private final HighScoresPanel highScoresPanel = new HighScoresPanel(this::showMenu);
    
    private final MusicPlayer musicPlayer = new MusicPlayer(settings);
    private boolean inSettingsHighScore = false;

    public ArcadeFrame()
    {
        setTitle("Scanline Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        registerGames();

        MenuPanel menuPanel = new MenuPanel(
            () -> showGame("snake", SNAKE_MUSICPATH),
            () -> showGame("breakout", BREAKOUT_MUSICPATH),
            () -> showGame("invaders", INVADERS_MUSICPATH),
            this::showScores,
            this::showSettings
        );
        
        SettingsPanel settingsPanel = new SettingsPanel(
                settings,
                () -> showMenu()
        );

        cards.add(menuPanel, MENU_CARD);

        for (ArcadeGame game : games.values())
        {
            cards.add(
                createGameScreen(
                    game.getDisplayTitle(),
                    game.getView(),
                    () -> returnFromGame(game.getCardName())
                ),
                game.getCardName()
            );

            game.stopGameLoop();
        }

        cards.add(highScoresPanel, SCORES_CARD);
        cards.add(settingsPanel, SETTINGS_CARD);

        setContentPane(cards);
        pack();
        setLocationRelativeTo(null);

        showMenu();
        
        initialMusicPlay();
        addMusicListener(menuPanel);
    }

    private void registerGames()
    {
        games.put("snake", new SnakeGameAdapter(settings, musicPlayer, () -> returnFromGame("snake")));
        games.put("breakout", new BreakoutGameAdapter(settings, musicPlayer, () -> returnFromGame("breakout")));
        games.put("invaders", new SpaceInvadersGameAdapter(settings, musicPlayer, () -> returnFromGame("invaders")));
    }

    private void showGame(String cardName, String musicPath)
    {
        ArcadeGame game = games.get(cardName);

        if (game == null)
        {
            return;
        }

        game.resetGame();
        cardLayout.show(cards, cardName);
        game.startGameLoop();
        game.getView().requestFocusInWindow();
        
        musicPlayer.playMusic(musicPath);
    }

    private void returnFromGame(String cardName)
    {
        ArcadeGame game = games.get(cardName);

        if (game != null)
        {
            game.stopGameLoop();
            game.resetGame();
        }

        showMenu();
    }

    private void showMenu()
    {
        cardLayout.show(cards, MENU_CARD);
        repaint();
    }

    private void showScores()
    {
        inSettingsHighScore = true;
        highScoresPanel.refreshScores();
        cardLayout.show(cards, SCORES_CARD);
    }

    private void showSettings()
    {
        inSettingsHighScore = true;
        cardLayout.show(cards, SETTINGS_CARD);
    }

    private JPanel createGameScreen(String title, JComponent content, Runnable onReturn)
    {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Color.black);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 15, 25));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

       

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 255, 200));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));

       
        topBar.add(titleLabel, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(settings.getDisplayColor());  //settings gameplay selection
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerWrapper.add(content, BorderLayout.CENTER);

        screen.add(topBar, BorderLayout.NORTH);
        screen.add(centerWrapper, BorderLayout.CENTER);

        return screen;
    }
    
    private void initialMusicPlay()
    {
        musicPlayer.playMusic("src/com/scanlinearcade/assets/music/mainmenu.wav"); 
    }

    private void addMusicListener(MenuPanel menuPanel)
    {
        
        menuPanel.addComponentListener(new ComponentAdapter() 
        {
            @Override
            public void componentShown(ComponentEvent e) 
            {
                if(inSettingsHighScore == true) //so mainmenu.wav doesn't restart after leaving Settings or High Score menus
                {
                    inSettingsHighScore = false;
                }
                
                else
                {
                    // Code to start music
                    musicPlayer.playMusic("src/com/scanlinearcade/assets/music/mainmenu.wav");
                }
            }
        });
        
    }
}
