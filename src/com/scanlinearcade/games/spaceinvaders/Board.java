package com.scanlinearcade.games.spaceinvaders;

import com.scanlinearcade.app.ArcadeFrame;
import com.scanlinearcade.app.GameOverDialog;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.swing.SwingUtilities;

/**
 * handles operations of the game, displays game for user
 * @author RayCa
 */
public class Board extends JPanel {

    private static final int HUD_HEIGHT = 48;
    private static final int TOTAL_HEIGHT = Commons.BOARD_HEIGHT + HUD_HEIGHT;

    // HUD colors
    private static final Color HUD_BG = new Color(58, 58, 62);
    private static final Color HUD_TEXT = new Color(235, 235, 235);

    // Border color
    private static final Color BOARD_BORDER = new Color(0, 255, 200, 170);

    // Instruction card colors
    private static final Color INSTRUCTION_DIM = new Color(0, 0, 0, 110);
    private static final Color INSTRUCTION_BOX_BG = new Color(10, 16, 30, 220);
    private static final Color INSTRUCTION_BOX_BORDER = new Color(0, 255, 200, 120);
    private static final Color INSTRUCTION_TITLE = new Color(230, 245, 255);
    private static final Color INSTRUCTION_TEXT = new Color(220, 225, 230);

    private List<Alien> aliens;
    private Player player;
    private Shot shot;
    
    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String explImg = "src/com/scanlinearcade/games/images/explosion.png";
    private String message = "Game Over";

    private Timer timer;
    private final Runnable returnToHubAction;
    private boolean gameOverDialogShown;
    private boolean paused;
    private boolean showingInstructionsCard;
    private String currentRunToken;

    /**
     * runs board and sets game components on board
     */
    public Board() {
        this(null);
    }

    public Board(Runnable returnToHubAction) 
    {

        this.returnToHubAction = returnToHubAction;

        initBoard();
        
    }

    /**
     * sets template for board
     */
    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        setPreferredSize(new Dimension(Commons.BOARD_WIDTH, TOTAL_HEIGHT));
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
       
