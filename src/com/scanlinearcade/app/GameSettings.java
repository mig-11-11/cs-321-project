
/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.scanlinearcade.app;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author RayCa
 */
public class GameSettings {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private float volume = 0.65f; // 0.0 -> 1.0
    
    private Color displayColor = Color.black;
    
    private final String[] gameDifficulties = {"NORMAL", "NORMAL", "NORMAL"};

    public float getVolume() 
    {
        return volume;
    }

    public void setVolume(float newVolume) 
    {
        float oldVolume = this.volume;
        this.volume = newVolume;
        pcs.firePropertyChange("volume", oldVolume, newVolume);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.addPropertyChangeListener(listener);
    }

    public String getDifficulty(int selection) 
    {
        return gameDifficulties[selection];
    }
    
    public double getDifficultyScale(int game)
    {
        switch (gameDifficulties[game]) {
            case "EASY":
                return 0.5;
            case "MEDIUM":
                return 1;
            case "HARD":
                return 2;
            default:
                break;
        }
        
        return 1;
    }

    public void setDifficulty(String difficulty, int game) 
    {
        gameDifficulties[game] = difficulty;
    }
    
    public Color getDisplayColor()
    {
        return displayColor;
    }
    
    public void setDisplayColor(Color color)
    {
        displayColor = color;
    }
}
