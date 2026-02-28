//*****************************************************************************************************
// 
// Program Title: BreakoutScore.java
// Project File: Breakout
// Name: Matteo Gomez
// Course Section: CS321-01 
// Date (MM/YYYY): 02/2026
//
//*****************************************************************************************************
package com.scanlinearcade.games.breakout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Domain Model Class: BreakoutScore
 *
 * <p>Intent: Encapsulates score and lives state for a Breakout session and
 * provides rendering support for a lightweight on-screen HUD.
 *
 * <p>Public API Signatures:
 * <ul>
 *   <li>{@code public BreakoutScore(int startingLives)}</li>
 *   <li>{@code public void reset(int startingLives)}</li>
 *   <li>{@code public void addScore(int amount)}</li>
 *   <li>{@code public void loseLife()}</li>
 *   <li>{@code public int getLives()}</li>
 *   <li>{@code public int getScore()}</li>
 *   <li>{@code public void draw(Graphics2D g2, int panelWidth)}</li>
 * </ul>
 *
 * <p>Package-private API Signatures: None in current implementation.
 */
public class BreakoutScore {
	private int score;
	private int lives;

	/**
	 * Creates a score model with the given number of starting lives.
	 * Signature: {@code public BreakoutScore(int startingLives)}
	 *
	 * @param startingLives initial number of lives
	 */
	public BreakoutScore(int startingLives) {
		this.score = 0;
		this.lives = startingLives;
	}

	/**
	 * Resets score and lives for a new game.
	 * Signature: {@code public void reset(int startingLives)}
	 *
	 * @param startingLives lives to assign after reset
	 */
	public void reset(int startingLives) {
		this.score = 0;
		this.lives = startingLives;
	}

	/**
	 * Adds points to the current score.
	 * Signature: {@code public void addScore(int amount)}
	 *
	 * @param amount number of points to add
	 */
	public void addScore(int amount) {
		score += amount;
	}

	/**
	 * Decreases remaining lives by one, without going below zero.
	 * Signature: {@code public void loseLife()}
	 */
	public void loseLife() {
		lives = Math.max(0, lives - 1);
	}

	/**
	 * Returns remaining lives.
	 * Signature: {@code public int getLives()}
	 *
	 * @return current life count
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Returns current score.
	 * Signature: {@code public int getScore()}
	 *
	 * @return accumulated points
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Draws the score and lives HUD.
	 * Signature: {@code public void draw(Graphics2D g2, int panelWidth)}
	 *
	 * @param g2 graphics context used for rendering
	 * @param panelWidth panel width used to position text on the right side
	 */
	public void draw(Graphics2D g2, int panelWidth) {
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("SansSerif", Font.BOLD, 14));
		g2.drawString("Score: " + score, 20, 20);
		g2.drawString("Lives: " + lives, panelWidth - 90, 20);
	}
}
