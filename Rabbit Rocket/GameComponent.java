/**
GameComponent has the needed components for the game to work, such as Ship and Asteroid.
@author Hiroki Koketsu 100258606
@version 2018-04-24 10h00
*/
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.SwingWorker;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameComponent extends JComponent {

		private final static Font GAME_OVER_FONT = new Font(Font.SERIF, Font.BOLD, 100);
		private final static Font RESTART_FONT = new Font(Font.SERIF, Font.PLAIN, 40);
		private final static Font TIME_LAP_FONT = new Font(Font.SERIF, Font.PLAIN, 20);
		private final static Font EXPLANATION_FONT = new Font(Font.SERIF, Font.PLAIN, 40);

		private int w;
		private int h;

		private final ShipImpl ship;
		private final Timer timers[];
		private static ShipKeyListener keyListener;
		private Set<Asteroid> asteroidsSet;
		private List<Cannon> shots;
		private boolean isGameOver;
		private long startTime;
		private long elapsedTime;
		private int numberOfShotsHitAsts;
		private Audio audio;
		private int life = 3;
		private static int difficulty; // how often asteroid is created, larger -> easier
		private long whenDamaged = 0;
		private long lastDamaged = 0;
		private boolean isDamaged;


		// pre-load the font to avoid lag on Macs, but still has lag before the game starts
		// AKN: Jeremy H
		private final static SwingWorker<Void, Void> assetLoader = new SwingWorker<Void, Void>() {
			public Void doInBackground() {
				Graphics2D g = new BufferedImage(10,10,BufferedImage.TYPE_BYTE_GRAY).createGraphics();
				g.setFont(TIME_LAP_FONT);
				g.setFont(GAME_OVER_FONT);
				return null;
			}
		};
		static {
			assetLoader.execute();
		}

		/**
		Create a constructor of GameComponent.
		*/
		public GameComponent() {
			ship = new ShipImpl(10, SpaceGame.getH()/3);
			shots = new ArrayList<Cannon>();
			timers = new Timer[] {
				new Timer(1000/60, (a) -> {this.update();}),
				new Timer(difficulty, (a) -> {this.makeAsteroid();})};
			asteroidsSet = new HashSet<Asteroid>();
			audio = new Audio();
			keyListener = new ShipKeyListener();
			addKeyListener(keyListener);
		}

		/**
		Paints components, such as ship, asteroid, cannon, score.
		@param g Graphics interface to be installed to paint compoennts
		*/
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			paintComponent(g2);
		}
		private void paintComponent(Graphics2D g) {
			ship.draw(g);
			shots.stream().forEach(s -> s.draw(g));
			asteroidsSet.stream().forEach((a) -> a.draw(g));
			if (elapsedTime < 40) {
				g.setFont(EXPLANATION_FONT);
				g.setColor(Color.WHITE);
				g.drawString("Press space to shoot a gun!", w/4, h/10);
			}
			displayScore(g);
			displayLife(g);
			if (isGameOver) {
				displayGameOver(g);
			}
		}

		/**
		Set the frequency of creating asteroids. Less the number, More asteroids are created in a period.
		*/
		public static void setDifficulty(int diff) {
			int d = 0;
			switch (diff) {
				case 1: d = 400; break;
				case 2: d = 200; break;
				case 3: d = 70; break;
				case 4: d = 10; break;
				default:
			}
			difficulty = d;
		}

		/**
		Starts the game, and set the settings of bounds of asteroids and ship.
		*/
		public void start() {
			w = getWidth();
			h = getHeight();
			AsteroidFactory.getInstance().setStartBounds(w, 0, h);
			ship.setMovementBounds(new Rectangle(w, h));
			try {
				assetLoader.get();
			} catch(Exception e) {
				// swallow... will load fonts later
			}
			for (Timer timer : timers) {
				timer.start();
			}
			startTime = System.nanoTime();
			makeAsteroid();
			audio.playBackgroundMusic();
		}

		/**
		Updates the game components at the rate of timer.
		*/
		private void update() {
			requestFocusInWindow();
			elapsedTime = (System.nanoTime() - startTime) / (100*1000*1000);
			ship.move();
			moveShots();
			moveAsteroids();
			isGameOver = checkGameOver();
			if (isGameOver) {
				gameOver();
			}
			repaint();
		}

		/**
		Functions when ship collides asteroid. Stops the timer so the components stop moving.
		*/
		private void gameOver() {
			for (Timer timer : timers) {
				timer.stop();
			}
			audio.stopBackgroundMusic();
			audio.playGameOverSound();
		}

		private static void restart() {
			SpaceGame.destroyFrameAndRestart();
		}

		/**
		Displays the message of "Game Over" when the ship collides an asteroid.
		@param g Graphic interface to be installed to draw the message
		*/
		private void displayGameOver(Graphics2D g) {
			g.setColor(Color.RED);
			g.setFont(GAME_OVER_FONT);
			g.drawString("Game Over", w/2 - 250, h/2);
			g.setFont(RESTART_FONT);
			g.drawString("Press 'R' to Continue", w/3, h*2/3);
		}

		/**
		Display the score, which is the sum of the playtime and number of shooting asteroids.
		@param g Graphic interface to be installed to draw the message
		*/
		private void displayScore(Graphics2D g) {
			g.setColor(Color.WHITE);
			g.setFont(TIME_LAP_FONT);
			g.drawString("Shot points: " + numberOfShotsHitAsts, w/20, h*18/20);
			g.drawString("Time: " + elapsedTime, w/20, h*19/20);
		}

		/**
		Display the life. At first, life is 3. Losts 1 life with collision.
		@param g Graphic interface to be installed to draw the message
		*/
		private void displayLife(Graphics2D g) {
			g.setColor(Color.WHITE);
			g.setFont(TIME_LAP_FONT);
			g.drawString("Life: ", w/20, h*17/20);
			BufferedImage heart = null;
			try {
				heart = ImageIO.read(new File("images/heart.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < life; i++) {
				g.drawImage(heart, w/20+50+i*30, h*17/20-15, w/20+50+i*30+20, h*17/20+5, 0, 0, 480, 480, null);
			}
		}

		/**
		Creates an asteroid.
		*/
		public void makeAsteroid() {
			Asteroid ast = AsteroidFactory.getInstance().makeAsteroid();
			asteroidsSet.add(ast);
		}

		/**
		Makes an asteroid move until when it becomes invisible form the game frame.
		*/
		public void moveAsteroids() {
			asteroidsSet.stream().filter(Asteroid::isVisible).forEach(Asteroid::move);	    asteroidsSet.removeIf(a -> !a.isVisible());
		}

		/**
		Fire the cannon.
		*/
		public void shoot() {
			Cannon c = ship.launchCannon();
			audio.playShootSound();
			shots.add(c);
		}

		/**
		Make a shot move. If the shot gets out of the game frame or hits an asteroid, it disappears.
		*/
		public void moveShots() {
			shots.stream().filter(Cannon::isVisible).forEach(Cannon::move);
			shots.removeIf(a -> !a.isVisible());
			checkShotHitAsteroid();
		}

		/**
		Check whether the shot hits any asteroids.
		If it does, remove both the shot and the asteroid.
		*/
		private void checkShotHitAsteroid() {
			if(!shots.isEmpty()) {
				for (Iterator<Cannon> it_shots = shots.iterator();it_shots.hasNext();) {
					Cannon shot = it_shots.next();
					for (Iterator<Asteroid> it_ast = asteroidsSet.iterator(); it_ast.hasNext();) {
						Asteroid asteroid = it_ast.next();
						if (shot.intersects(asteroid)) {
							if (asteroid.getCount() % 10 == 9) {
								numberOfShotsHitAsts -= 5;
								audio.playAlienSound();
							} else {
								numberOfShotsHitAsts++;
								audio.playExpSound();
							}
							it_shots.remove();
							it_ast.remove();
							break;
						}
					}
				}
			}
		}

		/**
		Check the collisions of ship and asteroids.
		@return true if the ship and an asteroid collides.
		*/
		public boolean checkGameOver() {
			for (Asteroid a : asteroidsSet) {
				if (a.intersects(ship)) {
					whenDamaged = elapsedTime;
					if (whenDamaged - lastDamaged > 10) {
						life--;
						audio.playDamageSound();
						lastDamaged = elapsedTime;
					}
					if (life == 0) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		Defines the functions depending on which key is pressed or released.
		Enables the ship move horizontally, vertically, and diagonally.
		If the space key is pressed the ship launches cannon.
		*/
		class ShipKeyListener extends KeyAdapter {
			private boolean up = false;
			private boolean down = false;
			private boolean right = false;
			private boolean left = false;

			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_KP_UP:
					case KeyEvent.VK_UP:
					case KeyEvent.VK_W: up = true;
					break;

					case KeyEvent.VK_KP_DOWN:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_S: down = true;
					break;

					case KeyEvent.VK_KP_RIGHT:
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_D: right = true;
					break;

					case KeyEvent.VK_KP_LEFT:
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_A: left = true;
					break;

					case KeyEvent.VK_SPACE: shoot();
					break;

					case KeyEvent.VK_R:
						if (isGameOver) {
							restart();
						}
					break;
					default:
				}

				if (up && left)
				ship.setDirection(Ship.Direction.UP_LEFT);
				else if (up && right)
				ship.setDirection(Ship.Direction.UP_RIGHT);
				else if (down & left)
				ship.setDirection(Ship.Direction.DOWN_LEFT);
				else if (down && right)
				ship.setDirection(Ship.Direction.DOWN_RIGHT);
				else if (up)
				ship.setDirection(Ship.Direction.UP);
				else if (down)
				ship.setDirection(Ship.Direction.DOWN);
				else if (right)
				ship.setDirection(Ship.Direction.RIGHT);
				else if (left)
				ship.setDirection(Ship.Direction.LEFT);
				else
				ship.setDirection(Ship.Direction.NONE);
			}

			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_KP_UP:
					case KeyEvent.VK_UP:
					case KeyEvent.VK_W:
					up = false;
					break;
					case KeyEvent.VK_KP_DOWN:
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_S:
					down = false;
					break;
					case KeyEvent.VK_KP_RIGHT:
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_D:
					right = false;
					break;
					case KeyEvent.VK_KP_LEFT:
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_A:
					left = false;
					break;
					default:
				}

				if (up)
				ship.setDirection(Ship.Direction.UP);
				else if (down)
				ship.setDirection(Ship.Direction.DOWN);
				else if (right)
				ship.setDirection(Ship.Direction.RIGHT);
				else if (left)
				ship.setDirection(Ship.Direction.LEFT);
				else
				ship.setDirection(Ship.Direction.NONE);
			}
		}
	}
