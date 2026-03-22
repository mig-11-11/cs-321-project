/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author RayCa
 */
public class SpaceInvaders extends JFrame  {

    /**
     * runs initUI
     */
    public SpaceInvaders() {

        initUI();
    }

    /**
     * sets board and runs game for user
     */
    private void initUI() {
        
        Dimension d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(d);
        
        new Board(layeredPane);
        
        setContentPane(layeredPane);
        
        setTitle("Space Invaders");
        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new SpaceInvaders();
            ex.setVisible(true);
        });
    }
}
