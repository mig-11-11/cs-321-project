/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.games.breakout;

import com.scanlinearcade.app.ArcadeGame;
import javax.swing.JComponent;

public class BreakoutGameAdapter implements ArcadeGame
{
    private final BreakPanel panel;
    private boolean firstEntryInstructionsPending = true;

    public BreakoutGameAdapter(Runnable returnToHubAction)
    {
        panel = new BreakPanel(returnToHubAction);
    }

    @Override
    public String getCardName()
    {
        return "breakout";
    }

    @Override
    public String getDisplayTitle()
    {
        return "Breakout";
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
        if (firstEntryInstructionsPending)
        {
            panel.showInstructionsCard();
            firstEntryInstructionsPending = false;
        }

        panel.startGameLoop();
    }

    @Override
    public void stopGameLoop()
    {
        panel.stopGameLoop();
    }
}
