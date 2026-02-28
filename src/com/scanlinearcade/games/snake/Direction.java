//*****************************************************************************************************
// Program Title: Direction.java
// Project File: 
// Name: Braden Gant
// Course Section: CS321-01 
// Date: 02/03/2026
// Program Description: Represents the four possible movements for Snake. The enum allows for the controller to determine how the snakes head position for each tick.
//*****************************************************************************************************


package com.scanlinearcade.games.snake;
public enum Direction 
{
    UP,     // UP    = to a Decrease of Y on the grid
    DOWN,   // DOWN  = to a Increase of Y on the grid
    LEFT,   // LEFT  = to a Decrease of X on the grid 
    RIGHT;  // RIGHT = to a Increase of X on the grid
    
   
    public boolean Opposite(Direction other) //This function checks to see if it is opposite of the direction
    {
       return (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN);    
    }
}
