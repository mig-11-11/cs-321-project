/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import static javax.swing.Box.createRigidArea;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 *
 * @author RayCa
 */
public class SettingsPanel extends JPanel
{
    private static final String SOUND_CARD = "SOUND";
    private static final String GAMEPLAY_CARD = "GAMEPLAY";
    private static final String DISPLAY_CARD = "DISPLAY";
            
    private final GameSettings settings;
    private final CardLayout settingsCardLayout;
    private final JPanel settingsCards;
    
    public SettingsPanel(GameSettings settings, Runnable returnToHub)
    {
        this.settings = settings;
        settingsCardLayout = new CardLayout();
        settingsCards = new JPanel(settingsCardLayout);
        
        setSettingsLayout(returnToHub);
        
        JPanel soundMenu = createCenterPanel(SOUND_CARD, createSoundMenu());
        JPanel gameplayMenu = createCenterPanel(GAMEPLAY_CARD, createGameplayMenu());
        JPanel displayMenu = createCenterPanel(DISPLAY_CARD, createDisplayMenu());
        
        
        settingsCards.add(soundMenu, SOUND_CARD);
        settingsCards.add(gameplayMenu, GAMEPLAY_CARD);
        settingsCards.add(displayMenu, DISPLAY_CARD);
        
        this.add(settingsCards, BorderLayout.CENTER);  
    }
    
    private void setSettingsLayout(Runnable returnToHub)
    {
        //Setup of SettingsPanel
        
        //Sets Title Bar
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.black);
        
