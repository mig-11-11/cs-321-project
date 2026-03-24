/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.scanlinearcade.app;
import javax.swing.JComponent;


    public interface ArcadeGame
{
    String getCardName();
    String getDisplayTitle();
    JComponent getView();

    void resetGame();
    void startGameLoop();
    void stopGameLoop();
}

