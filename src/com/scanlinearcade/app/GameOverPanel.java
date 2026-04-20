//*****************************************************************************************************
// 
// Program Title: GameOverPanel.java
// Project File: App
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 04/2026
//
//*****************************************************************************************************
package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * UI Panel Class: GameOverPanel
 *
 * <p>Intent: Displays game over results including final score, high scores leaderboard,
 * and options for score submission, game restart, or return to main hub. Integrates with
 * {@code HighScoreStore} to persist and display player scores.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public GameOverPanel(String gameKey, Runnable onRestart, Runnable onReturnToHub)}</li>
 *   <li>{@code public void showResult(String resultText, int score, String runToken)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class GameOverPanel extends JPanel {

    private final String gameKey;
    private final Runnable onRestart;
    private final Runnable onReturnToHub;

    private final JLabel resultLabel;
    private final JLabel scoreLabel;
    private final JTextField nameField;
    private final JButton saveButton;
    private final DefaultListModel<String> listModel;

    private String runToken;
    private int score;
    private boolean scoreSaved;

    /**
     * Creates a game over panel with result display and score submission UI.
     * Signature: {@code public GameOverPanel(String gameKey, Runnable onRestart, Runnable onReturnToHub)}
     *
     * @param gameKey unique identifier for the game (e.g., "breakout", "snake", "spaceinvaders")
     * @param onRestart callback invoked when the restart button is clicked
     * @param onReturnToHub callback invoked when the return to hub button is clicked
     */
    public GameOverPanel(String gameKey, Runnable onRestart, Runnable onReturnToHub) {
        this.gameKey = gameKey;
        this.onRestart = onRestart;
        this.onReturnToHub = onReturnToHub;

        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 170));
        setOpaque(true);
        setFocusable(true);

        JPanel box = new JPanel(new BorderLayout(10, 10));
        box.setBackground(new Color(20, 20, 30));
        box.setPreferredSize(new Dimension(560, 520));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(85, 85, 95), 1),
            BorderFactory.createEmptyBorder(26, 26, 26, 26)
        ));

        resultLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        resultLabel.setForeground(Color.WHITE);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        scoreLabel.setForeground(Color.WHITE);

        JPanel top = new JPanel(new GridLayout(0, 1, 0, 4));
        top.setOpaque(false);
        top.add(resultLabel);
        top.add(scoreLabel);

        listModel = new DefaultListModel<>();
        JList<String> scoreList = new JList<>(listModel);
        scoreList.setFocusable(false);

        JScrollPane scoreScroll = new JScrollPane(scoreList);
        scoreScroll.setPreferredSize(new Dimension(440, 260));

        JPanel scorePanel = new JPanel(new BorderLayout(0, 6));
        scorePanel.setOpaque(false);

        JLabel hsLabel = new JLabel("High Scores", SwingConstants.CENTER);
        hsLabel.setForeground(Color.WHITE);
        scorePanel.add(hsLabel, BorderLayout.NORTH);
        scorePanel.add(scoreScroll, BorderLayout.CENTER);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Initials/Name:");
        nameLabel.setForeground(Color.WHITE);
        namePanel.add(nameLabel);

        nameField = new JTextField(10);
        namePanel.add(nameField);

        saveButton = new JButton("Save Score");
        JButton restartButton = new JButton("Restart");
        JButton hubButton = new JButton("Return To Main Hub");

        // Keep key behavior deterministic for the underlying game input.
        saveButton.addActionListener(e -> {
            saveScoreIfNeeded();
            refreshHighScores();
        });

        restartButton.addActionListener(e -> {
            saveScoreIfNeeded();
            setVisible(false);
            if (onRestart != null) {
                onRestart.run();
            }
        });

        hubButton.addActionListener(e -> {
            saveScoreIfNeeded();
            setVisible(false);
            if (onReturnToHub != null) {
                onReturnToHub.run();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);
        buttons.add(saveButton);
        buttons.add(restartButton);
        buttons.add(hubButton);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(scorePanel, BorderLayout.CENTER);
        center.add(namePanel, BorderLayout.SOUTH);

        box.add(top, BorderLayout.NORTH);
        box.add(center, BorderLayout.CENTER);
        box.add(buttons, BorderLayout.SOUTH);

        add(box);

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "menu");
        actionMap.put("menu", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                hubButton.doClick();
            }
        });

        saveButton.setFocusable(false);
        restartButton.setFocusable(false);
        hubButton.setFocusable(false);
    }

    /**
     * Displays the game over result with final score and high scores list.
     * Updates the UI for score submission and shows relevant controls.
     * Signature: {@code public void showResult(String resultText, int score, String runToken)}
     *
     * @param resultText message to display (e.g., "You Win!", "Game Over!")
     * @param score final score achieved in this game session
     * @param runToken unique session identifier to prevent duplicate score submissions
     */
    public void showResult(String resultText, int score, String runToken) {
        this.score = score;
        this.runToken = runToken;

        resultLabel.setText(resultText);
        scoreLabel.setText("Score: " + score);

        nameField.setText("");
        scoreSaved = HighScoreStore.isRunAlreadySaved(gameKey, runToken);
        saveButton.setEnabled(!scoreSaved);
        nameField.setEnabled(!scoreSaved);

        refreshHighScores();

        setVisible(true);
        requestFocusInWindow();
    }

    /**
     * Refreshes the high scores list displayed on the panel.
     * Signature: {@code private void refreshHighScores()}
     */
    private void refreshHighScores() {
        listModel.clear();

        List<HighScoreStore.ScoreEntry> scores = HighScoreStore.getTopScores(gameKey, HighScoreStore.DEFAULT_MAX_ENTRIES);
        if (scores.isEmpty()) {
            listModel.addElement("No scores yet");
            return;
        }

        int rank = 1;
        for (HighScoreStore.ScoreEntry entry : scores) {
            listModel.addElement(rank + ". " + entry.playerName() + " - " + entry.score());
            rank++;
        }
    }

    /**
     * Submits the current score to the high score store if not already saved.
     * Uses the player name from the input field if provided.
     * Signature: {@code private void saveScoreIfNeeded()}
     */
    private void saveScoreIfNeeded() {
        if (scoreSaved) {
            return;
        }

        String enteredName = nameField.getText();
        if (enteredName == null || enteredName.isBlank()) {
            return;
        }

        scoreSaved = HighScoreStore.submitScore(
                gameKey,
                enteredName,
                score,
                HighScoreStore.DEFAULT_MAX_ENTRIES,
                runToken
        );

        if (scoreSaved) {
            saveButton.setEnabled(false);
            nameField.setEnabled(false);
        }
    }
}
