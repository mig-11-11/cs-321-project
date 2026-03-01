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

public class SnakeModel{

    private final int Columns;
    private final int rows;
    private final Random rng = new Random();

    private final Deque<Point> snake = new ArrayDeque<>();
    private Direction dir = Direction.RIGHT;
    private Direction pendingDir = Direction.RIGHT;

    private Point food;
    private boolean gameOver = false;
    private int score = 0;

    public SnakeModel(int cols, int rows) {
        this.Columns = cols;
        this.rows = rows;
        reset();
    }

    public void reset() {
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

    public void setDirection(Direction newDir) {
        // Disallow instant 180-degree turns
        if (newDir != null && !newDir.Opposite(dir)) {
            pendingDir = newDir;
        }
    }

    public void step() {
        if (gameOver) return;

        dir = pendingDir;

        Point head = snake.peekFirst();
        Point next = new Point(head);

        switch (dir) {
            case UP -> next.y -= 1;
            case DOWN -> next.y += 1;
            case LEFT -> next.x -= 1;
            case RIGHT -> next.x += 1;
        }

        // Wall collision
        if (next.x < 0 || next.x >= Columns || next.y < 0 || next.y >= rows) {
            gameOver = true;
            return;
        }

        // Self collision
        if (snakeContains(next)) {
            gameOver = true;
            return;
        }

        // Move head
        snake.addFirst(next);

        // Eat food?
        if (next.equals(food)) {
            score += 10;
            spawnFood(); // grow: don't remove tail
        } else {
            snake.removeLast(); // normal move: keep same length
        }
    }

    private boolean snakeContains(Point p) 
    {
        for(Point seg : snake) 
        {
            if (seg.equals(p)) return true;
        }
        return false;
    }

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


