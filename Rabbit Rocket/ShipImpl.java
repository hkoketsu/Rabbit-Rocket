/**
ShipImpl has the implementation of Ship. It also includes the implementation of Cannon.
@author Hiroki Koketsu 100258606
@version 2017-07-01 11h30
*/

import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ShipImpl implements Ship {

	private final static int HEIGHT = 80;
	private final static int WIDTH = 160;

	private BufferedImage rocket;
	private int x;
	private int y;
	private static Polygon shape;
	private Direction d;
	private Rectangle2D movementBounds;

	/**
	Create a constructor of ShipImpl.
	@param x the X coordinate of the highest point of the ship
	@param y the Y coordinate of the highest point of the ship
	*/
	public ShipImpl(int x, int y) {
		this.x = x;
		this.y = y;
		int[] xpoints = {x, x + WIDTH/3, x + WIDTH/3, x + WIDTH*2/3 + 5, x + WIDTH*2/3 + 5, x + WIDTH, x + WIDTH - 40, x};
		int[] ypoints = {y + HEIGHT/3 + 10, y + HEIGHT/3, y, y, y + HEIGHT/3, y + HEIGHT/3 + 20, y + HEIGHT * 2/3 + 10, y + HEIGHT * 2/3 + 10};
		shape = new Polygon(xpoints, ypoints, xpoints.length);
		d = Direction.NONE;

		try {
			rocket = ImageIO.read(new File("images/rocketUsachan.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDirection(Direction d) {
		this.d = d;
	}

	public void setMovementBounds(Rectangle2D movementBounds) {
		this.movementBounds = movementBounds;
	}

	public void move() {
		if (movementBounds.contains(new Rectangle2D.Double(x + d.dx * 5, y + d.dy * 5, WIDTH, HEIGHT))){
			shape.translate(d.dx * 5, d.dy * 5);
			x += d.dx * 5;
			y += d.dy * 5;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(rocket, x, y, x + WIDTH, y + HEIGHT, 0, 0, 1578,650, null);
	}

	public Shape getShape() {
		return shape;
	}

	public boolean intersects(Sprite other) {
		if (shape.intersects(other.getShape().getBounds2D())) {
			Area a1 = new Area(shape);
			Area a2 = new Area(other.getShape());
			a1.intersect(a2);
			if (!a1.isEmpty())
			return true;
		}
		return false;
	}

	public Cannon launchCannon() {
		int x_cannon = x + WIDTH + 5;
		int y_cannon = y + HEIGHT/2 - 10;
		return new CannonImpl(x_cannon, y_cannon);
	}

	/**
	CannonImpl is the implementation of a cannon.
	*/
	private class CannonImpl implements Cannon {
		private int x;
		private int y;
		private Rectangle cannon;
		private BufferedImage gun;

		public CannonImpl(int x, int y) {
			this.x = x;
			this.y = y;
			cannon = new Rectangle(x-10, y+5, 120, 25);
			try {
				gun = ImageIO.read(new File("images/gunshot.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void draw(Graphics2D g) {
			g.drawImage(gun,x-10,y-10,x+110,y+35,0,0,800,300,null);
		}

		public void move() {
			cannon.translate(40, 0);
			x += 40;
		}

		public boolean isVisible() {
			if (movementBounds.contains(cannon.getBounds2D())) {
				return true;
			}
			return false;
		}

		public Shape getShape() {
			return cannon;
		}

		public boolean intersects(Sprite other) {
			return cannon.intersects(other.getShape().getBounds2D()) ? true : false;
		}
	}
}
