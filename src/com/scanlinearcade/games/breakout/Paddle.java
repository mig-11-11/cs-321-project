//*****************************************************************************************************
// 
// Program Title: Paddle.java
// Project File: Breakout
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 02/2026
//
//*****************************************************************************************************
package com.scanlinearcade.games.breakout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Domain Model Class: Paddle
 *
 * <p>Intent: Models the player-controlled paddle, including horizontal movement,
 * boundary clamping, rendering, and collision shape exposure for ball physics.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public Paddle(int startX, int startY, int width, int height)}</li>
 *   <li>{@code public void update(boolean moveLeft, boolean moveRight, Rectangle bounds)}</li>
 *   <li>{@code public void draw(Graphics2D g2)}</li>
 *   <li>{@code public Rectangle getRect()}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class Paddle {
	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;

	/**
	 * Creates a paddle with the given starting position and dimensions.
	 * Signature: {@code public Paddle(int startX, int startY, int width, int height)}
	 *
	 * @param startX initial x-coordinate
	 * @param startY initial y-coordinate
	 * @param width paddle width in pixels
	 * @param height paddle height in pixels
	 */
	public Paddle(int startX, int startY, int width, int height) {
		this.x = startX;
		this.y = startY;
		this.width = width;
		this.height = height;
		this.speed = 10;
	}

	/**
	 * Updates paddle position based on movement input and constrains it to bounds.
	 * Signature: {@code public void update(boolean moveLeft, boolean moveRight, Rectangle bounds)}
	 *
	 * @param moveLeft whether left movement is active
	 * @param moveRight whether right movement is active
	 * @param bounds allowed movement area
	 */
	public void update(boolean moveLeft, boolean moveRight, Rectangle bounds) {
		if (moveLeft) {
			x -= speed;
		}
		if (moveRight) {
			x += speed;
		}

		if (x < bounds.x) {
			x = bounds.x;
		} else if (x + width > bounds.x + bounds.width) {
			x = bounds.x + bounds.width - width;
		}
	}

	/**
	 * Draws the paddle.
	 * Signature: {@code public void draw(Graphics2D g2)}
	 *
	 * @param g2 graphics context
	 */
	public void draw(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.fillRect(x, y, width, height);
	}

	/**
	 * Returns the paddle collision rectangle.
	 * Signature: {@code public Rectangle getRect()}
	 *
	 * @return rectangle for collision checks
	 */
	public Rectangle getRect() {
		return new Rectangle(x, y, width, height);
	}
}
