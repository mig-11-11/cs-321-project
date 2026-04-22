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
import java.util.concurrent.ThreadLocalRandom;

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
	private static final Color[] SPAWN_COLORS = {
		new Color(255, 70, 230),
		new Color(200, 80, 255),
		new Color(90, 170, 255)
	};

	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;
	private Color paddleColor;

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
		this.paddleColor = randomSpawnColor();
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
		g2.setColor(withAlpha(paddleColor, 100));
		g2.fillRect(x - 2, y - 2, width + 4, height + 4);

		g2.setColor(paddleColor);
		g2.fillRect(x, y, width, height);

		g2.setColor(lighten(paddleColor, 0.45f));
		g2.fillRect(x + 4, y + 2, Math.max(8, width / 3), Math.max(2, height / 3));
	}

	private Color randomSpawnColor() {
		int index = ThreadLocalRandom.current().nextInt(SPAWN_COLORS.length);
		return SPAWN_COLORS[index];
	}

	private Color withAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	private Color lighten(Color color, float amount) {
		int r = color.getRed() + Math.round((255 - color.getRed()) * amount);
		int g = color.getGreen() + Math.round((255 - color.getGreen()) * amount);
		int b = color.getBlue() + Math.round((255 - color.getBlue()) * amount);
		return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
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
