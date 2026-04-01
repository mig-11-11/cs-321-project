/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import java.awt.EventQueue;
import javax.swing.JFrame;

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

        add(new Board());

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