/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.spaceinvaders;

import com.scanlinearcade.app.ArcadeGame;
import javax.swing.JComponent;

public class SpaceInvadersGameAdapter implements ArcadeGame
{
    private final Board board;
    private boolean firstEntryInstructionsPending = true;

    public SpaceInvadersGameAdapter(Runnable returnToHubAction)
    {
        board = new Board(returnToHubAction);
    }

    @Override
    public String getCardName()
    {
        return "invaders";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Space Invaders";
    }

    @Override
    public JComponent getView()
    {
        return board;
    }

    @Override
    public void resetGame()
    {
        board.resetGame();
    }

    @Override
    public void startGameLoop()
    {
        if (firstEntryInstructionsPending)
        {
            board.showInstructionsCard();
            firstEntryInstructionsPending = false;
        }

        board.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        board.stopGameLoop();
    }
}
