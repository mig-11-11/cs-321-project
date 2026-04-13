/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 *
 * @author RayCa
 */
public class MusicPlayer 
{
    private Clip clip;
    private float currentVolume = 0.65f;
    
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

    public void playMusic(String musicLocation) 
    {
        try 
        {
            if (clip != null && clip.isRunning()) 
            {
                clip.stop();
                clip.setFramePosition(0);
            }
            
            File musicPath = new File(musicLocation);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            setVolume(currentVolume);
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start();
            
        } catch (Exception e) {
        }
    }

    public void setVolume(float volume) // 0.0 → 1.0
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