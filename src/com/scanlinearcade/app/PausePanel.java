/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author RayCa
 */
public class PausePanel extends JPanel
{
    public PausePanel(Runnable onResume, Runnable onRestart, Runnable onMainMenu)
    {
        setLayout(new GridBagLayout());
        setFocusable(true);

        // 👇 THIS is what makes it an overlay
        setBackground(new Color(0, 0, 0, 170)); // semi-transparent black
        setOpaque(true);

        JPanel box = new JPanel(new GridLayout(3, 1, 10, 10));
        box.setBackground(new Color(20, 20, 30));
        box.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton resume = new JButton("Resume");
        JButton restart = new JButton("Restart");
        JButton menu = new JButton("Main Menu");

        // Keep overlay keyboard handling deterministic: SPACE/ESC always resume.
        resume.setFocusable(false);
        restart.setFocusable(false);
        menu.setFocusable(false);

        resume.addActionListener(e -> onResume.run());
        restart.addActionListener(e -> onRestart.run());
        menu.addActionListener(e -> onMainMenu.run());

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "resume");
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "resume");
        actionMap.put("resume", new AbstractAction()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                onResume.run();
            }
        });

        box.add(resume);
        box.add(restart);
        box.add(menu);

        add(box);
    }
}