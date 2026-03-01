//*****************************************************************************************************
// Program Title: Direction.java
// Project File: 
// Name: Braden Gant
// Course Section: CS321-01 
// Date: 02/03/2026
// Program Description:  
//
//*****************************************************************************************************

package com.scanlinearcade.games.snake;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SnakeDemo 
{
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            JFrame f = new JFrame("Snake Demo");
            SnakePanel panel = new SnakePanel();

            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(panel);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setVisible(true);

            // Important: ensure the panel receives key input
            panel.requestFocusInWindow();
        }
        );
    }
}
