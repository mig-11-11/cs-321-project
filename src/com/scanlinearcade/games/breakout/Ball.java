//*****************************************************************************************************
// 
// Program Title: Ball.java
// Project File: Breakout
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 02/2026
//
//*****************************************************************************************************
package com.scanlinearcade.games.breakout;

import com.scanlinearcade.app.GameSettings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Domain Model Class: Ball
 *
 * <p>Intent: Models the moving ball entity in Breakout, including position,
 * velocity, speed progression, frame-to-frame motion, and bounce behavior when
 * contacting boundaries, the paddle, and bricks.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public Ball(int startX, int startY, int radius)}</li>
 *   <li>{@code public void reset(int startX, int startY)}</li>
 *   <li>{@code public void reset(int startX, int startY, boolean resetSpeed)}</li>
 *   <li>{@code public boolean update(Rectangle bounds, Paddle paddle, Bricks bricks, BreakoutScore score)}</li>
 *   <li>{@code public void draw(Graphics2D g2)}</li>
 *   <li>{@code public Rectangle getRect()}</li>
 *   <li>{@code public void bounceX()}</li>
 *   <li>{@code public void bounceY()}</li>
 *   <li>{@code public double getDx()}</li>
 *   <li>{@code public double getDy()}</li>
 *   <li>{@code public void increaseLevelSpeed()}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 * 
 * Goals: 
 * - Random ball trajectory upon initialization
 */
public class Ball {
	private static final Color[] SPAWN_COLORS = {
		new Color(255, 70, 230),
		new Color(200, 80, 255),
		new Color(90, 170, 255)
	};

	private double x;
	private double y;
	private double dx;
	private double dy;
	private int radius;
	private double speed;
	private double speedStep;
	private double maxSpeed;
        private Color ballColor;
        private GameSettings settings;

	/**
	 * Creates a ball and initializes it to the provided starting position.
	 * Signature: {@code public Ball(int startX, int startY, int radius)}
	 *
	 * @param startX initial x-coordinate of the ball center
	 * @param startY initial y-coordinate of the ball center
	 * @param radius radius of the ball in pixels
	 */
	public Ball(int startX, int startY, int radius, GameSettings settings) {
                this.settings = settings;
		this.radius = radius;
		this.speed = 6.0 * settings.getDifficultyScale(2);
		this.speedStep = 0.2;
		this.maxSpeed = 12.0 * settings.getDifficultyScale(2);
		reset(startX, startY);
	}

	/**
	 * Resets the ball to the provided position and default launch speed.
	 * Signature: {@code public void reset(int startX, int startY)}
	 *
	 * @param startX x-coordinate of the reset position
	 * @param startY y-coordinate of the reset position
	 */
	public void reset(int startX, int startY) {
		reset(startX, startY, true);
	}

	public void reset(int startX, int startY, boolean resetSpeed) {
		this.x = startX;
		this.y = startY;
		this.ballColor = randomSpawnColor();
		if (resetSpeed) {
			this.speed = 6.0 * settings.getDifficultyScale(2);
		}
		setRandomLaunchDirection();
	}

	/**
	 * Advances the ball by one frame and resolves collisions.
	 * Signature: {@code public boolean update(Rectangle bounds, Paddle paddle, Bricks bricks, BreakoutScore score)}
	 *
	 * @param bounds rectangular play area boundaries
	 * @param paddle paddle to collide with, if any
	 * @param bricks brick field to collide with, if any
	 * @param score score model to update on brick hits
	 * @return {@code true} when the ball falls below the bottom bound, otherwise {@code false}
	 */
	public boolean update(Rectangle bounds, Paddle paddle, Bricks bricks, BreakoutScore score) {
		x += dx;
		y += dy;

		if (x - radius < bounds.x) {
			x = bounds.x + radius;
			dx = Math.abs(dx);
		} else if (x + radius > bounds.x + bounds.width) {
			x = bounds.x + bounds.width - radius;
			dx = -Math.abs(dx);
		}

		if (y - radius < bounds.y) {
			y = bounds.y + radius;
			dy = Math.abs(dy);
		}

		if (paddle != null && dy > 0 && getRect().intersects(paddle.getRect())) {
			Rectangle pr = paddle.getRect();
			y = pr.y - radius;

			double hit = (x - (pr.x + pr.width / 2.0)) / (pr.width / 2.0);
			hit = Math.max(-1.0, Math.min(1.0, hit));

			double maxX = speed * 0.9;
			dx = hit * maxX;
			double dyMagnitude = Math.sqrt(Math.max(0.001, speed * speed - dx * dx));
			dy = -dyMagnitude;
		}

		if (bricks != null) {
			bricks.handleCollision(this, score);
		}

		return y - radius > bounds.y + bounds.height;
	}

	/**
	 * Draws the ball.
	 * Signature: {@code public void draw(Graphics2D g2)}
	 *
	 * @param g2 graphics context used for rendering
	 */
	public void draw(Graphics2D g2) {
		int drawX = (int) (x - radius);
		int drawY = (int) (y - radius);
		int size = radius * 2;

		Color base = ballColor == null ? SPAWN_COLORS[0] : ballColor;

		g2.setColor(withAlpha(base, 120));
		g2.fillOval(drawX - 2, drawY - 2, size + 4, size + 4);

		g2.setColor(base);
		g2.fillOval(drawX, drawY, size, size);

		g2.setColor(lighten(base, 0.45f));
		g2.fillOval(drawX + (radius / 2), drawY + (radius / 2), radius, radius);
	}

	/**
	 * Returns the current collision rectangle of the ball.
	 * Signature: {@code public Rectangle getRect()}
	 *
	 * @return rectangle enclosing the rendered ball
	 */
	public Rectangle getRect() {
		return new Rectangle((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
	}

	/**
	 * Reverses horizontal velocity.
	 * Signature: {@code public void bounceX()}
	 */
	public void bounceX() {
		dx = -dx;
	}

	/**
	 * Reverses vertical velocity.
	 * Signature: {@code public void bounceY()}
	 */
	public void bounceY() {
		dy = -dy;
	}

	/**
	 * Returns horizontal velocity.
	 * Signature: {@code public double getDx()}
	 *
	 * @return horizontal speed component
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * Returns vertical velocity.
	 * Signature: {@code public double getDy()}
	 *
	 * @return vertical speed component
	 */
	public double getDy() {
		return dy;
	}

	public void increaseLevelSpeed() {
		increaseSpeed();
	}

	private void setDirection(double dirX, double dirY) {
		double length = Math.sqrt(dirX * dirX + dirY * dirY);
		if (length == 0.0) {
			dx = speed;
			dy = -speed;
			return;
		}
		dx = (dirX / length) * speed;
		dy = (dirY / length) * speed;
	}

	private void setRandomLaunchDirection() {
		double dirX = ThreadLocalRandom.current().nextDouble(-0.85, 0.85);
		if (Math.abs(dirX) < 0.2) {
			dirX = dirX < 0.0 ? -0.2 : 0.2;
		}
		setDirection(dirX, -1.0);
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

	private void increaseSpeed() {
		if (speed >= maxSpeed) {
			return;
		}
		speed = Math.min(maxSpeed, speed + speedStep);
		double length = Math.sqrt(dx * dx + dy * dy);
		if (length > 0.0) {
			dx = (dx / length) * speed;
			dy = (dy / length) * speed;
		}
	}
}
