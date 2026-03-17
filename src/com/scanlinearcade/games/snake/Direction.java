//*****************************************************************************************************
// Program Title: Direction.java
// Project File: 
// Name: Braden Gant
// Course Section: CS321-01 
// Date: 02/03/2026
// Program Description: Represents the four possible movements for Snake. The enum allows for the controller to determine how the snakes head position for each tick.
//*****************************************************************************************************

package com.scanlinearcade.games.snake;
/**
 * Represents the four movement directions in Snake.
 *
 * <p>Grid convention used by this project:
 * UP decreases {@code y}, DOWN increases {@code y},
 * LEFT decreases {@code x}, RIGHT increases {@code x}.</p>
 *
 * <p>This enum is part of the Snake domain model and is used by {@code SnakeModel}
 * to update the snake head position each game tick.</p>
 *
 * <h2>API Outline</h2>
 * <pre>
 * public enum Direction
 * public boolean Opposite(Direction other)
 * </pre>
 */

public enum Direction 
{
    UP,     // UP    = to a Decrease of Y on the grid
    DOWN,   // DOWN  = to a Increase of Y on the grid
    LEFT,   // LEFT  = to a Decrease of X on the grid 
    RIGHT;  // RIGHT = to a Increase of X on the grid
    
   
    /**
     * Checks whether the given direction is the direct opposite of this direction.
     *
     * <p>This supports a core Snake rule: the player cannot instantly reverse direction
     * (RIGHT → LEFT), because it would cause an immediate self-collision.</p>
     *
     * @param other the direction to compare against
     * @return true if {@code other} is opposite of {@code this}, false otherwise
     */
    
    public boolean Opposite(Direction other) //This function checks to see if it is opposite of the direction
    {
       return (this == UP && other == DOWN) ||  
              (this == DOWN && other == UP) ||  
              (this == LEFT && other == RIGHT) ||  
              (this == RIGHT && other == LEFT);    
    }
}
