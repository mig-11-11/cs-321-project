package com.scanlinearcade.games.spaceinvaders;

import javax.swing.ImageIcon;

/**
 * Enemy Entity Class: Alien
 * 
 * <p>Intent: The Alien class encapsulates position, visibility, and image data
 * for the aliens that the user has to shoot at, which is displayed by
 * the board class.
 * 
 */
public class Alien extends Sprite {

    private Bomb bomb;

    
    public Alien(int x, int y) {

        initAlien(x, y);
    }

    /**
     * Sets the alien fields by overriding the fields of Sprite.
     * @param x the x coordinate to be used by Alien
     * @param y the y coordinate to be used by Alien
     */
    private void initAlien(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(getClass().getResource("/com/scanlinearcade/games/images/AlienSpaceship.png"));

        setImage(ii.getImage());
    }

    /**
     * Moves the alien object on the board and changes its direction.
     * @param direction the direction in which alien is moving
     */
    public void act(int direction) {

        this.x += direction;
    }

    
    public Bomb getBomb() {

        return bomb;
    }

    /**
     * Alien Projectile Class: Bomb
     * 
     * Handles and stores enemy projectile data.
     */
    public class Bomb extends Sprite {

        private boolean destroyed;
        
        
        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        /**
         * Sets the fields, image, and coordinates of bomb.
         * 
         * @param x the x location of bomb on the board/panel
         * @param y the y location of bomb on the board/panel
         */
        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var ii = new ImageIcon(getClass().getResource("/com/scanlinearcade/games/images/bomb.png"));
            setImage(ii.getImage());
        }

        
        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        
        public boolean isDestroyed() {

            return destroyed;
        }
    }
}