        JLabel titleLabel = new JLabel("SETTINGS", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 255, 200));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 50));
        titleBar.add(titleLabel, BorderLayout.CENTER);
        titleBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        this.add(titleBar, BorderLayout.NORTH);
        
        //---------------------------------------
        // Sets bar at bottom for buttons
        JPanel buttonBar = new JPanel();
        buttonBar.setBackground(Color.black);
        
        JButton sound = new JButton("Sound");
        sound.addActionListener(e -> settingsCardLayout.show(settingsCards, SOUND_CARD));
        JButton gameplay = new JButton("Gameplay");
        gameplay.addActionListener(e -> settingsCardLayout.show(settingsCards, GAMEPLAY_CARD));
        JButton display = new JButton("Display");
        display.addActionListener(e -> settingsCardLayout.show(settingsCards, DISPLAY_CARD));
        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> returnToHub.run());
        
        styleButton(sound);
        styleButton(gameplay);
        styleButton(display);
        styleButton(back);
        
        buttonBar.add(sound);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(gameplay);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(display);
        buttonBar.add(Box.createHorizontalStrut(20));
        buttonBar.add(back);
        buttonBar.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        this.add(buttonBar, BorderLayout.SOUTH);
        //---------------------------------------------
    }
    
    private void styleButton(JButton button)
    {
        button.setFont(new Font("Monospaced", Font.BOLD, 18));
        button.setBackground(Color.black);
        button.setForeground(new Color(0, 255, 200));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(235, 236, 240), 2));
        button.setPreferredSize(new Dimension(160, 50));
        button.setMaximumSize(new Dimension(100, 100));
    }
    
    private JPanel createCenterPanel(String title, JPanel currentMenu)
    {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.black);
        
        JPanel subtitleBar = new JPanel();
        subtitleBar.setBackground(Color.black);
        subtitleBar.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, new Color(235, 236, 240)));
        
        JLabel subtitleLabel = new JLabel(title);
        subtitleLabel.setForeground(new Color(235, 236, 240));
        subtitleLabel.setFont(new Font("Monospaced", Font.BOLD, 38));
        subtitleBar.add(subtitleLabel, BorderLayout.CENTER);
        centerPanel.add(subtitleBar, BorderLayout.NORTH);
        
        centerPanel.add(currentMenu, BorderLayout.CENTER);
        return centerPanel;
    }
    
    private JPanel createSoundMenu() 
    {
        JPanel soundPanel = new JPanel();
        soundPanel.setLayout(new BoxLayout(soundPanel, BoxLayout.Y_AXIS));
        soundPanel.setBackground(Color.black);

        soundPanel.add(createRigidArea(new Dimension(0, 80)));

        // Inner box panel
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(33, 33, 33));
        box.setMaximumSize(new Dimension(1000, 230));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Border
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 236, 240), 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40) 
        ));

        // Volume Label
        JLabel volumeLabel = new JLabel("VOLUME:");
        volumeLabel.setForeground(new Color(235, 236, 240));
        volumeLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(volumeLabel);
        box.add(createRigidArea(new Dimension(0, 25)));

        // Value label
        JLabel valueLabel = new JLabel("65%");
        valueLabel.setForeground(Color.white);
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(valueLabel);
        box.add(createRigidArea(new Dimension(0, 20)));

        // Slider
        JSlider volumeSlider = new JSlider(0, 100, 65);
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeSlider.setMaximumSize(new Dimension(400, 40));
        volumeSlider.setOpaque(false);

        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            valueLabel.setText(value + "%");
            settings.setVolume(value / 100.0f);
        });

        box.add(volumeSlider);

        // Add box into main panel
        soundPanel.add(box);

        return soundPanel;
    }
    
    private JPanel createGameplayMenu()
    {
        JPanel gameplayPanel = new JPanel();
        gameplayPanel.setLayout(new BoxLayout(gameplayPanel, BoxLayout.Y_AXIS));
        gameplayPanel.setBackground(Color.black);
        
        gameplayPanel.add(createRigidArea(new Dimension(0, 80)));

        // Inner box panel
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(33, 33, 33));
        box.setMaximumSize(new Dimension(1000, 500));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Border
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 236, 240), 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40) 
        ));
        
        // Set Snake Difficulty Option
        JLabel snakeLabel = new JLabel("SNAKE DIFFICULTY:");
        snakeLabel.setForeground(new Color(235, 236, 240));
        snakeLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        snakeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String[] difficulties = {"EASY", "NORMAL", "HARD"};
        JComboBox<String> snakeDifficultyBox = new JComboBox<>(difficulties);
        styleComboBox(snakeDifficultyBox, 0);
        snakeDifficultyBox.addActionListener(e -> {
            settings.setDifficulty((String) snakeDifficultyBox.getSelectedItem(), 0);
        });
        
        // Set Space Invaders Difficulty Option
        JLabel invadersLabel = new JLabel("SPACE INVADERS DIFFICULTY:");
        invadersLabel.setForeground(new Color(235, 236, 240));
        invadersLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        invadersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JComboBox<String> invadersDifficultyBox = new JComboBox<>(difficulties);
        styleComboBox(invadersDifficultyBox, 1);
        invadersDifficultyBox.addActionListener(e -> {
            settings.setDifficulty((String) invadersDifficultyBox.getSelectedItem(), 1);
        });
        
        // Set Breakout Difficulty Option
        JLabel breakoutLabel = new JLabel("BREAK OUT DIFFICULTY:");
        breakoutLabel.setForeground(new Color(235, 236, 240));
        breakoutLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        breakoutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JComboBox<String> breakoutDifficultyBox = new JComboBox<>(difficulties);
        styleComboBox(breakoutDifficultyBox, 2);
        breakoutDifficultyBox.addActionListener(e -> {
            settings.setDifficulty((String) breakoutDifficultyBox.getSelectedItem(), 2);
        });

        box.add(snakeLabel);
        box.add(createRigidArea(new Dimension(0, 20)));
        box.add(snakeDifficultyBox);
        box.add(createRigidArea(new Dimension(0, 60)));
        box.add(invadersLabel);
        box.add(createRigidArea(new Dimension(0, 20)));
        box.add(invadersDifficultyBox);
        box.add(createRigidArea(new Dimension(0, 60)));
        box.add(breakoutLabel);
        box.add(createRigidArea(new Dimension(0, 20)));
        box.add(breakoutDifficultyBox);
        box.add(createRigidArea(new Dimension(0, 60)));
        
        gameplayPanel.add(box);

        return gameplayPanel;
    }
    
    private JComboBox styleComboBox(JComboBox comboBox, int game)
    {
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(200, 30));
        comboBox.setSelectedItem(settings.getDifficulty(game));
        comboBox.setFont(new Font("Monospaced", Font.BOLD, 20));
        return comboBox;
    }
    
    private JPanel createDisplayMenu()
    {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(Color.black);
        
        displayPanel.add(createRigidArea(new Dimension(0, 80)));
        
        // Inner box panel
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(33, 33, 33));
        box.setMaximumSize(new Dimension(1000, 500));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Border
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 236, 240), 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40) 
        ));
        
        //Color picker
        JLabel displayLabel = new JLabel("BACKGROUND COLOR:");
        displayLabel.setForeground(new Color(235, 236, 240));
        displayLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        displayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel colorLabel = new JLabel("Click a button!");
        colorLabel.setForeground(Color.white);
        colorLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        colorLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JPanel colorPanel = new JPanel(new GridLayout(3,3,5,5));
        colorPanel.setBackground(new Color(33, 33, 33));
        
        enum PresetColor {
        BLACK(Color.BLACK, "BLACK"), BLUE(Color.BLUE, "BLUE"), CYAN(Color.CYAN, "CYAN"),
        DARK_GRAY(Color.DARK_GRAY, "DARK GRAY"), GRAY(Color.GRAY, "GRAY"), GREEN(Color.GREEN, "GREEN"),
        LIGHT_GRAY(Color.LIGHT_GRAY, "LIGHT GRAY"), MAGENTA(Color.MAGENTA, "MAGENTA"), ORANGE(Color.ORANGE, "ORANGE");

        final Color color;
        final String name;
        PresetColor(Color color, String name) { this.color = color; this.name = name; }
        }
        
        for (PresetColor pc : PresetColor.values()) {
            JButton button = new JButton();
            button.setBackground(pc.color);
            button.addActionListener(e -> {
                colorLabel.setText("Selected: " + pc.name);
                settings.setDisplayColor(pc.color);
            });
            colorPanel.add(button);
        }
        
        box.add(displayLabel);
        box.add(createRigidArea(new Dimension(0, 20)));
        box.add(colorPanel);
        box.add(createRigidArea(new Dimension(0, 20)));
        box.add(colorLabel);
        
        displayPanel.add(box);
        
        return displayPanel;
    }
    
    
    
}
