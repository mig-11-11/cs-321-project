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
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Domain model for the Snake game. This class stores all game state and enforces
 * the core Snake rules:
 *
 * <ul>
 *   <li>snake movement on a 2D grid</li>
 *   <li>direction changes with a no-instant-reverse constraint</li>
 *   <li>food spawning that never overlaps the snake</li>
 *   <li>growth and scoring when food is eaten</li>
 *   <li>collision detection (wall and self) and game-over state</li>
 * </ul>
 *
 * <p><b>Design contract:</b></p>
 * <ul>
 *   <li>{@link #step()} is the only method that advances the game by one tick.</li>
 *   <li>{@link #setDirection(Direction)} rejects illegal 180-degree direction reversals.</li>
 *   <li>When {@link #isGameOver()} becomes {@code true}, {@link #step()} performs no further updates.</li>
 *   <li>Food is always placed on an empty grid cell (never on the snake).</li>
 * </ul>
 *
 * <h2>API Outline (public)</h2>
 * <pre>
 * public SnakeModel(int cols, int rows)
 * public void reset()
 * public void setDirection(Direction newDir)
 * public void step()
 * public int getCols()
 * public int getRows()
 * public Deque&lt;Point&gt; getSnake()
 * public Point getFood()
 * public boolean isGameOver()
 * public int getScore()
 * </pre>
 *
 * <h2>Internal Helpers (private)</h2>
 * <pre>
 * private boolean snakeContains(Point p)
 * private void spawnFood()
 * </pre>
 */
public class SnakeModel{

    private final int Columns; //Number of Columns
    private final int rows;    //Sumber ot Rows
    private final Random rng = new Random(); //Utilized Random for random food placement

    private final Deque<Point> snake = new ArrayDeque<>();
    private Direction dir = Direction.RIGHT;
    private Direction pendingDir = Direction.RIGHT;

    private Point food;
    private boolean gameOver = false;
    private int score = 0;

    
    /**
     * Constructs a SnakeModel with a fixed grid size and initializes a new game.
     *
     * <pre>
     * public SnakeModel(int cols, int rows)
     * </pre>
     *
     * @param cols number of columns in the grid
     * @param rows number of rows in the grid
     */
    public SnakeModel(int cols, int rows) 
    {
        this.Columns = cols;
        this.rows = rows;
        reset();
    }

    
    /**
     * Resets the game to its initial state
     * clears the snake body, places the snake at the center, resets score and gameOver,
     * sets direction to RIGHT, and spawns a new food item.
     *
     * <pre>
     * public void reset()
     * </pre>
     */
    public void reset() 
    {
        snake.clear();
        int startX = Columns / 2;
        int startY = rows / 2;

        // Start snake length 3 moving right
        snake.addFirst(new Point(startX, startY));
        snake.addLast(new Point(startX - 1, startY));
        snake.addLast(new Point(startX - 2, startY));

        dir = Direction.RIGHT;
        pendingDir = Direction.RIGHT;
        score = 0;
        gameOver = false;
        spawnFood();
    }

    /**
     * Requests a direction change that will be applied on the next tick.
     * Illegal 180-degree reversals are rejected 
     *
     * <pre>
     * public void setDirection(Direction newDir)
     * </pre>
     *
     * @param newDir requested direction (typically from keyboard input)
     */
    public void setDirection(Direction newDir) 
    {
        // Disallow instant 180-degree turns
        if (newDir != null && !newDir.Opposite(dir)) 
        {
            pendingDir = newDir;
        }
    }

    
    /**
     * Advances the game by one tick.
     *
     * <p>Tick sequence:</p>
     * <ol>
     *   <li>Apply pending direction</li>
     *   <li>Compute next head position</li>
     *   <li>Check wall collision and self-collision</li>
     *   <li>Move snake head forward</li>
     *   <li>If food eaten: increment score and spawn new food (growth)</li>
     *   <li>Else: remove tail to maintain length</li>
     * </ol>
     *
     * <pre>
     * public void step()
     * </pre>
     */
    public void step() 
    {
        if(gameOver) return;

        dir = pendingDir;

        Point head = snake.peekFirst();
        Point next = new Point(head);

        switch(dir) 
        {
            case UP -> next.y -= 1;
            case DOWN -> next.y += 1;
            case LEFT -> next.x -= 1;
            case RIGHT -> next.x += 1;
        }

        // Wall collision
        if(next.x < 0 || next.x >= Columns || next.y < 0 || next.y >= rows) 
        {
            gameOver = true;
            return;
        }

        // Self collision
        if(snakeContains(next)) 
        {
            gameOver = true;
            return;
        }

        // Move head
        snake.addFirst(next);

        // Eat food?
        if(next.equals(food)) 
        {
            score += 10;
            spawnFood(); // grow: don't remove tail
        } 
        else 
        {
            snake.removeLast(); // normal move: keep same length
        }
    }
    
    /**
     * Checks whether the given point is currently occupied by the snake body.
     *
     * <pre>
     * private boolean snakeContains(Point p)
     * </pre>
     *
     * @param p grid cell to test
     * @return true if any snake segment equals {@code p}; false otherwise
     */
    private boolean snakeContains(Point p) 
    {
        for(Point seg : snake) 
        {
            if (seg.equals(p)) return true;
        }
        return false;
    }

    /**
     * Spawns food at a random empty grid cell.
     *
     * <p>Design contract: this method must never place food on a cell occupied by the snake.</p>
     *
     * <pre>
     * private void spawnFood()
     * </pre>
     */
    private void spawnFood() 
    {
        while(true) 
        {
            int x = rng.nextInt(Columns);
            int y = rng.nextInt(rows);
            Point candidate = new Point(x, y);
            if(!snakeContains(candidate)) 
            {
                food = candidate;
                return;
            }
        }
    }

    // Getters (used by UI)
    public int getCols() 
    {
        return Columns; 
    }
    public int getRows() 
    { 
        return rows; 
    }
    public Deque<Point> getSnake() 
    { 
        return snake; 
    }
    public Point getFood() 
    { 
        return food; 
    }
    public boolean isGameOver() 
    { 
        return gameOver; 
    }
    public int getScore() 
    { 
        return score; 
    }
}


