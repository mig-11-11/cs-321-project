package com.scanlinearcade.games.spaceinvaders;

import javax.swing.ImageIcon;

/**
 * 
 * @author RayCa
 */
public class Alien extends Sprite {

    private Bomb bomb;

    /**
     * runs alien credentials
     * @param x the x coordinate to be used by Alien
     * @param y the y coordinate to be used by ALIEN
     */
    public Alien(int x, int y) {

        initAlien(x, y);
    }

    /**
     * sets the alien fields
     * @param x the x coordinate to be used by Alien
     * @param y the y coordinate to be used by Alien
     */
    private void initAlien(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var alienImg = "src/com/scanlinearcade/games/images/AlienSpaceship.png";
        var ii = new ImageIcon(alienImg);

        setImage(ii.getImage());
    }

    /**
     * moves the alien
     * @param direction the direction in which alien is moving
     */
    public void act(int direction) {

        this.x += direction;
    }

    /**
     * 
     * @return bomb object
     */
    public Bomb getBomb() {

        return bomb;
    }

    /**
     * handles and stores enemy projectile data
     */
    public class Bomb extends Sprite {

        private boolean destroyed;

        /**
         * runs initBomb method using parameters
         * @param x the x location of bomb on the board/panel
         * @param y the y location of bomb on the board/panel
         */
        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        /**
         * sets the coordinates of bomb
         * @param x
         * @param y 
         */
        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/com/scanlinearcade/games/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        /**
         * 
         * @param destroyed 
         */
        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        /**
         * checks to see if alien is destroyed or not
         * @return true alien is destroyed
         */
        public boolean isDestroyed() {

            return destroyed;
        }
    }
}
