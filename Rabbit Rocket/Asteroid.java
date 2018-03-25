/**
Asteroid is a interface for a asteroid which will become the obstacles for a ship.
@author Hiroki Koketsu 100258606
@version 2017-07-01 10h30
*/

import java.awt.Graphics2D;
import java.awt.Shape;

public interface Asteroid extends Sprite {
	/**
	Make the asteroid move at the velocity.
	*/
	public void move();

	/**
	Returns true if the asteroid is visible, meaning is within the game frame.
	@return true if the asteroid is visible
	*/
	public boolean isVisible();

	/**
	Gets the asteroid.
	@return shape the asteroid
	*/
	public Shape getShape();

	/**
	Draws the asteroid with light gray color.
	*/
	public void draw(Graphics2D g);

	/**
	Returns true if the asteroid collides with other object.
	@return true if the asteroid collides with other object.
	*/
	public boolean intersects(Sprite other);

	/**
	Returns the number of asteroids it created.
	@return the number of asteroids it created from the beginning.
	*/
	public int getCount();
}
