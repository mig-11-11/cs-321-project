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

    @FunctionalInterface
    public interface GameOverHandler {
        void onGameOver(String resultText, int score, String runToken);
    }

    /** Pixels per grid cell. */
    private static final int CELL = 24;     // pixels per grid cell
    private static final int COLS = 25;     // grid width
    private static final int ROWS = 20;     // grid height
    private static final int FPS_MS = 120;  // lower = faster
    
    private static final int HUD_HEIGHT = 48; // slightly taller HUD bar

    // HUD colors
    private static final Color HUD_BG = new Color(58, 58, 62);
    private static final Color HUD_TEXT = new Color(235, 235, 235);
    private static final Color HUD_ACCENT = new Color(0, 255, 200);

    // Instruction card colors
    private static final Color INSTRUCTION_DIM = new Color(0, 0, 0, 110);
    private static final Color INSTRUCTION_BOX_BG = new Color(10, 16, 30, 220);
    private static final Color INSTRUCTION_BOX_BORDER = new Color(0, 255, 200, 120);
    private static final Color INSTRUCTION_TITLE = new Color(230, 245, 255);
    private static final Color INSTRUCTION_TEXT = new Color(220, 225, 230);
    
    
    
    
    
    /** Domain model containing all Snake state and game rules. */
    private final SnakeModel model = new SnakeModel(COLS, ROWS);
    
    /** Swing timer driving the update loop */
    private final Timer timer;
    private final Runnable returnToHubAction;
    private final GameOverHandler gameOverHandler;
    private boolean gameOverOverlayShown;
    private boolean paused;
    private boolean showingInstructionsCard;
    private long suppressPauseUntilMs;
    private String currentRunToken;

    public SnakePanel() 
    {
        this(null, null);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    public SnakePanel(Runnable returnToHubAction) 
    {
        setBackground(Color.BLACK);
        setFocusable(true);
        this.returnToHubAction = returnToHubAction;
        this.gameOverHandler = gameOverHandler;
        this.currentRunToken = UUID.randomUUID().toString();
        this.gameOverOverlayShown = false;
        this.paused = false;
        this.showingInstructionsCard = false;
        this.suppressPauseUntilMs = 0L;

        // Game loop, updates the model then repaints the panel
       
        timer = new Timer(FPS_MS,e -> 
        {
            if (!paused)
            {
                model.step();
            }
            
            if(model.isGameOver() && !gameOverOverlayShown) 
            {
                gameOverOverlayShown = true;
                stopGameLoop();

                if (this.gameOverHandler != null)
                {
                    this.gameOverHandler.onGameOver("Game Over", model.getScore(), currentRunToken);
                }
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
                    suppressPauseUntilMs = System.currentTimeMillis() + 200L;
                    repaint();
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> model.setDirection(Direction.UP);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> model.setDirection(Direction.DOWN);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> model.setDirection(Direction.LEFT);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> model.setDirection(Direction.RIGHT);

                    case KeyEvent.VK_R -> model.reset(); // restart
                    case KeyEvent.VK_I -> showInstructionsCard(); // instructions
                    default -> { }
                }

                if (e.getKeyCode() == KeyEvent.VK_R) {
                    gameOverOverlayShown = false;
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
        return new Dimension(COLS * CELL, ROWS * CELL + HUD_HEIGHT);
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

    // Logical game size
    final int hudHeight = HUD_HEIGHT;
    final int logicalBoardW = COLS * CELL;
    final int logicalBoardH = ROWS * CELL;
    final int logicalTotalH = logicalBoardH + hudHeight;

    // Available size in the actual panel
    int panelW = getWidth();
    int panelH = getHeight();

    // Scale uniformly so the whole game fits and keeps its aspect ratio
    double scaleX = (double) panelW / logicalBoardW;
    double scaleY = (double) panelH / logicalTotalH;
    double scale = Math.min(scaleX, scaleY);

    // Center the scaled game area
    int drawW = (int) Math.round(logicalBoardW * scale);
    int drawH = (int) Math.round(logicalTotalH * scale);
    int offsetX = (panelW - drawW) / 2;
    int offsetY = (panelH - drawH) / 2;

    // Fill outer background
    g2.setColor(Color.BLACK);
    g2.fillRect(0, 0, panelW, panelH);

    // Move and scale into place
    g2.translate(offsetX, offsetY);
    g2.scale(scale, scale);

    // Board background area
    g2.setColor(new Color(12, 12, 12));
    g2.fillRect(0, 0, logicalBoardW, logicalBoardH);

    // Optional grid
    g2.setColor(new Color(20, 20, 20));
    for (int x = 0; x <= COLS; x++) 
    {
        g2.drawLine(x * CELL, 0, x * CELL, logicalBoardH);
    }
    for (int y = 0; y <= ROWS; y++) 
    {
        g2.drawLine(0, y * CELL, logicalBoardW, y * CELL);
    }

    // Food
    Point food = model.getFood();
    g2.setColor(Color.WHITE);
    g2.fillOval(food.x * CELL + 3, food.y * CELL + 3, CELL - 6, CELL - 6);

    // Snake
    boolean first = true;
    for (Point p : model.getSnake()) 
    {
        if (first) 
        {
            g2.setColor(new Color(0, 220, 120)); // head
            first = false;
        } 
        else 
        {
            g2.setColor(new Color(0, 160, 90)); // body
        }

        g2.fillRect(p.x * CELL + 2, p.y * CELL + 2, CELL - 4, CELL - 4);
    }

    // HUD area
    g2.setColor(HUD_BG);
    g2.fillRect(0, logicalBoardH, logicalBoardW, hudHeight);

    g2.setFont(new Font("Consolas", Font.PLAIN, 16));
    FontMetrics fm = g2.getFontMetrics();
    int hudBaseline = logicalBoardH + ((hudHeight - fm.getHeight()) / 2) + fm.getAscent();

    String pauseText = "[Esc] Pause Menu";
    String instructionsText = "[I] Instructions";
    String scoreText = "Score: " + model.getScore();

    // Bottom left
    g2.setColor(HUD_TEXT);
    g2.drawString(pauseText, 10, hudBaseline);

    // Middle
    int instructionsX = (logicalBoardW - fm.stringWidth(instructionsText)) / 2;
    g2.setColor(HUD_ACCENT);
    g2.drawString(instructionsText, instructionsX, hudBaseline);

    // Bottom right
    int scoreX = logicalBoardW - fm.stringWidth(scoreText) - 10;
    g2.setColor(HUD_TEXT);
    g2.drawString(scoreText, scoreX, hudBaseline);

    // Instructions overlay only
    if (showingInstructionsCard)
    {
        int boxX = 48;
        int boxY = 42;
        int boxW = logicalBoardW - 96;
        int boxH = logicalBoardH - 84;

        // Soft dim
        g2.setColor(INSTRUCTION_DIM);
        g2.fillRoundRect(boxX + 8, boxY + 8, boxW, boxH, 28, 28);

        // Main panel
        g2.setColor(INSTRUCTION_BOX_BG);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 28, 28);

        // Border
        g2.setColor(INSTRUCTION_BOX_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 28, 28);

        // Title
        g2.setColor(INSTRUCTION_TITLE);
        g2.setFont(new Font("Consolas", Font.BOLD, 28));
        drawCenteredLine(g2, "Snake Instructions", boxY + 48);

        // Body text
        g2.setColor(INSTRUCTION_TEXT);
        g2.setFont(new Font("Consolas", Font.PLAIN, 15));
        drawCenteredLine(g2, "Eat food to grow and increase score.", boxY + 100);
        drawCenteredLine(g2, "Avoid walls and your own body.", boxY + 132);
        drawCenteredLine(g2, "Move: [W/A/S/D] or [Arrow Keys]", boxY + 184);
        drawCenteredLine(g2, "Pause Menu: [Esc]", boxY + 216);
        drawCenteredLine(g2, "Instructions: [I]", boxY + 248);
        drawCenteredLine(g2, "Press any key to start / continue", boxY + 308);
        drawCenteredLine(g2, "Press [M] to return to the main menu", boxY + 340);
    }

    g2.dispose();
}

public boolean isShowingInstructionsCard()
{
    return showingInstructionsCard;
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
        gameOverOverlayShown = false;
        currentRunToken = UUID.randomUUID().toString();
        paused = false;
        showingInstructionsCard = false;
        suppressPauseUntilMs = 0L;
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

    public boolean isShowingInstructionsCard()
    {
        return showingInstructionsCard;
    }

    public boolean shouldSuppressPauseToggle()
    {
        return showingInstructionsCard || System.currentTimeMillis() < suppressPauseUntilMs;
    }
    
}
