/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 *
 * @author RayCa
 */
public class PausePanel extends JPanel implements ActionListener{
   
    final private JButton resumeButton, restartButton, menuButton;
    //add actionlistener to each button

    public PausePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBounds(0,0,358,350);
        
        /*JPanel redPanel = new JPanel();
        redPanel.setBackground(Color.red);*/

        JLabel title = new JLabel("PAUSED");
        title.setFont(new Font("Monospaced", Font.BOLD, 48));
        title.setForeground(Color.white);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        resumeButton = new JButton("Resume");
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.setMaximumSize(new Dimension(300,100));
        
        
        restartButton = new JButton("Restart");
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuButton = new JButton("Quit");
        menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // spacing
        add(Box.createVerticalGlue()); // pushes everything to center
        add(title);
        add(Box.createVerticalStrut(20));
        add(resumeButton);
        add(Box.createVerticalStrut(10));
        add(restartButton);
        add(Box.createVerticalStrut(10));
        add(menuButton);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dark transparent background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}