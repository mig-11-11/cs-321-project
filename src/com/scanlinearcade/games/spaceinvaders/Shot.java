package com.scanlinearcade.games.spaceinvaders;

import javax.swing.ImageIcon;

/**
 * Represents a projectile fired by the player in the Space Invaders game.
 * 
 * <p>Intent: A shot is typically created when the player fires and moves upward on the screen
 * until it either collides with an enemy or leaves the game area.
 * This class is responsible for initializing the shot’s image and
 * positioning relative to the player’s current location. Movement and
 * collision handling are managed by the game board.
 */
public class Shot extends Sprite {

    public Shot() {
    }
    
    
    public Shot(int x, int y) {

        initShot(x, y);
    }

    /**
     * Sets the x, y coordinates and image of shot.
     * @param x x coordinate for shot on board
     * @param y y coordinate for shot on board
     */
    private void initShot(int x, int y) {

        var shotImg = "src/com/scanlinearcade/games/images/shot.png";
        var ii = new ImageIcon(shotImg);
        setImage(ii.getImage());

        int H_SPACE = 6;
        setX(x + H_SPACE);

        int V_SPACE = 1;
        setY(y - V_SPACE);
    }
}
