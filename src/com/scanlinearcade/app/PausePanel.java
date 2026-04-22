/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.scanlinearcade.app;

import javax.swing.*;
import java.awt.*;

/**
 * UI Overlay Component: PausePanel
 * 
 * <p>Intent: Provides a reusable pause menu overlay for arcade games.
 * Displays options to resume gameplay, restart the current game, view instructions,
 * or return to the main menu. Designed to appear as a semi-transparent overlay
 * on top of the active game view.
 * 
 */
public class PausePanel extends JPanel
{
    /**
     * Constructs a PausePanel with callback actions for each menu option
     * 
     * @param onResume Action executed when the resume button is pressed.
     * @param onRestart Action executed when the restart button is pressed.
     * @param onInstructions Action executed when the instructions button is pressed.
     * @param onMainMenu Action executed when the main menu button is pressed.
     */
    public PausePanel(Runnable onResume, Runnable onRestart, Runnable onInstructions, Runnable onMainMenu)
    {
        setLayout(new GridBagLayout());

        // THIS is what makes it an overlay
        setBackground(new Color(0, 0, 0, 115)); // softer transparent overlay
        setOpaque(false);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(true);
        box.setBackground(new Color(14, 18, 32, 225));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 200), 2),
            BorderFactory.createEmptyBorder(26, 34, 26, 34)
        ));

        JLabel title = new JLabel("PAUSE MENU", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(new Color(0, 255, 200));
        title.setFont(new Font("Consolas", Font.BOLD, 30));


        JButton resume = new JButton("Resume");
        JButton restart = new JButton("Restart");
        JButton instructions = new JButton("Instructions");
        JButton menu = new JButton("Main Menu");

        styleButton(resume);
        styleButton(restart);
        styleButton(instructions);
        styleButton(menu);

        resume.addActionListener(e -> onResume.run());
        restart.addActionListener(e -> onRestart.run());
        instructions.addActionListener(e -> onInstructions.run());
        menu.addActionListener(e -> onMainMenu.run());

        box.add(title);
        box.add(Box.createRigidArea(new Dimension(0, 8)));

        box.add(Box.createRigidArea(new Dimension(0, 22)));
        box.add(resume);
        box.add(Box.createRigidArea(new Dimension(0, 12)));
        box.add(restart);
        box.add(Box.createRigidArea(new Dimension(0, 12)));
        box.add(instructions);
        box.add(Box.createRigidArea(new Dimension(0, 12)));
        box.add(menu);

        add(box);
    }

    private void styleButton(JButton button)
    {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Consolas", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(240, 48));
        button.setMaximumSize(new Dimension(240, 48));
    }
}