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

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
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
    /**
     * Constructs the Snake panel, initializes the update loop, and registers keyboard input.
     *
     * <p>Key bindings:</p>
     * <ul>
     *   <li>Move: Arrow keys or WASD</li>
     *   <li>Restart: R</li>
     *   <li>Pause/Resume: Space</li>
     * </ul>
     *
     * <pre>
     * public SnakePanel()
     * </pre>
     */
  
    public SnakePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);

        // Game loop, updates the model then repaints the panel
        timer = new Timer(FPS_MS, e -> 
        {
            model.step();
            repaint();
        });
        timer.start();

        // Keyboard input tp mopel actions
        addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> model.setDirection(Direction.UP);
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> model.setDirection(Direction.DOWN);
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> model.setDirection(Direction.LEFT);
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> model.setDirection(Direction.RIGHT);

                    case KeyEvent.VK_R -> model.reset(); // restart
                    case KeyEvent.VK_SPACE -> togglePause();
                    default -> { }
                }
            }
        });
    }

    
      /**
     * Returns the preferred size of the panel based on the grid size plus HUD space.
     *
     * <pre>
     * public Dimension getPreferredSize()
     * </pre>
     *
     * @return preferred panel size (grid area + HUD bar)
     */
    @Override
    public Dimension getPreferredSize() 
    {
        // Add extra space for HUD text
        return new Dimension(COLS * CELL, ROWS * CELL + 40);
    }

    private void togglePause() 
    {
        if (timer.isRunning()) timer.stop();
        else timer.start();
        repaint();
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
        g2.drawString("Score: " + model.getScore() + "   [Space]=Pause  [R]=Restart", 10, boardH + 25);

        // Overlay for game over
        if (model.isGameOver()) {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRect(0, 0, boardW, boardH);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Consolas", Font.BOLD, 36));
            drawCentered(g2, "GAME OVER", new Rectangle(0, 0, boardW, boardH - 20));

            g2.setFont(new Font("Consolas", Font.PLAIN, 18));
            drawCentered(g2, "Press R to restart", new Rectangle(0, 40, boardW, boardH));
        }

        g2.dispose();
    }

    /**
     * Draws a string centered inside the given rectangle using the current font.
     *
     * <pre>
     * private static void drawCentered(Graphics2D g2, String text, Rectangle rect)
     * </pre>
     *
     * @param g2 graphics context used to render text
     * @param text text to draw
     * @param rect rectangle to center the text within
     */
    private static void drawCentered(Graphics2D g2, String text, Rectangle rect) {
        FontMetrics fm = g2.getFontMetrics();
        int x = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int y = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
    }
    
   
    
    
   
        public void startGameLoop()
    {
        if (!timer.isRunning())
        {
            timer.start();
        }
        requestFocusInWindow();
    }

    public void stopGameLoop()
    {
        if (timer.isRunning())
        {
            timer.stop();
        }
    }

    public void resetGame()
    {
        model.reset();
        repaint();
    }
    
}



