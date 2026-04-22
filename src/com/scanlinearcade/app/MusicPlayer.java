//*****************************************************************************************************
// 
// Program Title: MusicPlayer.java
// Project File: App
// Name: Justin Campbell
// Course Section: CS321-01 
// Date (MM/YYYY): 04/2026
//
//*****************************************************************************************************
package com.scanlinearcade.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Audio Service Class: MusicPlayer
 *
 * <p>Intent: Manages audio playback for the arcade including background music and sound effects.
 * Supports continuous looping, volume control with gamma curve adjustment, and integration
 * with {@code GameSettings} for real-time volume changes via property listeners.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public MusicPlayer(GameSettings settings)}</li>
 *   <li>{@code public void playMusic(String musicLocation)}</li>
 *   <li>{@code public void setVolume(float volume)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class MusicPlayer 
{
    private Clip clip;
    private float currentVolume = 0.65f;
    
    /**
     * Creates a music player and initializes volume from game settings.
     * Registers a property change listener to respond to volume setting changes.
     * Signature: {@code public MusicPlayer(GameSettings settings)}
     *
     * @param settings game settings containing initial volume and volume change listener support
     */
    public MusicPlayer(GameSettings settings)
    {
        this.currentVolume = settings.getVolume();
        
        settings.addPropertyChangeListener(evt -> {
            if ("volume".equals(evt.getPropertyName()))
            {
                currentVolume = (float) evt.getNewValue();
                setVolume(currentVolume);
            }
        });
    }

    /**
     * Loads and plays a music file with continuous looping.
     * If a clip is currently playing, stops it first before loading the new track.
     * Signature: {@code public void playMusic(String musicLocation)}
     *
     * @param musicLocation file path to the audio file to play
     */
    public void playMusic(String musicLocation) 
    {
        try 
        {
            if (clip != null && clip.isRunning()) 
            {
                clip.stop();
                clip.setFramePosition(0);
            }
            
            InputStream audioSrc = getClass().getResourceAsStream(musicLocation);

            if (audioSrc == null) {
                throw new IllegalArgumentException("Music file not found: " + musicLocation);
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
            
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            setVolume(currentVolume);
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start();
            
        } catch (Exception e) {
        }
    }

    /**
     * Adjusts the current clip volume using a gamma curve for perceptual scaling.
     * Signature: {@code public void setVolume(float volume)}
     *
     * @param volume desired volume level (0.0 = silent, 1.0 = maximum); scaled using gamma curve
     */
    public void setVolume(float volume)
    {
        if (clip != null)
        {
            try
            {
                FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();

                // Gamma curve
                float gamma = 0.5f; // < 1 boosts low-end
                float scaled = (float) Math.pow(volume, gamma);

                float dB = min + (max - min) * scaled;

                gainControl.setValue(dB);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}