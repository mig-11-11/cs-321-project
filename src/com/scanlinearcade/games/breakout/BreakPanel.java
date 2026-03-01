//*****************************************************************************************************
// 
// Program Title: BreakPanel.java
// Project File: Breakout
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 02/2026
//
//*****************************************************************************************************
package com.scanlinearcade.games.breakout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class Outline: BreakPanel
 *
 * <p>Intent: Coordinates the playable Breakout screen by managing the frame timer,
 * input state, game update cycle, and rendering pipeline for game entities and
 * end-state messages.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public BreakPanel()}</li>
 *   <li>{@code public void actionPerformed(ActionEvent e)}</li>
 *   <li>{@code public void keyTyped(KeyEvent e)}</li>
 *   <li>{@code public void keyPressed(KeyEvent e)}</li>
 *   <li>{@code public void keyReleased(KeyEvent e)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 *
 * <p>Additional Internal Signatures:
 * <ul>
 *   <li>{@code private void initGame()}</li>
 *   <li>{@code private void resetRound()}</li>
 *   <li>{@code protected void paintComponent(Graphics g)}</li>
 *   <li>{@code private void drawCenteredText(Graphics2D g2, String text, int y, int size)}</li>
 * </ul>
 *
 * Goals:
 * - Add pausing functionality (variable isPaused), and a pause menu with options to resume, restart, or quit to main menu
 */
public class BreakPanel extends JPanel implements ActionListener, KeyListener {
	public static final int PANEL_WIDTH = 800;
	public static final int PANEL_HEIGHT = 600;

	private Ball ball;
	private Paddle paddle;
	private Bricks bricks;
	private BreakoutScore score;
	private Timer timer;
	private boolean running;
	private boolean leftPressed;
	private boolean rightPressed;

	/**
	 * Creates the panel, initializes game state, and starts the update timer.
	 * Signature: {@code public BreakPanel()}
	 */
	public BreakPanel() {
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setBackground(Color.BLACK);
		setFocusable(true);
		addKeyListener(this);

		initGame();

		timer = new Timer(16, this);
		timer.start();
	}

	/**
	 * Initializes a new game session including score, brick layout, and round state.
	 * Signature: {@code private void initGame()}
	 */
	private void initGame() {
		score = new BreakoutScore(3);
		bricks = new Bricks(5, 10, PANEL_WIDTH, 60);
		resetRound();
		running = true;
	}

	/**
	 * Resets round-specific objects (paddle and ball) after life loss or game start.
	 * Signature: {@code private void resetRound()}
	 */
	private void resetRound() {
		paddle = new Paddle(PANEL_WIDTH / 2 - 45, PANEL_HEIGHT - 40, 90, 12);
		ball = new Ball(PANEL_WIDTH / 2, PANEL_HEIGHT - 60, 8);
	}

	/**
	 * Timer callback that advances game state and triggers repaint.
	 * Signature: {@code public void actionPerformed(ActionEvent e)}
	 *
	 * @param e timer event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			Rectangle bounds = new Rectangle(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
			paddle.update(leftPressed, rightPressed, bounds);

			boolean lost = ball.update(bounds, paddle, bricks, score);
			if (lost) {
				score.loseLife();
				if (score.getLives() > 0) {
					resetRound();
				} else {
					running = false;
				}
			}

			if (bricks.isCleared()) {
				running = false;
			}
		}
		repaint();
	}

	/**
	 * Paints gameplay objects and end-of-game messages.
	 * Signature: {@code protected void paintComponent(Graphics g)}
	 *
	 * @param g graphics context
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		bricks.draw(g2);
		paddle.draw(g2);
		ball.draw(g2);
		score.draw(g2, PANEL_WIDTH);

		if (!running) {
			String message = bricks.isCleared() ? "You Win!" : "Game Over";
			drawCenteredText(g2, message, PANEL_HEIGHT / 2 - 10, 28);
			drawCenteredText(g2, "Press Space to Restart", PANEL_HEIGHT / 2 + 20, 16);
		}
	}

	/**
	 * Draws horizontally centered text on the panel.
	 * Signature: {@code private void drawCenteredText(Graphics2D g2, String text, int y, int size)}
	 *
	 * @param g2 graphics context
	 * @param text text to draw
	 * @param y baseline y-coordinate
	 * @param size font size
	 */
	private void drawCenteredText(Graphics2D g2, String text, int y, int size) {
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("SansSerif", Font.BOLD, size));
		int textWidth = g2.getFontMetrics().stringWidth(text);
		int x = (PANEL_WIDTH - textWidth) / 2;
		g2.drawString(text, x, y);
	}

	/**
	 * Not used for this panel.
	 * Signature: {@code public void keyTyped(KeyEvent e)}
	 *
	 * @param e key event
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Handles key press state for paddle movement and restart input.
	 * Signature: {@code public void keyPressed(KeyEvent e)}
	 *
	 * @param e key event
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = true;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE && !running) {
			initGame();
		}
	}

	/**
	 * Handles key release state for paddle movement.
	 * Signature: {@code public void keyReleased(KeyEvent e)}
	 *
	 * @param e key event
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftPressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}
	}
}
