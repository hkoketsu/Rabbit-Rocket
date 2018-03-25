/**
AsteroidFactory creates asteroids and make them move from the set start bounds.
AsteroidsImpl has the detail settings of the size, starting point, and velocity of each asteroids and its implementation.
@author Hiroki Koketsu 100258606
@version 2018-04-24 10h00
*/

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AsteroidFactory {

	private final static AsteroidFactory instance = new AsteroidFactory();

	private int count = 0;

	private static Rectangle startBounds;

	/**
	Creates a constructor of AsteroidFactory. Does not accept any access from other classes. Instead, the constructor is initialized within the class, and accessed through getInstance() method.
	*/
	private AsteroidFactory() {
	}

	/**
	Returns the constructor of AsteroidFactory.
	@return instance the instance of the constructor AsteroidFactory
	*/
	public static AsteroidFactory getInstance() {
		return instance;
	}

	/**
	Sets the starting bounds of asteroids. The bounds is created in a rectangle, but it will not have width, so it is actually a line.
	@param x the X coordinate of the stariting bounds
	@param minY the minimum Y coordinate of the starting bounds
	@param maxY the maximum Y coordinate of the starting bounds
	*/
	public void setStartBounds(int x, int minY, int maxY) {
		startBounds = new Rectangle(x, minY, 0, maxY - minY);
	}

	/**
	Creates an asteroid.
	@return an asteroid which has the starting position with x, y coordinates and its speed.
	*/
	public Asteroid makeAsteroid() {
		int x = (int) startBounds.getX();
		int y = random((int)startBounds.getY(), (int)startBounds.getHeight());

		int x_speed = random(1,10);
		int y_speed = random(-1,1);
		int size = 4;
		int scale = random(4,10);
		int xpoints[] = {x, x + size * scale, x + 2 * size * scale, x + size * scale};
		int ypoints[] = {y, y + size * scale, y, y - size * scale};
		count++;

		return new AsteroidImpl(xpoints, ypoints, x_speed, y_speed, size, scale, count);
	}

	/**
	Generate a random integer between the given two integer. Static method.
	@param min minimum integer of the random number
	@param max maximum integer of the random number
	*/
	private static int random(int min, int max) {
		Random rand = new Random();
		return min + (int) rand.nextInt(max-min);
	}

	private static class AsteroidImpl implements Asteroid {
		private final static Color COLOR = Color.LIGHT_GRAY;
		private int x;
		private int y;
		private final int x_speed;
		private final int y_speed;
		private final int size;
		private final int scale;
		private final Polygon shape;
		private int count;
		private BufferedImage star1;
		private BufferedImage star2;
		private BufferedImage alien;

		/**
		Creates a constructor of AsteroidImpl.
		@param x the X coordinate of the starting point of asteroid
		@param y the Y coordinate of the starting point of asteroid
		@param xs the speed of asteroid toward x-axis
		@param ys the speed of asteroid toward y-axis
		@param sz the size of asteroid
		@param sc the size level of asteroid (1 - 5)
		@param c the factor to decide which enermy to produce
		*/
		private AsteroidImpl(int[] x, int[] y, int xs, int ys, int sz, int sc, int c) {
			shape = new Polygon(x, y, x.length);
			this.x = x[0];
			this.y = y[0];
			x_speed = xs;
			y_speed = ys;
			size = sz;
			scale = sc;
			count = c;

			try {
				star1 = ImageIO.read(new File("images/enemy.png"));
				star2 = ImageIO.read(new File("images/enemy2.png"));
				alien = ImageIO.read(new File("images/alien.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void move() {
			shape.translate(-1 * x_speed, y_speed);
			x -= x_speed;
			y += y_speed;
		}

		public boolean isVisible() {
			if (shape.xpoints[0] + 10 < 0) {
				return false;
			}
			return true;
		}

		public void draw(Graphics2D g) {
			double adj = 1.2;
			int x0 = (int) adj * x;
			int y0 = (int) adj * (y - size * scale);
			int x1 = (int) adj * (x + 2 * size * scale);
			int y1 = (int) adj * (y + size * scale);
			if (count % 10 < 5) { // 5/10 - star1
				g.drawImage(star1, x0, y0, x1, y1,0,0,270,270,null);
			} else if (count % 10 < 9) { // 4/10 - star2
				g.drawImage(star2, x0, y0, x1, y1,0,0,370,370,null);
			} else { // 1/10 - alien
				g.drawImage(alien, x0, y0, x1, y1,0,0,450,390,null);
			}
		}

		public Shape getShape() {
			return shape;
		}

		public int getCount() {
			return count;
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
	}
}
