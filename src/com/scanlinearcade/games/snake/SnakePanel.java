//*****************************************************************************************************
// Program Title: Direction.java
// Project File: 
// Name: Braden Gant
// Course Section: CS321-01 
// Date: 02/03/2026
// Program Description:  
//
//*****************************************************************************************************

package com.scanlinearcade.games.snake;

import com.scanlinearcade.app.ArcadeFrame;
import com.scanlinearcade.app.GameOverDialog;
import java.awt.*;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Swing UI panel for the Snake game. This class serves as the primary View/Controller
 * for the Snake module:
 *
 * <ul>
 *   <li><b>View:</b> Renders the grid, snake, food, HUD text, and game-over overlay.</li>
 *   <li><b>Controller:</b> Captures keyboard input (WASD/Arrow keys, Space, R) and
 *       forwards intent to the {@link SnakeModel}.</li>
 *   <li><b>Session loop:</b> Uses a Swing {@link Timer} to advance the game at a fixed rate.</li>
 * </ul>
 *
 * <p><b>Design contract:</b> Core game rules and state updates belong in {@link SnakeModel}.
 * This panel should only trigger updates (via {@code model.step()}) and render the model state.</p>
 *
 * <h2>API Outline (public/protected)</h2>
 * <pre>
 * public SnakePanel()
 * public Dimension getPreferredSize()
 * protected void paintComponent(Graphics g)
 * </pre>
 *
 * <h2>Internal Helpers (package-private/private)</h2>
 * <pre>
 * private void togglePause()
 * private static void drawCentered(Graphics2D g2, String text, Rectangle rect)
 * </pre>
 */
public class SnakePanel extends JPanel {

    /** Pixels per grid cell. */
    private static final int CELL = 24;     // pixels per grid cell
    private static final int COLS = 25;     // grid width
    private static final int ROWS = 20;     // grid height
    private static final int FPS_MS = 120;  // lower = faster
    
    /** Domain model containing all Snake state and game rules. */
    private final SnakeModel model = new SnakeModel(COLS, ROWS);
    
    /** Swing timer driving the update loop */
    private final Timer timer;
    private final Runnable returnToHubAction;
    private boolean gameOverDialogShown;
    private boolean paused;
    private boolean showingInstructionsCard;
    private String currentRunToken;

