//*****************************************************************************************************
// 
// Program Title: GameSettings.java
// Project File: App
// Name: Justin Campbell
// Course Section: CS321-01 
// Date (MM/YYYY): 04/2026
//
//*****************************************************************************************************
package com.scanlinearcade.app;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Configuration Service Class: GameSettings
 *
 * <p>Intent: Manages global game configuration including volume, difficulty levels per game,
 * and display color settings. Supports property change notifications for volume updates,
 * allowing components to react to setting changes in real-time.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public float getVolume()}</li>
 *   <li>{@code public void setVolume(float newVolume)}</li>
 *   <li>{@code public void addPropertyChangeListener(PropertyChangeListener listener)}</li>
 *   <li>{@code public String getDifficulty(int selection)}</li>
 *   <li>{@code public double getDifficultyScale(int game)}</li>
 *   <li>{@code public void setDifficulty(String difficulty, int game)}</li>
 *   <li>{@code public Color getDisplayColor()}</li>
 *   <li>{@code public void setDisplayColor(Color color)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class GameSettings {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private float volume = 0.65f; // 0.0 -> 1.0
    
    private Color displayColor = Color.black;
    
    private final String[] gameDifficulties = {"NORMAL", "NORMAL", "NORMAL"};

    /**
     * Returns the current volume setting.
     * Signature: {@code public float getVolume()}
     *
     * @return volume level (0.0 = silent, 1.0 = maximum)
     */
    public float getVolume() 
    {
        return volume;
    }

    /**
     * Sets the volume and notifies all registered property change listeners.
     * Signature: {@code public void setVolume(float newVolume)}
     *
     * @param newVolume new volume level (0.0 = silent, 1.0 = maximum)
     */
    public void setVolume(float newVolume) 
    {
        float oldVolume = this.volume;
        this.volume = newVolume;
        pcs.firePropertyChange("volume", oldVolume, newVolume);
    }
    
    /**
     * Registers a property change listener to be notified of setting changes.
     * Signature: {@code public void addPropertyChangeListener(PropertyChangeListener listener)}
     *
     * @param listener listener to add for property change notifications
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Returns the difficulty setting for a specific game.
     * Signature: {@code public String getDifficulty(int selection)}
     *
     * @param selection game index (0 = first game, 1 = second game, etc.)
     * @return difficulty level string (e.g., "EASY", "NORMAL", "HARD")
     */
    public String getDifficulty(int selection) 
    {
        return gameDifficulties[selection];
    }
    
    /**
     * Returns a difficulty multiplier for game speed/challenge scaling.
     * Signature: {@code public double getDifficultyScale(int game)}
     *
     * @param game game index (0 = first game, 1 = second game, etc.)
     * @return multiplier for difficulty (0.5 for EASY, 1.0 for NORMAL, 2.0 for HARD)
     */
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

    /**
     * Sets the difficulty level for a specific game.
     * Signature: {@code public void setDifficulty(String difficulty, int game)}
     *
     * @param difficulty difficulty level string (e.g., "EASY", "NORMAL", "HARD")
     * @param game game index (0 = first game, 1 = second game, etc.)
     */
    public void setDifficulty(String difficulty, int game) 
    {
        gameDifficulties[game] = difficulty;
    }
    
    /**
     * Returns the current display background color.
     * Signature: {@code public Color getDisplayColor()}
     *
     * @return background color for display
     */
    public Color getDisplayColor()
    {
        return displayColor;
    }
    
    /**
     * Sets the display background color.
     * Signature: {@code public void setDisplayColor(Color color)}
     *
     * @param color new background color for display
     */
    public void setDisplayColor(Color color)
    {
        displayColor = color;
    }
}
