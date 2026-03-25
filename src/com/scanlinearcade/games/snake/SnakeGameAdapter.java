/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.snake;

import com.scanlinearcade.app.ArcadeGame;
import javax.swing.JComponent;

public class SnakeGameAdapter implements ArcadeGame
{
    private final SnakePanel panel;

    public SnakeGameAdapter()
    {
        panel = new SnakePanel();
    }

    @Override
    public String getCardName()
    {
        return "snake";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Snake";
    }

    @Override
    public JComponent getView()
    {
        return panel;
    }

    @Override
    public void resetGame()
    {
        panel.resetGame();
    }

    @Override
    public void startGameLoop()
    {
        panel.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}