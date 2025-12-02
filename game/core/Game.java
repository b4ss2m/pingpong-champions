package game.core;

import game.Assets;
import game.Save;
import game.scenes.MenuScene;
import game.state.StateMachine;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.*;

/** Main panel for the game, controlled by the state machine. */
public class Game extends JPanel implements
        KeyListener,
        MouseListener,
        MouseMotionListener,
        Runnable {

    public StateMachine<Game> stateMachine;
    private boolean running = false;

    public static final int VIRTUAL_WIDTH = 256; // pixel art size
    public static final int VIRTUAL_HEIGHT = 256;

    private BufferedImage frameBuffer; // virtual "canvas"
    private Graphics2D bufferGraphics;

    public double zoom = 1.0;
    private Random shakeRandom = new Random(); // game feel (juice up big moments)
    private double shakeIntensity = 0.0;
    private double shakeZoom = 0.0;

    /** init game and set initial state to menu. */
    public Game(String title) {
        Assets.loadAll(); // cache all game asssets

        frameBuffer = new BufferedImage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = frameBuffer.createGraphics();

        setPreferredSize(new Dimension(1024, 1024));

        JFrame window = new JFrame(title);
        window.setIconImage(Assets.ICON.get());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setVisible(true);

        setFocusable(true);
        requestFocusInWindow(); 
        addKeyListener(this);
        addMouseListener(this); 
        addMouseMotionListener(this);

        Save progress = new Save();
        
        try {
            progress.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stateMachine = new StateMachine<>(this);
        stateMachine.changeState(new MenuScene(progress));
    }

    /** starts game loop on another thread. */
    public void start() {
        running = true;
        Thread gameLoopThread = new Thread(this, "GameLoop"); // never block EDT
        gameLoopThread.start();
    }

    /** stops game loop. */
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        if (stateMachine == null) {
            return;
        }
        long last = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - last) / 1000000000.0; // ns to secs
            last = now;

            if (deltaTime > 0.1) { // make sure delta is never too big
                deltaTime = 0.1;
            }
            // lerp shake and zoom back to 0
            double decaySpeed = 50.0 / (1.0 + shakeIntensity);
            shakeIntensity = shakeIntensity - (shakeIntensity * decaySpeed * deltaTime);
            shakeZoom = shakeZoom - (shakeZoom * decaySpeed * deltaTime);

            stateMachine.update(deltaTime);
            repaint();

            try {
                Thread.sleep(1); // wait a bit we don't melt the CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        if (stateMachine == null) {
            return;
        }

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK); // black bg
        g2d.fillRect(0, 0, getWidth(), getHeight());
        stateMachine.render(bufferGraphics);

        // game is always square aspect ratio
        int squareSize = Math.min(getWidth(), getHeight());

        // apply zoom and zoom-shake
        int offsetX = (int) ((shakeRandom.nextDouble() * 2 - 1) * shakeIntensity);
        int offsetY = (int) ((shakeRandom.nextDouble() * 2 - 1) * shakeIntensity);

        int drawSize = (int) (squareSize * (zoom + shakeZoom));
        int drawX = (getWidth() - drawSize) / 2 + offsetX;
        int drawY = (getHeight() - drawSize) / 2 + offsetY;

        g2d.drawImage(frameBuffer, drawX, drawY, drawSize, drawSize, null);
    }

    // converts screen cordinates to virtual cordinates
    private MouseEvent createVirtualMouseEvent(MouseEvent e) {
        return new MouseEvent(
                e.getComponent(),
                e.getID(),
                e.getWhen(),
                e.getModifiersEx(),
                (e.getX() * VIRTUAL_WIDTH) / getWidth(),
                (e.getY() * VIRTUAL_HEIGHT) / getHeight(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton());
    }

    /** add shake to the camera and accumulating shake. */
    public void shake(double amount) {
        shakeIntensity += amount;
        shakeZoom += amount / 560.0;
    }

    /** add shake to the camera and accumulating shake. */
    public void resetShake() {
        shakeIntensity = 0;
        shakeZoom = 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        stateMachine.handleInput(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        stateMachine.handleInput(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        stateMachine.handleInput(createVirtualMouseEvent(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stateMachine.handleInput(createVirtualMouseEvent(e));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        stateMachine.handleInput(createVirtualMouseEvent(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        stateMachine.handleInput(createVirtualMouseEvent(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        stateMachine.handleInput(createVirtualMouseEvent(e));
    }
}