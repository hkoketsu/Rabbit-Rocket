/**
Cannon is the interface for cannons which ship will launch by the space key.
@author Hiroki Koketsu 100258606
@version 2017-07-01 11h00
*/
import java.awt.Shape;
import java.awt.Graphics2D;

public interface Cannon extends Sprite {
  /**
  Draws the cannon.
  */
  public void draw(Graphics2D g);

  /**
  Make the cannon move.
  */   
  public void move();

  /**
  Returns true if the cannon is visible, meaning is within the game frame.
  @return true if the cannon is visible
  */
  public boolean isVisible();

  /**
  Gets the cannon.
  @return shape the cannon
  */
  public Shape getShape();

  /**
  Returns true if the cannon collides with other object.
  @return true if the cannon collides with other object.
  */
  public boolean intersects(Sprite other);
}
