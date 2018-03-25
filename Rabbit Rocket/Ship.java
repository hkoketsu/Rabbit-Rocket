/**
	Ship is the interface for a ship.
	@author Hiroki Koketsu 100258606
	@version 2017-07-01 11h00
*/
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public interface Ship extends Sprite {
	
	/**
		Directions of which the ship moves.
	*/
	public enum Direction {
		NONE(0, 0), UP(0, -1), DOWN(0, 1), RIGHT(1, 0), LEFT(-1, 0),
		UP_RIGHT(1, -1), UP_LEFT(-1, -1), DOWN_RIGHT(1, 1), DOWN_LEFT(-1, 1);
		public final int dx;
		public final int dy;
		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	};

	/**
		Sets the direction of the ship.
		@param d the direction of the ship		cf.) Ship.java : enum Direction
	*/
	public void setDirection(Direction d);
	
	/**
		Set the movement bounds of the ship.
		@param movementBounds the rectangle bounds which defines the area where ship can move
	*/
	public void setMovementBounds(Rectangle2D bounds);
	
	/**
		Make the ship move.
	*/
	public void move();
	
	/**
		Draw the ship with different color for the border and inside.
		@param g Graphics interface to be installed to paint the ship.
	*/
	public void draw(Graphics2D g);
	
	/**
		Gets the shape of the ship.
		@return shape the ship
	*/
	public Shape getShape();
	
	/**
		Returns true if the ship collides with other object.
		@return true if the ship collides with other object.
	*/
	public boolean intersects(Sprite other);
	
	/**
		Launches the cannon.
		@return the cannon which starts from the front of ship
	*/
	public Cannon launchCannon();
}
