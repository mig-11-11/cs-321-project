/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.scanlinearcade.app;
import javax.swing.SwingUtilities;

public class Main 
{
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new ArcadeFrame().setVisible(true));
    }
    
}
