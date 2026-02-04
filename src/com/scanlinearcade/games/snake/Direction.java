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

public enum Direction 
{
    UP,     // UP    = to a Decrease of Y on the grid
    DOWN,   // DOWN  = to a Increase of Y on the grid
    LEFT,   // LEFT  = to a Decrease of X on the grid 
    RIGHT;  // RIGHT = to a Increase of X on the grid
    
   
    public boolean Opposite (Direction other)
    {
       return (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN) ||  
              (this == UP && other == DOWN);    
    }
}
    