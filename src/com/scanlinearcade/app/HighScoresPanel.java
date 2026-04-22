//*****************************************************************************************************
// 
// Program Title: HighScoresPanel.java
// Project File: App
// Name: Braden Gant
// Course Section: CS321-01 
// Date (MM/YYYY): 04/2026
//
//*****************************************************************************************************
package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * UI Panel Class: HighScoresPanel
 *
 * <p>Intent: Displays a comprehensive high scores leaderboard screen with tabbed navigation
 * for each arcade game. Presents ranked scores with player names, formatted as arcade-style
 * display cards with color-coded highlighting for top scores and alternating row colors.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public HighScoresPanel(Runnable onBackToMenu)}</li>
 *   <li>{@code public void refreshScores()}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class HighScoresPanel extends JPanel
{
    private static final Color BG = new Color(8, 10, 24);
    private static final Color PANEL_BG = new Color(16, 20, 38);
    private static final Color CARD_BG = new Color(22, 26, 48);
    private static final Color ROW_ALT = new Color(18, 22, 40);
    private static final Color TAB_BG = new Color(20, 24, 42);
    private static final Color TAB_SELECTED = new Color(28, 34, 60);

    private static final Color CYAN = new Color(0, 255, 200);
    private static final Color TEXT = new Color(235, 235, 235);
    private static final Color MUTED = new Color(165, 175, 190);
    private static final Color GOLD = new Color(255, 220, 120);

    private final Runnable onBackToMenu;

    private final JTabbedPane tabs;

    private final JPanel snakeTab;
    private final JPanel breakoutTab;
    private final JPanel invadersTab;

    /**
     * Creates a high scores panel with tabbed game leaderboards.
     * Signature: {@code public HighScoresPanel(Runnable onBackToMenu)}
     *
     * @param onBackToMenu callback invoked when the back button is clicked
     */
    public HighScoresPanel(Runnable onBackToMenu)
    {
        this.onBackToMenu = onBackToMenu;

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = createHeaderPanel();

        tabs = new JTabbedPane();
        tabs.setFocusable(false);
        tabs.setOpaque(false);
        tabs.setBackground(BG);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Monospaced", Font.BOLD, 14));
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new ArcadeTabbedPaneUI());

        snakeTab = createScoreCard();
        breakoutTab = createScoreCard();
        invadersTab = createScoreCard();

        tabs.addTab("Snake", createTabPage(snakeTab));
        tabs.addTab("Breakout", createTabPage(breakoutTab));
        tabs.addTab("Space Invaders", createTabPage(invadersTab));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshScores();
    }

    /**
     * Refreshes the leaderboards for all games from persistent storage.
     * Signature: {@code public void refreshScores()}
     */
    public void refreshScores()
    {
        rebuildTab(
            snakeTab,
            "Snake",
            HighScoreStore.getTopScores("snake", HighScoreStore.DEFAULT_MAX_ENTRIES)
        );

        rebuildTab(
            breakoutTab,
            "Breakout",
            HighScoreStore.getTopScores("breakout", HighScoreStore.DEFAULT_MAX_ENTRIES)
        );

        rebuildTab(
            invadersTab,
            "Space Invaders",
            HighScoreStore.getTopScores("spaceinvaders", HighScoreStore.DEFAULT_MAX_ENTRIES)
        );

        revalidate();
        repaint();
    }

    /**
     * Creates the header panel with title and back button.
     * Signature: {@code private JPanel createHeaderPanel()}
     *
     * @return formatted header panel
     */
    private JPanel createHeaderPanel()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JButton backButton = new JButton("BACK");
        backButton.setFocusable(false);
        backButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        backButton.setForeground(TEXT);
        backButton.setBackground(TAB_BG);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CYAN, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        backButton.addActionListener(e -> onBackToMenu.run());

        JLabel titleLabel = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        titleLabel.setForeground(CYAN);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));

        JLabel spacer = new JLabel();
        spacer.setPreferredSize(backButton.getPreferredSize());

        header.add(backButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        header.add(spacer, BorderLayout.EAST);

        return header;
    }

    /**
     * Creates a scrollable tab page wrapping a score card display.
     * Signature: {@code private JScrollPane createTabPage(JPanel scoreCard)}
     *
     * @param scoreCard the score card panel to wrap
     * @return scrollable container for the score card
     */
    private JScrollPane createTabPage(JPanel scoreCard)
    {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(true);
        page.setBackground(BG);
        page.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        // Put the score card back at the top-left and let it stretch right
        page.add(scoreCard, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setBackground(BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    /**
     * Creates an empty score card panel with arcade styling and scanline effect.
     * Signature: {@code private JPanel createScoreCard()}
     *
     * @return formatted score card panel ready for score row content
     */
    private JPanel createScoreCard()
    {
        JPanel panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                g.setColor(new Color(255, 255, 255, 8));
                for (int y = 0; y < getHeight(); y += 4)
                {
                    g.drawLine(0, y, getWidth(), y);
                }
            }
        };

        panel.setOpaque(true);
        panel.setBackground(PANEL_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CYAN, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        return panel;
    }

    /**
     * Populates a tab panel with game title, header row, and formatted score entries.
     * Signature: {@code private void rebuildTab(JPanel tabPanel, String gameTitle, List<HighScoreStore.ScoreEntry> scores)}
     *
     * @param tabPanel panel to populate with score data
     * @param gameTitle title of the game for display
     * @param scores list of score entries to display
     */
    private void rebuildTab(
        JPanel tabPanel,
        String gameTitle,
        List<HighScoreStore.ScoreEntry> scores
    )
    {
        tabPanel.removeAll();

        JLabel gameLabel = new JLabel(gameTitle);
        gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gameLabel.setForeground(CYAN);
        gameLabel.setFont(new Font("Monospaced", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Top " + HighScoreStore.DEFAULT_MAX_ENTRIES + " scores saved on this computer");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Monospaced", Font.PLAIN, 13));

        tabPanel.add(gameLabel);
        tabPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        tabPanel.add(subtitle);
        tabPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        tabPanel.add(createHeaderRow());
        tabPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (scores.isEmpty())
        {
            JLabel emptyLabel = new JLabel("No scores saved yet.");
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setForeground(MUTED);
            emptyLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
            tabPanel.add(emptyLabel);
            return;
        }

        int rank = 1;
        for (HighScoreStore.ScoreEntry entry : scores)
        {
            boolean highlight = rank == 1;
            boolean striped = (rank % 2 == 0);

            tabPanel.add(createScoreRow(
                "#" + rank,
                entry.playerName(),
                String.valueOf(entry.score()),
                highlight,
                striped
            ));
            tabPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            rank++;
        }
    }

    /**
     * Creates the header row for score table columns (Rank, Name, Score).
     * Signature: {@code private JPanel createHeaderRow()}
     *
     * @return formatted header row panel
     */
    private JPanel createHeaderRow()
    {
        JPanel row = new JPanel(new BorderLayout(18, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel rankLabel = createRowLabel("Rank", CYAN, 18, SwingConstants.LEFT);
        rankLabel.setPreferredSize(new Dimension(120, 28));

        JLabel nameLabel = createRowLabel("Name", CYAN, 18, SwingConstants.LEFT);

        JLabel scoreLabel = createRowLabel("Score", CYAN, 18, SwingConstants.RIGHT);
        scoreLabel.setPreferredSize(new Dimension(140, 28));

        row.add(rankLabel, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(scoreLabel, BorderLayout.EAST);

        return row;
    }

    /**
     * Creates a formatted row for a single score entry with rank, name, and score.
     * Signature: {@code private JPanel createScoreRow(String rank, String name, String score, boolean highlight, boolean striped)}
     *
     * @param rank rank number or label
     * @param name player name
     * @param score player score
     * @param highlight if {@code true}, uses gold color for top rank highlighting
     * @param striped if {@code true}, uses alternating background color for visual separation
     * @return formatted score row panel
     */
    private JPanel createScoreRow(
        String rank,
        String name,
        String score,
        boolean highlight,
        boolean striped
    )
    {
        JPanel row = new JPanel(new BorderLayout(18, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setOpaque(true);
        row.setBackground(striped ? ROW_ALT : CARD_BG);
        row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        Color rowColor = highlight ? GOLD : TEXT;

        JLabel rankLabel = createRowLabel(rank, rowColor, 18, SwingConstants.LEFT);
        rankLabel.setPreferredSize(new Dimension(120, 22));

        JLabel nameLabel = createRowLabel(name, rowColor, 18, SwingConstants.LEFT);

        JLabel scoreLabel = createRowLabel(score, rowColor, 18, SwingConstants.RIGHT);
        scoreLabel.setPreferredSize(new Dimension(140, 22));

        row.add(rankLabel, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(scoreLabel, BorderLayout.EAST);

        return row;
    }

    /**
     * Creates a styled label for use in score row or header row.
     * Signature: {@code private JLabel createRowLabel(String text, Color color, int fontSize, int alignment)}
     *
     * @param text text to display
     * @param color text color
     * @param fontSize font size in points
     * @param alignment text alignment (e.g., SwingConstants.LEFT, RIGHT)
     * @return formatted label
     */
    private JLabel createRowLabel(String text, Color color, int fontSize, int alignment)
    {
        JLabel label = new JLabel(text, alignment);
        label.setForeground(color);
        label.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        return label;
    }

    /**
     * Custom UI delegate for arcade-styled tabbed panes.
     * Provides customized tab appearance and behavior for the arcade theme.
     */
    private static class ArcadeTabbedPaneUI extends BasicTabbedPaneUI
    {
        @Override
        protected void installDefaults()
        {
            super.installDefaults();
            highlight = CYAN;
            lightHighlight = CYAN;
            shadow = BG;
            darkShadow = BG;
            focus = BG;
        }

        @Override
        protected void paintTabBackground(
            Graphics g,
            int tabPlacement,
            int tabIndex,
            int x,
            int y,
            int w,
            int h,
            boolean isSelected
        )
        {
            g.setColor(isSelected ? TAB_SELECTED : TAB_BG);
            g.fillRect(x, y, w, h);
        }

        @Override
        protected void paintText(
            Graphics g,
            int tabPlacement,
            Font font,
            java.awt.FontMetrics metrics,
            int tabIndex,
            String title,
            java.awt.Rectangle textRect,
            boolean isSelected
        )
        {
            Font drawFont = new Font("Monospaced", Font.BOLD, 14);
            g.setFont(drawFont);
            java.awt.FontMetrics drawMetrics = g.getFontMetrics(drawFont);

            g.setColor(isSelected ? TEXT : MUTED);
            g.drawString(title, textRect.x, textRect.y + drawMetrics.getAscent());
        }

        @Override
        protected void paintFocusIndicator(
            Graphics g,
            int tabPlacement,
            java.awt.Rectangle[] rects,
            int tabIndex,
            java.awt.Rectangle iconRect,
            java.awt.Rectangle textRect,
            boolean isSelected
        )
        {
            // No default dashed focus outline.
        }

        @Override
        protected void paintContentBorder(
            Graphics g,
            int tabPlacement,
            int selectedIndex
        )
        {
            int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);

            g.setColor(BG);
            g.fillRect(0, tabAreaHeight, tabPane.getWidth(), tabPane.getHeight() - tabAreaHeight);

            g.setColor(CYAN);
            g.drawLine(0, tabAreaHeight, tabPane.getWidth(), tabAreaHeight);
        }
    }
}