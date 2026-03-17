package com.scanlinearcade.app;

import com.scanlinearcade.games.snake.SnakePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.CardLayout;


public class ArcadeFrame extends JFrame
{
    private static final String MENU_CARD = "menu";
    private static final String SNAKE_CARD = "snake";
    private static final String BREAKOUT_CARD = "breakout";
    private static final String INVADERS_CARD = "invaders";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final SnakePanel snakePanel = new SnakePanel();

    
    public ArcadeFrame()
    {
        setTitle("Scanline Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        MenuPanel menuPanel = new MenuPanel(
            this::showSnake,
            this::showBreakout,
            this::showInvaders
        );

        JPanel snakeScreen = createGameScreen(
            "Snake",
            snakePanel,
            () ->
            {
                snakePanel.stopGameLoop();
                snakePanel.resetGame();
                showMenu();
            }
        );

        JPanel breakoutScreen = createGameScreen(
            "Breakout",
            createPlaceholderPanel("Breakout", "Breakout will be plugged in here."),
            this::showMenu
        );

        JPanel invadersScreen = createGameScreen(
            "Space Invaders",
            createPlaceholderPanel("Space Invaders", "Space Invaders will be plugged in here."),
            this::showMenu
        );

        cards.add(menuPanel, MENU_CARD);
        cards.add(snakeScreen, SNAKE_CARD);
        cards.add(breakoutScreen, BREAKOUT_CARD);
        cards.add(invadersScreen, INVADERS_CARD);

        setContentPane(cards);
        pack();
        setLocationRelativeTo(null);

        // Prevent Snake from running in the background while on the hub menu
        snakePanel.stopGameLoop();

        showMenu();
    }

    private JPanel createGameScreen(String title, JComponent content, Runnable onReturn)
    {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setBackground(Color.BLACK);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 15, 25));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton returnButton = new JButton("Return to Hub");
        returnButton.setFocusable(false);
        returnButton.setBackground(Color.BLACK);
        returnButton.setForeground(new Color(255, 220, 80));
        returnButton.setBorder(BorderFactory.createLineBorder(new Color(255, 220, 80), 2));
        returnButton.addActionListener(e -> onReturn.run());

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 255, 200));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));

        topBar.add(returnButton, BorderLayout.WEST);
        topBar.add(titleLabel, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.BLACK);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerWrapper.add(content, BorderLayout.CENTER);

        screen.add(topBar, BorderLayout.NORTH);
        screen.add(centerWrapper, BorderLayout.CENTER);

        bindEscapeToReturn(screen, onReturn);

        return screen;
    }

    private JPanel createPlaceholderPanel(String gameName, String message)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(18, 18, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 200), 3));

        JLabel label = new JLabel(
            "<html><div style='text-align:center;'>" +
            "<h1 style='color:#00FFC8; font-family:monospace;'>" + gameName + "</h1>" +
            "<p style='color:white; font-family:monospace;'>" + message + "</p>" +
            "</div></html>",
            SwingConstants.CENTER
        );

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void bindEscapeToReturn(JComponent component, Runnable onReturn)
    {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ESCAPE"),
            "returnToMenu"
        );

        component.getActionMap().put(
            "returnToMenu",
            new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    onReturn.run();
                }
            }
        );
    }

    private void showMenu()
    {
        cardLayout.show(cards, MENU_CARD);
        repaint();
    }

    private void showSnake()
    {
        
        cardLayout.show(cards, SNAKE_CARD);
        snakePanel.resetGame();
        snakePanel.startGameLoop();
        snakePanel.requestFocusInWindow();
    }

    private void showBreakout()
    {
        cardLayout.show(cards, BREAKOUT_CARD);
    }

    private void showInvaders()
    {
        cardLayout.show(cards, INVADERS_CARD);
    }
}