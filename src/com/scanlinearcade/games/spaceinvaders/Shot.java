package com.scanlinearcade.games.spaceinvaders;

import javax.swing.ImageIcon;

public class Shot extends Sprite {

    public Shot() {
    }

    /**
     * runs initShot()
     * @param x x coordinate for shot on board
     * @param y y coordinate for shot on board
     */
    public Shot(int x, int y) {

        initShot(x, y);
    }

    /**
     * sets the x and y coordinates and image of shot
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
