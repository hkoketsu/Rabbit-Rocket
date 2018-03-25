/**
The game frame. Switches panels after the user enters its difficulty level. Also, it is enabled to be restarted after game over.
@author Hiroki Koketsu
@version 2018-04-24 10h00
*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextField;
import javax.swing.*;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.lang.NumberFormatException;
import javax.imageio.ImageIO;

public class SpaceGame extends JFrame implements ActionListener{

  private static SpaceGame game;
  private static GameComponent comp;

  private TextField tf;
  private JLabel[] lbl;
  private Button btn;
  private JPanel defPanel;

  private BufferedImage usachan;
  private BufferedImage title_logo;

  // frame size
  private final static int WIDTH = 1100;
  private final static int HEIGHT = 700;

  public static void main(String[] args){
    game = new SpaceGame();
  }

  public SpaceGame() {
    setTitle("Setting");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setSize(500, 500);
    setLocation(400, 200);
    setResizable(false);

    defPanel = getDefPanel();
    this.add(defPanel);
    setVisible(true);
  }

  private void switchFrame() {
    remove(defPanel);

    setResizable(false);
    comp = new GameComponent();
    add(comp);

    game.setTitle("Rabbit Rocket");
    game.setSize(WIDTH, HEIGHT);
    game.setLocation(200, 100);
    game.getContentPane().setBackground(Color.BLACK);
    game.setVisible(true);
    game.comp.start();
  }

  public static void destroyFrameAndRestart() {
    game.remove(comp);
    game.dispose();
    comp = new GameComponent();
    game = new SpaceGame();
  }

  private JPanel getDefPanel(){
    JPanel p = new JPanel();
    SpringLayout layout = new SpringLayout();
    p.setLayout(layout);

    int w = getWidth();
    int h = getHeight();

    try {
      usachan = ImageIO.read(new File("images/usachan.png"));
      title_logo = ImageIO.read(new File("images/title_logo.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Resize the images
    Image u = usachan.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
    Image tl = title_logo.getScaledInstance(67*4, 45*4, Image.SCALE_SMOOTH);

    lbl = new JLabel[7];
    lbl[0] = new JLabel("Game mode");
    lbl[1] = new JLabel("(1:Easy, 2:Normal, 3:Hard, 4:Impossible)");
    lbl[2] = new JLabel("Difficulty:");
    lbl[3] = new JLabel("Tips: You will lose your points if you shoot an alien!");
    lbl[4] = new JLabel("");
    lbl[5] = new JLabel(new ImageIcon(u));
    lbl[6] = new JLabel(new ImageIcon(tl));

    tf = new TextField(5);

    btn = new Button("Game Start");
    btn.addActionListener(this);

    // Game mode
    layout.putConstraint(SpringLayout.NORTH, lbl[0], h*2/5 + 20, SpringLayout.NORTH, p);
    layout.putConstraint(SpringLayout.WEST, lbl[0], w/3 + 50, SpringLayout.WEST, p);

    // (1:Easy, 2:Normal, 3:Hard, 4:Impossible)
    layout.putConstraint(SpringLayout.NORTH, lbl[1], 10, SpringLayout.SOUTH, lbl[0]);
    layout.putConstraint(SpringLayout.WEST, lbl[1], w/4, SpringLayout.WEST, p);

    // Difficulty:
    layout.putConstraint(SpringLayout.NORTH, lbl[2], 40, SpringLayout.SOUTH, lbl[1]);
    layout.putConstraint(SpringLayout.WEST, lbl[2], w/3 + 10, SpringLayout.WEST, p);

    // Text input
    layout.putConstraint(SpringLayout.NORTH, tf, 0, SpringLayout.NORTH, lbl[2]);
    layout.putConstraint(SpringLayout.WEST, tf, 20, SpringLayout.EAST, lbl[2]);

    // Game Start button
    layout.putConstraint(SpringLayout.NORTH, btn, 30, SpringLayout.SOUTH, lbl[2]);
    layout.putConstraint(SpringLayout.WEST, btn, 15, SpringLayout.WEST, lbl[2]);

    // Hint for gameplay: Press space to shoot a gun!
    layout.putConstraint(SpringLayout.SOUTH, lbl[3], -h/10, SpringLayout.SOUTH, p);
    layout.putConstraint(SpringLayout.WEST, lbl[3], w/5 + 10, SpringLayout.WEST, p);

    // Warning message
    layout.putConstraint(SpringLayout.SOUTH, lbl[4], -10, SpringLayout.NORTH, lbl[2]);
    layout.putConstraint(SpringLayout.WEST, lbl[4], 0, SpringLayout.WEST, lbl[1]);

    // usachan drawing
    layout.putConstraint(SpringLayout.NORTH, lbl[5], 50, SpringLayout.NORTH, lbl[0]);
    layout.putConstraint(SpringLayout.EAST, lbl[5], -10, SpringLayout.EAST, p);

    // title logo
    layout.putConstraint(SpringLayout.NORTH, lbl[6], 10, SpringLayout.NORTH, p);
    layout.putConstraint(SpringLayout.WEST, lbl[6], w/5+10, SpringLayout.WEST, p);

    for (int i = 0; i < lbl.length; i++) {
      p.add(lbl[i]);
    }
    p.add(tf);
    p.add(btn);

    return p;
  }

  public static int getH() {
    return HEIGHT;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int d = 0;;
    try {
      d = Integer.parseInt(tf.getText());
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    if (d >= 1 && d <= 4) {
      GameComponent.setDifficulty(d);
      switchFrame();
    } else {
      lbl[4].setText("Enter 1, 2, 3 or 4 and start the game");
    }
  }
}
