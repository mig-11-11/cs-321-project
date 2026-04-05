/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author RayCa
 */
public class SettingsPanel extends JPanel
{
    private static final String SOUND_CARD = "Sound";
    private static final String GAMEPLAY_CARD = "Gameplay";
    private static final String DISPLAY_CARD = "Display";
            
    private final CardLayout settingsCardLayout;
    private final JPanel settingsCards;
    
    public SettingsPanel(Runnable returnToHub)
    {
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
        
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
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
        
        buttonBar.add(sound);
        buttonBar.add(gameplay);
        buttonBar.add(display);
        buttonBar.add(back);
        buttonBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.lightGray, 5),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        this.add(buttonBar, BorderLayout.SOUTH);
        //---------------------------------------------
    }
    
    private JPanel createCenterPanel(String title, JPanel currentMenu)
    {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.black);
        
        JPanel subtitleBar = new JPanel();
        subtitleBar.setBackground(Color.black);
        subtitleBar.setBorder(BorderFactory.createMatteBorder(5, 0, 5, 0, Color.lightGray));
        
        JLabel subtitleLabel = new JLabel(title);
        subtitleLabel.setForeground(Color.lightGray);
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
        return soundPanel;
    }
    
    private JPanel createGameplayMenu()
    {
        JPanel gameplayPanel = new JPanel();
        gameplayPanel.setLayout(new BoxLayout(gameplayPanel, BoxLayout.Y_AXIS));
        gameplayPanel.setBackground(Color.black);
        return gameplayPanel;
    }
    
    private JPanel createDisplayMenu()
    {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(Color.black);
        return displayPanel;
    }
    
}