        gameInit();
    }


    /**
     * sets board for player and aliens
     */
    private void gameInit() {

        aliens = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {

                var alien = new Alien(Commons.ALIEN_INIT_X + 18 * j,
                        Commons.ALIEN_INIT_Y + 18 * i);
                aliens.add(alien);
            }
        }

        player = new Player();
        shot = new Shot();
        deaths = 0;
        direction = -1;
        inGame = true;
        paused = false;
        showingInstructionsCard = false;
        message = "Game Over";
        gameOverDialogShown = false;
        currentRunToken = UUID.randomUUID().toString();
    }

    /**
     * displays aliens on screen
     * @param g 
     */
    private void drawAliens(Graphics g) {

        for (Alien alien : aliens) {

            if (alien.isVisible()) {

                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying()) {

                alien.die();
            }
        }
    }

    /**
     * displays player onscreen
     * @param g 
     */
    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    /**
     * displays graphics for shot from player
     * @param g 
     */
    private void drawShot(Graphics g) {

        if (shot.isVisible()) {

            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    /**
     * displays bomb graphics from enemies/aliens
     * @param g 
     */
    private void drawBombing(Graphics g) {

        for (Alien a : aliens) {

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {

                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int logicalBoardW = Commons.BOARD_WIDTH;
        int logicalBoardH = Commons.BOARD_HEIGHT;
        int logicalTotalH = TOTAL_HEIGHT;

        int panelW = getWidth();
        int panelH = getHeight();

        double scaleX = (double) panelW / logicalBoardW;
        double scaleY = (double) panelH / logicalTotalH;
        double scale = Math.min(scaleX, scaleY) * 0.90;

        int drawW = (int) Math.round(logicalBoardW * scale);
        int drawH = (int) Math.round(logicalTotalH * scale);
        int offsetX = (panelW - drawW) / 2;
        int offsetY = (panelH - drawH) / 2;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, panelW, panelH);

        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);

        doDrawing(g2);

        g2.dispose();
    }

    /**
     * 
     * @param g 
     */
    private void doDrawing(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.black);
        g2.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        // visible border around the board
        g2.setColor(BOARD_BORDER);
        g2.drawRect(0, 0, Commons.BOARD_WIDTH - 1, Commons.BOARD_HEIGHT - 1);

        g2.setColor(Color.green);

        if (inGame) {

            g2.drawLine(0, Commons.GROUND,
                    Commons.BOARD_WIDTH, Commons.GROUND);

            drawAliens(g2);
            drawPlayer(g2);
            drawShot(g2);
            drawBombing(g2);

            } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            if (!gameOverDialogShown) {
                gameOverDialogShown = true;
                SwingUtilities.invokeLater(this::showSharedGameOverMenu);
            }
        }

        drawHud(g2);

        if (showingInstructionsCard) {
            drawInstructionsOverlay(g2);
        }

        Toolkit.getDefaultToolkit().sync();
    }


    /**
     * updates the board for enemies, player, and shots
     */
    private void update() {

        if (paused) {
            return;
        }

        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {

            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        // shot
        if (shot.isVisible()) {

            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Alien alien : aliens) {

                int alienX = alien.getX();
                int alienY = alien.getY();

                if (alien.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX)
                            && shotX <= (alienX + Commons.ALIEN_WIDTH)
                            && shotY >= (alienY)
                            && shotY <= (alienY + Commons.ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(explImg);
                        alien.setImage(ii.getImage());
                        alien.setDying(true);
                        deaths++;
                        shot.die();
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        // aliens

        for (Alien alien : aliens) {

            int x = alien.getX();

            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {

                direction = -1;

                Iterator<Alien> i1 = aliens.iterator();

                while (i1.hasNext()) {

                    Alien a2 = i1.next();
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }

            if (x <= Commons.BORDER_LEFT && direction != 1) {

                direction = 1;

                Iterator<Alien> i2 = aliens.iterator();

                while (i2.hasNext()) {

                    Alien a = i2.next();
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        Iterator<Alien> it = aliens.iterator();

        while (it.hasNext()) {

            Alien alien = it.next();

            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }

                alien.act(direction);
            }
        }

        // bombs
        var generator = new Random();

        for (Alien alien : aliens) {

            int shot = generator.nextInt(15);
            Alien.Bomb bomb = alien.getBomb();

            if (shot == Commons.CHANCE && alien.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(alien.getX());
                bomb.setY(alien.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()) {

                if (bombX >= (playerX)
                        && bombX <= (playerX + Commons.PLAYER_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + Commons.PLAYER_HEIGHT)) {

                    var ii = new ImageIcon(explImg);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }

            if (!bomb.isDestroyed()) {

                bomb.setY(bomb.getY() + 1);

                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {

                    bomb.setDestroyed(true);
                }
            }
        }
    }

    /**
     * 
     */
    private void doGameCycle() {

        update();
        repaint();
    }

    private void showSharedGameOverMenu() {
        GameOverDialog.showDialog(
                this,
                "spaceinvaders",
                currentRunToken,
                message,
                deaths * 10,
                this::restartFromDialog,
                this::returnToHubFromDialog
        );
    }

    private void restartFromDialog() 
    {
        resetGame();
        startGameLoop();
    }

    private void returnToHubFromDialog() {
        if (returnToHubAction != null) {
            returnToHubAction.run();
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        new ArcadeFrame().setVisible(true);
        if (window != null) {
            window.dispose();
        }
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    /**
     * 
     */
    private class TAdapter extends KeyAdapter {

        
        @Override
        public void keyReleased(KeyEvent e) {

            if (paused) {
                return;
            }

            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (showingInstructionsCard) {
                if (key == KeyEvent.VK_M) {
                    returnToHubFromDialog();
                    return;
                }

                paused = false;
                showingInstructionsCard = false;
                repaint();
                return;
            }

            if (key == KeyEvent.VK_I && inGame) {
                showInstructionsCard();
                return;
            }

            if (paused) {
                return;
            }

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            if (key == KeyEvent.VK_SPACE) {

                if (inGame) {

                    if (!shot.isVisible()) {

                        shot = new Shot(x, y);
                    }
                }
            }
        }
    }

    private void drawHud(Graphics2D g2) {

        g2.setColor(HUD_BG);
        g2.fillRect(0, Commons.BOARD_HEIGHT, Commons.BOARD_WIDTH, HUD_HEIGHT);

        g2.setFont(new Font("Consolas", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        int hudBaseline = Commons.BOARD_HEIGHT + ((HUD_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();

        String pauseText = "[Esc] Pause Menu";
        String instructionsText = "[I] Instructions";
        String scoreText = "Score: " + (deaths * 10);

        g2.setColor(HUD_TEXT);
        g2.drawString(pauseText, 8, hudBaseline);

        int instructionsX = (Commons.BOARD_WIDTH - fm.stringWidth(instructionsText)) / 2;
        g2.drawString(instructionsText, instructionsX, hudBaseline);

        int scoreX = Commons.BOARD_WIDTH - fm.stringWidth(scoreText) - 8;
        g2.drawString(scoreText, scoreX, hudBaseline);
    }

    private void drawInstructionsOverlay(Graphics2D g2) {

        int boxX = 24;
        int boxY = 20;
        int boxW = Commons.BOARD_WIDTH - 48;
        int boxH = Commons.BOARD_HEIGHT - 40;

        g2.setColor(INSTRUCTION_DIM);
        g2.fillRoundRect(boxX + 6, boxY + 6, boxW, boxH, 24, 24);

        g2.setColor(INSTRUCTION_BOX_BG);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 24, 24);

        g2.setColor(INSTRUCTION_BOX_BORDER);
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 24, 24);

        g2.setColor(INSTRUCTION_TITLE);
        g2.setFont(new Font("Consolas", Font.BOLD, 13));
        drawCenteredLine(g2, "Space Invaders Instructions", boxY + 28);

        g2.setColor(INSTRUCTION_TEXT);
        g2.setFont(new Font("Consolas", Font.PLAIN, 8));
        drawCenteredLine(g2, "Destroy all aliens before they reach the ground.", boxY + 72);
        drawCenteredLine(g2, "Move: [Left/Right]", boxY + 104);
        drawCenteredLine(g2, "Shoot: [Space]", boxY + 130);
        drawCenteredLine(g2, "Pause Menu: [Esc]", boxY + 156);
        drawCenteredLine(g2, "Instructions: [I]", boxY + 182);
        drawCenteredLine(g2, "Press any key to start / continue", boxY + 230);
        drawCenteredLine(g2, "Press [M] to return to the main menu", boxY + 256);
    }

    private void drawCenteredLine(Graphics2D g2, String text, int y) {
        int x = (Commons.BOARD_WIDTH - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }
    
    public void resetGame()
    {
        gameInit();
        repaint();
    }

    public void startGameLoop()
    {
        if (!timer.isRunning())
        {
            timer.start();
        }

        requestFocusInWindow();
    }

    public void stopGameLoop()
    {
        if (timer.isRunning())
        {
            timer.stop();
        }
    } 

    public void showInstructionsCard()
    {
        if (!inGame)
        {
            return;
        }

        paused = true;
        showingInstructionsCard = true;
        repaint();
    }

    public boolean isShowingInstructionsCard()
    {
        return showingInstructionsCard;
    }
    
}