package com.scanlinearcade.games.spaceinvaders;

import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;

/**
 * User Control Class: Player
 * 
 * <p>Intent: The Player class encapsulates the position, movement, visibility, and
 * image data for the player. The board will use a player object, which the user
 * will control. The user interacts with the game through the player object, and
 * the Player class is responsible for handling the key inputs from the user.
 * 
 */
public class Player extends Sprite {

    private int width;

    
    public Player() {

        initPlayer();
    }

    /**
     * Sets the player fields and coordinates by overriding Sprite fields.
     */
    private void initPlayer() {

        var playerImg = "src/com/scanlinearcade/games/images/player.png";
        var ii = new ImageIcon(playerImg);

        width = ii.getImage().getWidth(null);
        setImage(ii.getImage());

        int START_X = 270;
        setX(START_X);

        int START_Y = 280;
        setY(START_Y);
    }

    /**
     * Moves the player object on the board.
     */
    public void act() {

        x += dx;

        if (x <= 2) {

            x = 2;
        }

        if (x >= Commons.BOARD_WIDTH - 2 * width) {

            x = Commons.BOARD_WIDTH - 2 * width;
        }
    }

    /**
     * On the board, moves player's ship to the left when user clicks left arrow
     * and moves ship to right when user clicks right arrow.
     * @param e detected key being pressed
     */
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {

            dx = -2;
        }

        if (key == KeyEvent.VK_RIGHT) {

            dx = 2;
        }
    }

    /**
     * On the board, stops the player's ship when selected keys are released.
     * @param e detected key being released
     */
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {

            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {

            dx = 0;
        }
    }
}
