package com.scanlinearcade.app;

import com.scanlinearcade.games.breakout.BreakoutGameAdapter;
import com.scanlinearcade.games.snake.SnakeGameAdapter;
import com.scanlinearcade.games.spaceinvaders.SpaceInvadersGameAdapter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class ArcadeFrame extends JFrame
{
    private static final String MENU_CARD = "menu";
    private static final String SCORES_CARD = "scores";
    private static final String SETTINGS_CARD = "settings";

    private final GameSettings settings = new GameSettings();
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final Map<String, ArcadeGame> games = new LinkedHashMap<>();
    
    private final HighScoresPanel highScoresPanel = new HighScoresPanel(this::showMenu);

    public ArcadeFrame()
    {
        setTitle("Scanline Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        registerGames();

        MenuPanel menuPanel = new MenuPanel(
            () -> showGame("snake"),
            () -> showGame("breakout"),
            () -> showGame("invaders"),
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
        
        String filepath = "src/com/scanlinearcade/assets/music/boogie-pecan-pie-main-version-41135-02-14.wav";
        PlayMusic(filepath);
    }

    private void registerGames()
    {
        games.put("snake", new SnakeGameAdapter(settings, () -> returnFromGame("snake")));
        games.put("breakout", new BreakoutGameAdapter(settings, () -> returnFromGame("breakout")));
        games.put("invaders", new SpaceInvadersGameAdapter(settings, () -> returnFromGame("invaders")));
    }

    private void showGame(String cardName)
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
        highScoresPanel.refreshScores();
        cardLayout.show(cards, SCORES_CARD);
    }

    private void showSettings()
    {
        cardLayout.show(cards, SETTINGS_CARD);
    }

    private JPanel createGameScreen(String title, JComponent content, Runnable onReturn)
    {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Color.BLACK);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 15, 25));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

       

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 255, 200));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));

       
        topBar.add(titleLabel, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.BLACK);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerWrapper.add(content, BorderLayout.CENTER);

        screen.add(topBar, BorderLayout.NORTH);
        screen.add(centerWrapper, BorderLayout.CENTER);

        return screen;
    }

    
    
    public static void PlayMusic(String location)
    {
        try
        {
            File musicPath = new File(location);
            
            if(musicPath.exists())
            {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else
            {
                System.out.println("Cant find file");
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
