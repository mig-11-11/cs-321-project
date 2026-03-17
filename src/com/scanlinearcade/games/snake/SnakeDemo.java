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
/**
 * Standalone launcher used to run Snake without the full Scanline Arcade hub.
 * This supports rapid development/testing of the Snake module.
 *
 * <h2>API Outline (public)</h2>
 * <pre>
 * public static void main(String[] args)
 * </pre>
 */

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SnakeDemo 
{

    public static void main(String[] args) 
    {
     /**
     * Program entry point. Creates a Swing window, installs {@link SnakePanel},
     * and makes it visible.
     *
     * <pre>
     * public static void main(String[] args)
     * </pre>
     *
     * @param args command-line arguments (unused)
     */
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

            // Important: ensuress the panel receives key input
            panel.requestFocusInWindow();
        }
        );
    }
}
