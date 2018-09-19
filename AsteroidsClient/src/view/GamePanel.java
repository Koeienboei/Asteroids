package view;

import client.Client;
import static client.ClientState.GET_SERVER;
import static client.ClientState.INITIALIZE;
import static client.ClientState.LOGIN;
import static client.ClientState.PLAYING;
import client.Game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import static java.lang.Math.abs;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import model.Asteroid;
import model.AsteroidsModel;
import model.Bullet;
import model.GameObject;
import model.Spaceship;

/* In this panel the model is drawn. A GamePane; -onject os an observer
 * of the AsteroidsModel. If anything changes in the model, the 
 * AsteroidsModel-object will be repainted automatically.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Observer {

    private Client client;
    private Game game;

    public GamePanel() {
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(800, 800));
    }

    @Override
    public void paintComponent(Graphics g) {
        // Empty the screen and use anti-aliasing
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // Set the background color to black
        this.setBackground(Color.black);

        if (client.getClientState() == PLAYING) {
            int xDisplacement = getXDisplacement();
            int yDisplacement = getYDisplacement();

            // Draw the components of the model
            paintBullets(g2, xDisplacement, yDisplacement);
            paintAsteroids(g2, xDisplacement, yDisplacement);
            paintSpaceships(g2, xDisplacement, yDisplacement);

            if (!client.getSpaceship().isAlive()) {
                g.setColor(Color.white);
                g.drawString("You are dead!", 360, 395);
            }
        } else if (client.getClientState() == INITIALIZE) {
            g.setColor(Color.white);
            g.drawString("Initializing...", 360, 395);
        } else if (client.getClientState() == LOGIN) {
            g.setColor(Color.white);
            g.drawString("Connecting to server...", 360, 395);
        } else if (client.getClientState() == GET_SERVER) {
            g.setColor(Color.white);
            g.drawString("Retreiving server info...", 360, 395);
        }
    }

    private void paintSpaceship(Graphics2D g, Spaceship spaceship, int xDisplacement, int yDisplacement) {
        Integer x = getObjectScreenPositionX(spaceship, xDisplacement);
        Integer y = getObjectScreenPositionY(spaceship, yDisplacement);

        if (x != null && y != null) {
            // Draw the fire that comes from the exhaust when the player accelerates.
            if (spaceship.getSpaceshipController().goForward()) {
                Polygon p = new Polygon();
                // The points of the triangle that represent the fire of the exhaust
                p.addPoint((int) (x - Math.sin(spaceship.getDirection()) * 25),
                        (int) (y + Math.cos(spaceship.getDirection()) * 25));
                p.addPoint((int) (x + Math.sin(spaceship.getDirection() + 0.9
                        * Math.PI) * 15), (int) (y - Math.cos(spaceship
                                .getDirection()
                                + 0.9 * Math.PI) * 15));
                p.addPoint((int) (x + Math.sin(spaceship.getDirection() + 1.1
                        * Math.PI) * 15), (int) (y - Math.cos(spaceship
                                .getDirection()
                                + 1.1 * Math.PI) * 15));
                g.setColor(Color.yellow);
                g.fill(p);
            }

            // Draw the spaceship
            Polygon p = new Polygon();
            // The front of the spaceship
            p.addPoint((int) (x + Math.sin(spaceship.getDirection()) * 20), (int) (y - Math.cos(spaceship.getDirection()) * 20));
            // The two points of the back of the spaceship
            p.addPoint((int) (x + Math.sin(spaceship.getDirection() + 0.8
                    * Math.PI) * 20), (int) (y - Math.cos(spaceship
                            .getDirection()
                            + 0.8 * Math.PI) * 20));
            p.addPoint((int) (x + Math.sin(spaceship.getDirection() + 1.2
                    * Math.PI) * 20), (int) (y - Math.cos(spaceship
                            .getDirection()
                            + 1.2 * Math.PI) * 20));
            /*
                 * Draw the spaceship as a filled black triangle with the lines around
                 * it in the color of the player.
             */
            g.setColor(Color.black);
            g.fill(p);
            if (client.getSpaceship().getId() == spaceship.getId()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.RED);
            }
            g.draw(p);
        }
    }

    private void paintSpaceships(Graphics2D g, int xDisplacement, int yDisplacement) {
        Iterator<Spaceship> it = game.getModel().getSpaceships().iterator();
        while (it.hasNext()) {
            Spaceship spaceship = it.next();
            if (spaceship.isAlive()) {
                paintSpaceship(g, spaceship, xDisplacement, yDisplacement);
            }
        }
    }

    private void paintBullets(Graphics2D g, int xDisplacement, int yDisplacement) {
        g.setColor(Color.yellow);
        Iterator<Bullet> it = game.getModel().getBullets().iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            Integer x = getObjectScreenPositionX(bullet, xDisplacement);
            Integer y = getObjectScreenPositionY(bullet, yDisplacement);
            if (x != null && y != null) {
                g.drawOval(x - 2, y - 2, 5, 5);
            }
        }
    }

    private void paintAsteroids(Graphics2D g, int xDisplacement, int yDisplacement) {
        g.setColor(Color.gray);
        Iterator<Asteroid> it = game.getModel().getAsteroids().iterator();
        while (it.hasNext()) {
            Asteroid asteroid = it.next();
            Integer x = getObjectScreenPositionX(asteroid, xDisplacement);
            Integer y = getObjectScreenPositionY(asteroid, yDisplacement);
            if (x != null && y != null) {
                Ellipse2D.Double e = new Ellipse2D.Double();
                e.setFrame(x - asteroid.getRadius(), y - asteroid.getRadius(),
                        2 * asteroid.getRadius(), 2 * asteroid.getRadius());
                g.fill(e);
            }
        }
    }

    private int getXDisplacement() {
        if (client.getSpaceship().getX() - 400 < 0) {
            return 0 - ((int) client.getSpaceship().getX() - 400);
        } else if (client.getSpaceship().getX() + 400 > game.getModel().getWidth()) {
            return 0 - ((int) client.getSpaceship().getX() + 400 - game.getModel().getWidth());
        } else {
            return 0;
        }
    }

    private int getYDisplacement() {
        if (client.getSpaceship().getY() - 400 < 0) {
            return 0 - ((int) client.getSpaceship().getY() - 400);
        } else if (client.getSpaceship().getY() + 400 > game.getModel().getHeight()) {
            return 0 - ((int) client.getSpaceship().getY() + 400 - game.getModel().getHeight());
        } else {
            return 0;
        }
    }

    private Integer getObjectScreenPositionX(GameObject gameObject, int xDisplacement) {
        int gameObjectNewX = (game.getModel().getWidth() + (int) gameObject.getX() + xDisplacement) % game.getModel().getWidth();
        int screenLeft = (int) client.getSpaceship().getX() - 400 + xDisplacement;
        int screenRight = (int) client.getSpaceship().getX() + 400 + xDisplacement;
        if (gameObjectNewX >= screenLeft && gameObjectNewX <= screenRight) {
            return gameObjectNewX - screenLeft;
        } else {
            return null;
        }
    }

    private Integer getObjectScreenPositionY(GameObject gameObject, int yDisplacement) {
        int gameObjectNewY = (game.getModel().getHeight() + (int) gameObject.getY() + yDisplacement) % game.getModel().getHeight();
        int screenTop = (int) client.getSpaceship().getY() - 400 + yDisplacement;
        int screenBottom = (int) client.getSpaceship().getY() + 400 + yDisplacement;
        if (gameObjectNewY >= screenTop && gameObjectNewY <= screenBottom) {
            return gameObjectNewY - screenTop;
        } else {
            return null;
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        repaint();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGame(Game game) {
        this.game = game;
        this.game.addObserver(this);
    }

}
