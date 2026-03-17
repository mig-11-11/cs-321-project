package com.scanlinearcade.games.spaceinvaders;

import java.awt.Image;

/**
 * 
 * @author RayCa
 */
public class Sprite {

    private boolean visible;
    private Image image;
    private boolean dying;

    int x;
    int y;
    int dx;

    /**
     * sets the visibility of Sprite
     */
    public Sprite() {

        visible = true;
    }

    /**
     * changes the visibility of Sprite if sprite dies
     */
    public void die() {

        visible = false;
    }

    /**
     * 
     * @return if sprite is visible
     */
    public boolean isVisible() {

        return visible;
    }

    /**
     * sets the visibility of sprite
     * @param visible if sprite is visible
     */
    protected void setVisible(boolean visible) {

        this.visible = visible;
    }

    /**
     * sets the image for the display of sprite
     * @param image the image to be used to display sprite
     */
    public void setImage(Image image) {

        this.image = image;
    }

    /**
     * 
     * @return the image to be displayed by sprite
     */
    public Image getImage() {

        return image;
    }

    /**
     * sets the x coordinate for the sprite on board
     * @param x the x coordinates for the sprite on board
     */
    public void setX(int x) {

        this.x = x;
    }

    /**
     * sets the y coordinate for the sprite on board
     * @param y the y coordinate for the sprite on board
     */
    public void setY(int y) {

        this.y = y;
    }

    /**
     * 
     * @return the y coordinate for the sprite on board
     */
    public int getY() {

        return y;
    }

    /**
     * 
     * @return the x coordinate for the sprite on board
     */
    public int getX() {

        return x;
    }

    /**
     * sets the condition of sprite to be dead
     * @param dying is the sprite dead/gone on the board or not
     */
    public void setDying(boolean dying) {

        this.dying = dying;
    }

    /**
     * 
     * @return if the sprite is considered to be dead or gone
     */
    public boolean isDying() {

        return this.dying;
    }
}
