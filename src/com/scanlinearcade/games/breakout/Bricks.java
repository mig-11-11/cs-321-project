//*****************************************************************************************************
// 
// Program Title: Bricks.java
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
 * Domain Model Class: Bricks
 *
 * <p>Intent: Represents the brick matrix for a level, tracks which bricks are
 * still active, resolves ball-brick collision outcomes, and reports level-clear
 * state when all bricks are removed.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public Bricks(int rows, int cols, int panelWidth, int topOffset)}</li>
 *   <li>{@code public boolean handleCollision(Ball ball, BreakoutScore score)}</li>
 *   <li>{@code public boolean isCleared()}</li>
 *   <li>{@code public void draw(Graphics2D g2)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
* 	Goals:
* - Add function for spawning more bricks after the first set is cleared, and add a way to track how many sets have been cleared to increase difficulty.
 */
public class Bricks {
	private boolean[][] alive;
	private int rows;
	private int cols;
	private int brickWidth;
	private int brickHeight;
	private int gap;
	private int offsetX;
	private int offsetY;

	/**
	 * Creates a brick grid sized to fit the panel width.
	 * Signature: {@code public Bricks(int rows, int cols, int panelWidth, int topOffset)}
	 *
	 * @param rows number of brick rows
	 * @param cols number of brick columns
	 * @param panelWidth width of the playable panel
	 * @param topOffset vertical offset from the top where the grid starts
	 */
	public Bricks(int rows, int cols, int panelWidth, int topOffset) {
		this.rows = rows;
		this.cols = cols;
		this.gap = 4;
		this.offsetX = 40;
		this.offsetY = topOffset;
		this.brickHeight = 18;

		int totalGap = (cols - 1) * gap;
		int availableWidth = panelWidth - (offsetX * 2) - totalGap;
		this.brickWidth = Math.max(10, availableWidth / cols);

		alive = new boolean[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				alive[r][c] = true;
			}
		}
	}

	/**
	 * Checks whether the ball collides with any live brick and resolves the hit.
	 * Signature: {@code public boolean handleCollision(Ball ball, BreakoutScore score)}
	 *
	 * @param ball current ball
	 * @param score score model to increment when a brick is destroyed
	 * @return {@code true} if a collision occurred and a brick was removed
	 */
	public boolean handleCollision(Ball ball, BreakoutScore score) {
		Rectangle ballRect = ball.getRect();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (!alive[r][c]) {
					continue;
				}
				Rectangle brickRect = getBrickRect(r, c);
				if (ballRect.intersects(brickRect)) {
					alive[r][c] = false;
					if (score != null) {
						score.addScore(50);
					}

					Rectangle overlap = ballRect.intersection(brickRect);
					if (overlap.width >= overlap.height) {
						ball.bounceY();
					} else {
						ball.bounceX();
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Indicates whether all bricks have been cleared.
	 * Signature: {@code public boolean isCleared()}
	 *
	 * @return {@code true} when no bricks remain alive
	 */
	public boolean isCleared() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (alive[r][c]) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Draws all currently alive bricks.
	 * Signature: {@code public void draw(Graphics2D g2)}
	 *
	 * @param g2 graphics context
	 */
	public void draw(Graphics2D g2) {
		g2.setColor(new Color(200, 120, 40));
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (!alive[r][c]) {
					continue;
				}
				Rectangle rect = getBrickRect(r, c);
				g2.fillRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(Color.BLACK);
				g2.drawRect(rect.x, rect.y, rect.width, rect.height);
				g2.setColor(new Color(200, 120, 40));
			}
		}
	}

	private Rectangle getBrickRect(int row, int col) {
		int x = offsetX + col * (brickWidth + gap);
		int y = offsetY + row * (brickHeight + gap);
		return new Rectangle(x, y, brickWidth, brickHeight);
	}
}
