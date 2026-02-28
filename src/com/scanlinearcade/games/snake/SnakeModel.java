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


public class SnakeModel 
{
    private final int Col;
    private final int Row;
    private final Random rng = new Random();
    private final Deque<Point> snake = new ArrayDeque<>();
    private Direction facing = Direction.RIGHT;
    private Direction Pending_Face = Direction.RIGHT;
    
    public void reset()
    {
        snake.clear();
    }
    
    public SnakeModel(int Col, int Row)
    {
        this.Col = Col;
        this.Row = Row;
        reset();
        
    }
    
    
    
  
}


