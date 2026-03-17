/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.scanlinearcade.app;

import com.scanlinearcade.games.breakout.BreakPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
/**
 *
 * @author Braden
 */
public class ArcadeFrame extends JFrame
{
    private static final String MENU_CARD = "menu";
    private static final String BREAKOUT_CARD = "breakout";

    private final CardLayout cardLayout;
    private final JPanel cards;
    private JPanel breakoutHost;
    private BreakPanel breakoutPanel;

    public ArcadeFrame()
    {
        setTitle("Scanline Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(createMenuPanel(), MENU_CARD);
        cards.add(createBreakoutContainer(), BREAKOUT_CARD);

        setContentPane(cards);
        showMenu();
    }

    private JPanel createMenuPanel()
    {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(Color.BLACK);

        JLabel title = new JLabel("Scanline Arcade", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 44));
        title.setBorder(BorderFactory.createEmptyBorder(60, 20, 10, 20));

        JLabel subtitle = new JLabel("Main Menu", SwingConstants.CENTER);
        subtitle.setForeground(new Color(190, 190, 190));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.CENTER);
        top.add(subtitle, BorderLayout.SOUTH);

        JButton breakoutButton = new JButton("Play Breakout");
        breakoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        breakoutButton.setPreferredSize(new Dimension(220, 44));
        breakoutButton.addActionListener(e -> showBreakout());

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(220, 44));
        exitButton.addActionListener(e -> dispose());

        JPanel buttonColumn = new JPanel();
        buttonColumn.setOpaque(false);
        buttonColumn.setLayout(new BoxLayout(buttonColumn, BoxLayout.Y_AXIS));
        buttonColumn.add(Box.createVerticalGlue());
        buttonColumn.add(breakoutButton);
        buttonColumn.add(Box.createRigidArea(new Dimension(0, 16)));
        buttonColumn.add(exitButton);
        buttonColumn.add(Box.createVerticalGlue());

        menu.add(top, BorderLayout.NORTH);
        menu.add(buttonColumn, BorderLayout.CENTER);
        return menu;
    }

    private JPanel createBreakoutContainer()
    {
        JPanel breakoutCard = new JPanel(new BorderLayout());
        breakoutCard.setBackground(Color.DARK_GRAY);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> endBreakoutSessionAndShowMenu());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(backButton);

        breakoutHost = new JPanel(new GridBagLayout());
        breakoutHost.setBackground(Color.DARK_GRAY);

        breakoutCard.add(header, BorderLayout.NORTH);
        breakoutCard.add(breakoutHost, BorderLayout.CENTER);
        return breakoutCard;
    }

    public void showMenu()
    {
        cardLayout.show(cards, MENU_CARD);
    }

    private void endBreakoutSessionAndShowMenu()
    {
        if (breakoutPanel != null) {
            breakoutPanel.endGame();
        }
        breakoutHost.removeAll();
        breakoutHost.revalidate();
        breakoutHost.repaint();
        breakoutPanel = null;
        showMenu();
    }

    private void showBreakout()
    {
        breakoutHost.removeAll();
        breakoutPanel = new BreakPanel(this::endBreakoutSessionAndShowMenu);
        breakoutHost.add(breakoutPanel);
        breakoutHost.revalidate();
        breakoutHost.repaint();

        cardLayout.show(cards, BREAKOUT_CARD);
        SwingUtilities.invokeLater(() -> breakoutPanel.requestFocusInWindow());
    }
}

