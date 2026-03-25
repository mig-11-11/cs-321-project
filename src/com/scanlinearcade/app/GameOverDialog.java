package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Reusable game-over dialog that can be used by any game panel.
 */
public final class GameOverDialog extends JDialog {

    private final String gameKey;
    private final String runToken;
    private final int score;
    private final JTextField nameField;
    private final JButton saveButton;
    private boolean scoreSaved;

    private GameOverDialog(
            Window owner,
            String gameKey,
            String runToken,
            String resultText,
            int score,
            Runnable onRestart,
            Runnable onReturnToHub
    ) {
        super(owner, "Game Over", ModalityType.APPLICATION_MODAL);

        this.gameKey = gameKey;
        this.runToken = runToken;
        this.score = score;
        this.nameField = new JTextField(10);

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel resultLabel = new JLabel(resultText, SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel top = new JPanel(new GridLayout(0, 1, 0, 4));
        top.add(resultLabel);
        top.add(scoreLabel);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        fillHighScoreModel(listModel, gameKey);

        JList<String> scoreList = new JList<>(listModel);
        scoreList.setFocusable(false);
        JScrollPane scoreScroll = new JScrollPane(scoreList);
        scoreScroll.setPreferredSize(new Dimension(280, 140));

        JPanel scorePanel = new JPanel(new BorderLayout(0, 6));
        scorePanel.add(new JLabel("High Scores", SwingConstants.CENTER), BorderLayout.NORTH);
        scorePanel.add(scoreScroll, BorderLayout.CENTER);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.add(new JLabel("Initials/Name:"));
        namePanel.add(nameField);

        saveButton = new JButton("Save Score");
        saveButton.addActionListener(e -> {
            saveScoreIfNeeded();
            listModel.clear();
            fillHighScoreModel(listModel, gameKey);
        });

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> {
            saveScoreIfNeeded();
            dispose();
            if (onRestart != null) {
                onRestart.run();
            }
        });

        JButton hubButton = new JButton("Return To Main Hub");
        hubButton.addActionListener(e -> {
            saveScoreIfNeeded();
            dispose();
            if (onReturnToHub != null) {
                onReturnToHub.run();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(saveButton);
        buttons.add(restartButton);
        buttons.add(hubButton);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.add(scorePanel, BorderLayout.CENTER);
        center.add(namePanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(saveButton);

        if (HighScoreStore.isRunAlreadySaved(gameKey, runToken)) {
            scoreSaved = true;
            saveButton.setEnabled(false);
            nameField.setEnabled(false);
        }
    }

    public static void showDialog(
            Component parent,
            String gameKey,
            String runToken,
            String resultText,
            int score,
            Runnable onRestart,
            Runnable onReturnToHub
    ) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        GameOverDialog dialog = new GameOverDialog(owner, gameKey, runToken, resultText, score, onRestart, onReturnToHub);
        dialog.setVisible(true);
    }

    private static void fillHighScoreModel(DefaultListModel<String> model, String gameKey) {
        List<HighScoreStore.ScoreEntry> scores = HighScoreStore.getTopScores(gameKey, HighScoreStore.DEFAULT_MAX_ENTRIES);

        if (scores.isEmpty()) {
            model.addElement("No scores yet");
            return;
        }

        int rank = 1;
        for (HighScoreStore.ScoreEntry entry : scores) {
            model.addElement(rank + ". " + entry.playerName() + " - " + entry.score());
            rank++;
        }
    }

    private void saveScoreIfNeeded() {
        if (scoreSaved) {
            return;
        }

        scoreSaved = HighScoreStore.submitScore(
                gameKey,
                nameField.getText(),
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