    public SnakePanel() 
    {
        this(null);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    public SnakePanel(Runnable returnToHubAction) 
    {
        setBackground(Color.WHITE);
        setFocusable(true);
        this.returnToHubAction = returnToHubAction;
        this.currentRunToken = UUID.randomUUID().toString();
        this.gameOverDialogShown = false;
        this.paused = false;
        this.showingInstructionsCard = false;

        // Game loop, updates the model then repaints the panel
       
        timer = new Timer(FPS_MS,e -> 
        {
            if (!paused)
            {
                model.step();
            }
            
            if(model.isGameOver() && !gameOverDialogShown) 
            {
                gameOverDialogShown = true;
                stopGameLoop();
                SwingUtilities.invokeLater(this::showSharedGameOverMenu);
            }
            repaint();
        });

        // Keyboard input tp mopel actions
        addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) {
                if (paused && showingInstructionsCard)
                {
                    if (e.getKeyCode() == KeyEvent.VK_M)
                    {
                        returnToHubFromDialog();
                        return;
                    }

                    paused = false;
                    showingInstructionsCard = false;
                    repaint();
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> model.setDirection(Direction.UP);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> model.setDirection(Direction.DOWN);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> model.setDirection(Direction.LEFT);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> model.setDirection(Direction.RIGHT);

                    case KeyEvent.VK_R -> model.reset(); // restart
                    case KeyEvent.VK_SPACE -> togglePause();
                    case KeyEvent.VK_I -> toggleInstructionsCard();
                    case KeyEvent.VK_M -> returnToHubFromDialog();
                    default -> { }
                }

                if (e.getKeyCode() == KeyEvent.VK_R) {
                    gameOverDialogShown = false;
                    currentRunToken = UUID.randomUUID().toString();
                    paused = false;
                    showingInstructionsCard = false;
                    if (!timer.isRunning()) {
                        timer.start();
                    }
                }
            }
        });
    }

    private void showSharedGameOverMenu() {
        GameOverDialog.showDialog(
                this,
                "snake",
            currentRunToken,
                "Game Over",
                model.getScore(),
                this::restartFromDialog,
                this::returnToHubFromDialog
        );
    }

    private void restartFromDialog() {
        model.reset();
        gameOverDialogShown = false;
        currentRunToken = UUID.randomUUID().toString();
        paused = false;
        showingInstructionsCard = false;
        if (!timer.isRunning()) {
            timer.start();
        }
        requestFocusInWindow();
        repaint();
    }

    private void returnToHubFromDialog() {
        if (returnToHubAction != null) {
            returnToHubAction.run();
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        new ArcadeFrame().setVisible(true);
        if (window != null) {
            window.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() 
    {
        // Add extra space for HUD text
        return new Dimension(COLS * CELL, ROWS * CELL + 40);
    }

    private void togglePause() 
    {
        if (model.isGameOver())
        {
            return;
        }

        paused = !paused;
        if (!paused)
        {
            showingInstructionsCard = false;
        }
        repaint();
    }

    private void toggleInstructionsCard()
    {
        if (!paused)
        {
            return;
        }

        showingInstructionsCard = !showingInstructionsCard;
        repaint();
    }

    private void drawCenteredLine(Graphics2D g2, String text, int y)
    {
        int x = (COLS * CELL - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

  
    /**
     * Renders the Snake game based on the current {@link SnakeModel} state.
     *
     * <p>This method draws:</p>
     * <ul>
     *   <li>board background and optional grid</li>
     *   <li>food location</li>
     *   <li>snake body and head</li>
     *   <li>HUD text (score + controls)</li>
     *   <li>game-over overlay when applicable</li>
     * </ul>
     *
     * <p><b>Design contract:</b> No game rules should be executed here. Rendering
     * is derived from model getters only.</p>
     *
     * <pre>
     * protected void paintComponent(Graphics g)
     * </pre>
     *
     * @param g the Swing graphics context for this component
     */
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int boardW = COLS * CELL;
        int boardH = ROWS * CELL;

        // Board background area (top part)
        g2.setColor(new Color(12, 12, 12));
        g2.fillRect(0, 0, boardW, boardH);

        // Optional grid
        g2.setColor(new Color(25, 25, 25));
        for (int x = 0; x <= COLS; x++) g2.drawLine(x * CELL, 0, x * CELL, boardH);
        for (int y = 0; y <= ROWS; y++) g2.drawLine(0, y * CELL, boardW, y * CELL);

        // Food
        Point food = model.getFood();
        g2.setColor(Color.WHITE);
        g2.fillOval(food.x * CELL + 3, food.y * CELL + 3, CELL - 6, CELL - 6);

        // Snake
        boolean first = true;
        for (Point p : model.getSnake()) {
            if (first) {
                g2.setColor(new Color(0, 220, 120)); // head
                first = false;
            } else {
                g2.setColor(new Color(0, 160, 90)); // body
            }
            g2.fillRect(p.x * CELL + 2, p.y * CELL + 2, CELL - 4, CELL - 4);
        }

        // HUD area (bottom bar)
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, boardH, boardW, 40);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.PLAIN, 16));
        g2.drawString("Score: " + model.getScore() + "   [Space]=Pause  [R]=Restart  [M]=Menu", 10, boardH + 25);

        if (paused)
        {
            g2.setColor(new Color(0, 0, 0, 190));
            g2.fillRect(30, 40, boardW - 60, boardH - 80);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Consolas", Font.BOLD, 26));

            if (showingInstructionsCard)
            {
                drawCenteredLine(g2, "Snake Instructions", 90);
                g2.setFont(new Font("Consolas", Font.PLAIN, 16));
                drawCenteredLine(g2, "Eat food to grow and increase score.", 140);
                drawCenteredLine(g2, "Avoid walls and your own body.", 170);
                drawCenteredLine(g2, "Move: [W/A/S/D] or [Arrow Keys]", 220);
                drawCenteredLine(g2, "Pause/Resume: [Space]", 250);
                drawCenteredLine(g2, "Press any button to start Snake", 300);
                drawCenteredLine(g2, "Press [M] to return to the main menu", 330);
            }
            else
            {
                g2.drawString("Paused", 320, 90);
                g2.setFont(new Font("Consolas", Font.PLAIN, 16));
                g2.drawString("[Space] Resume", 280, 160);
                g2.drawString("[R] Restart", 290, 190);
                g2.drawString("[M] Return to Main Menu", 220, 220);
                g2.drawString("[I] Instructions", 250, 250);
            }
        }

        g2.dispose();
    }

 
    
   
   
    public void startGameLoop()
    {
        if(!timer.isRunning())
        {
            timer.start();
        }
        requestFocusInWindow();
    }

    public void stopGameLoop()
    {
        if(timer.isRunning())
        {
            timer.stop();
        }
    }

    public void resetGame()
    {
        model.reset();
        gameOverDialogShown = false;
        currentRunToken = UUID.randomUUID().toString();
        paused = false;
        showingInstructionsCard = false;
        repaint();
    }

    public void showInstructionsCard()
    {
        if (model.isGameOver())
        {
            return;
        }

        paused = true;
        showingInstructionsCard = true;
        repaint();
    }
    
}



