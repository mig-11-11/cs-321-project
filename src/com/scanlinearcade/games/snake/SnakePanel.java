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
import com.scanlinearcade.app.GameSettings;
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

    // Retro neon palette
    private static final Color OUTER_BG = new Color(3, 4, 12);
    private static final Color BOARD_BG = new Color(10, 12, 28);
    private static final Color GRID_MINOR = new Color(18, 22, 44);
    private static final Color GRID_MAJOR = new Color(24, 34, 68);

    private static final Color NEON_CYAN = new Color(0, 255, 200);
    private static final Color NEON_PINK = new Color(255, 70, 180);
    private static final Color ARCADE_YELLOW = new Color(255, 220, 90);
    private static final Color SOFT_WHITE = new Color(235, 235, 245);

    private static final Color BOARD_BORDER = NEON_CYAN;
    private static final Color BOARD_BORDER_ACCENT = NEON_PINK;

    // HUD colors
    private static final Color HUD_BG = new Color(12, 16, 32);
    private static final Color HUD_TEXT = SOFT_WHITE;
    private static final Color HUD_ACCENT = NEON_CYAN;
    private static final Color HUD_SCORE = ARCADE_YELLOW;

    // Snake colors
    private static final Color SNAKE_HEAD_FILL = new Color(0, 255, 200);
    private static final Color SNAKE_HEAD_BORDER = new Color(255, 70, 180);
    private static final Color SNAKE_BODY_FILL = new Color(255, 70, 180);
    private static final Color SNAKE_BODY_BORDER = new Color(0, 255, 200);

    // Food colors
    private static final Color FOOD_FILL = new Color(255, 220, 90);
    private static final Color FOOD_ACCENT = new Color(255, 70, 180);

    // Instruction card colors
    private static final Color INSTRUCTION_DIM = new Color(0, 0, 0, 145);
    private static final Color INSTRUCTION_BOX_BG = new Color(8, 10, 28, 235);
    private static final Color INSTRUCTION_BOX_BORDER = new Color(0, 255, 200, 180);
    private static final Color INSTRUCTION_TITLE = new Color(0, 255, 200);
    private static final Color INSTRUCTION_TEXT = new Color(235, 235, 245);
    
    
    
    
    
    /** Domain model containing all Snake state and game rules. */
    private final SnakeModel model = new SnakeModel(COLS, ROWS);
    
    /** Swing timer driving the update loop */
    private final Timer timer;
    private final Runnable returnToHubAction;
    private final GameOverHandler gameOverHandler;
    private boolean gameOverOverlayShown;
    private boolean paused;
    private boolean showingInstructionsCard;
    private boolean firstEntryInstructionsPending;
    private long suppressPauseUntilMs;
    private String currentRunToken;
    private GameSettings settings;

    public SnakePanel() 
    {
        this(null, null, null);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    public SnakePanel(Runnable returnToHubAction)
    {
        this(returnToHubAction, null, null);
    }

    public SnakePanel(Runnable returnToHubAction, GameOverHandler gameOverHandler, GameSettings settings) 
    {
        setBackground(Color.BLACK);
        setFocusable(true);
        this.returnToHubAction = returnToHubAction;
        this.gameOverHandler = gameOverHandler;
        this.currentRunToken = UUID.randomUUID().toString();
        this.gameOverOverlayShown = false;
        this.paused = false;
        this.showingInstructionsCard = false;
        this.firstEntryInstructionsPending = true;
        this.suppressPauseUntilMs = 0L;
        this.settings = settings;

        // Game loop, updates the model then repaints the panel
       
        timer = new Timer(FPS_MS,e ->    //change this for settings difficulty
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
                    this.gameOverHandler.onGameOver("Game Over!", model.getScore(), currentRunToken);
                }
            }
            repaint();
        });

        // Keyboard input tp mopel actions
        addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) {
                if (showingInstructionsCard)
                {
                    if (e.getKeyCode() == KeyEvent.VK_M)
                    {
                        returnToHubFromDialog();
                        return;
                    }

                    paused = false;
                    showingInstructionsCard = false;
                    firstEntryInstructionsPending = false;
                    suppressPauseUntilMs = System.currentTimeMillis() + 200L;
                    if (!timer.isRunning())
                    {
                        timer.start();
                    }
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
    
    private void drawScanlines(Graphics2D g2, int width, int height)
    {
        g2.setColor(new Color(255, 255, 255, 8));
        for (int y = 0; y < height; y += 4)
        {
            g2.drawLine(0, y, width, y);
        }
    }

    private void drawSnakeSegment(Graphics2D g2, Point p, boolean head)
    {
        int px = p.x * CELL;
        int py = p.y * CELL;

        if (head)
        {
            g2.setColor(SNAKE_HEAD_BORDER);
            g2.fillRect(px + 1, py + 1, CELL - 2, CELL - 2);

            g2.setColor(SNAKE_HEAD_FILL);
            g2.fillRect(px + 4, py + 4, CELL - 8, CELL - 8);

            // pixel eyes
            g2.setColor(Color.BLACK);
            g2.fillRect(px + 7, py + 7, 3, 3);
            g2.fillRect(px + CELL - 10, py + 7, 3, 3);
            g2.fillRect(px + CELL - 13, py + 13, 3, 2);
        }
        else
        {
            g2.setColor(SNAKE_BODY_BORDER);
            g2.fillRect(px + 1, py + 1, CELL - 2, CELL - 2);

            g2.setColor(SNAKE_BODY_FILL);
            g2.fillRect(px + 3, py + 3, CELL - 6, CELL - 6);
        }
    }

    private void drawFood(Graphics2D g2, Point food)
    {
        int px = food.x * CELL;
        int py = food.y * CELL;

        g2.setColor(FOOD_ACCENT);
        g2.fillOval(px + 4, py + 4, CELL - 8, CELL - 8);

        g2.setColor(FOOD_FILL);
        g2.fillOval(px + 6, py + 6, CELL - 12, CELL - 12);

        // tiny neon highlight
        g2.setColor(Color.WHITE);
        g2.fillRect(px + 9, py + 8, 3, 3);
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
    g2.setColor(settings.getDisplayColor());  //changes color based on display settings
    g2.fillRect(0, 0, panelW, panelH);

    // Move and scale into place
    g2.translate(offsetX, offsetY);
    g2.scale(scale, scale);

    // Board background area
    g2.setColor(BOARD_BG);
    g2.fillRect(0, 0, logicalBoardW, logicalBoardH);

    // Scanlines for retro monitor feel
    drawScanlines(g2, logicalBoardW, logicalBoardH);

    // Neon border around board
    g2.setColor(BOARD_BORDER);
    g2.drawRect(0, 0, logicalBoardW - 1, logicalBoardH - 1);

    g2.setColor(BOARD_BORDER_ACCENT);
    g2.drawLine(0, 1, logicalBoardW - 1, 1);

    // Grid
    for (int x = 0; x <= COLS; x++) 
    {
        g2.setColor((x % 5 == 0) ? GRID_MAJOR : GRID_MINOR);
        g2.drawLine(x * CELL, 0, x * CELL, logicalBoardH);
    }

    for (int y = 0; y <= ROWS; y++) 
    {
        g2.setColor((y % 5 == 0) ? GRID_MAJOR : GRID_MINOR);
        g2.drawLine(0, y * CELL, logicalBoardW, y * CELL);
    }

    // Food
    Point food = model.getFood();
    drawFood(g2, food);

    // Snake
    boolean first = true;
    for (Point p : model.getSnake()) 
    {
        drawSnakeSegment(g2, p, first);
        first = false;
    }

    // HUD area
    g2.setColor(HUD_BG);
    g2.fillRect(0, logicalBoardH, logicalBoardW, hudHeight);

    // HUD neon trim
    g2.setColor(NEON_CYAN);
    g2.drawLine(0, logicalBoardH, logicalBoardW, logicalBoardH);
    g2.setColor(NEON_PINK);
    g2.drawLine(0, logicalBoardH + 2, logicalBoardW, logicalBoardH + 2);

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
    g2.setColor(HUD_SCORE);
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

        // Small accent line
        g2.setColor(NEON_PINK);
        g2.drawLine(boxX + 20, boxY + 62, boxX + boxW - 20, boxY + 62);

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

    public void startGameLoop()
    {
        if(!timer.isRunning())
        {
            int speed = FPS_MS;
            double scale = speed / settings.getDifficultyScale(0);
            timer.setDelay((int)scale); //sets speed based on difficulty
        
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

    public void showFirstEntryInstructionsIfPending()
    {
        if (firstEntryInstructionsPending)
        {
            showInstructionsCard();
        }
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