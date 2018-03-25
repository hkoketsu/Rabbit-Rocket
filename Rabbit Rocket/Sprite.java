/**
	Sprite is a general computer graphic that may be moved on-screen and otherwise manipulated as a single entity.
	@author Hiroki Koketsu 100258606
	@version 2017-07-01 11h00
*/
import java.awt.*;

public interface Sprite {
	public void draw(Graphics2D g);
	public void move();
	public Shape getShape(); // used by intersects
	public boolean intersects(Sprite other);
}
