package com.ahmed.snack;


import javax.swing.Timer;
import javax.swing.JPanel;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;    //the size of the element
    static final int GAME_UNIT = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE; //number of elements in the game;
    static final int DELAY = 100; //the speed of the snake
    final int[] x = new int[GAME_UNIT]; //contain all the elements' coordinates of x-axis
    final int[] y = new int[GAME_UNIT]; //contain all the elements' coordinates of y-axis
    int bodyParts = 3;
    int foodEaten = 0; // the score
    int foodX; //x coordinate of the food
    int foodY; //y coordinate of the food
    char direction = 'R';
    boolean running = false; //determine the state of the game
    boolean beginning = true; //determine if it is the first time to start the game, or it is after game over state
    Timer timer; //to start the actionPerformed method or to stop it
    Random random; //a class to generate a random number

    public GamePanel() {    // the constructor
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true); //to set the focus to the panel
        this.addKeyListener(new MyKeyAdapter()); //adding the keyListener to get the event from the keyboard
        startGame();

    }

    public void startGame() {
        newFood();
        timer = new Timer(DELAY, this);
        timer.start(); //start the actionPerformed method

    }

    @Override
    public void paintComponent(Graphics g) { //drawing all the elements
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            //this for loop to draw the grid
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            //drawing the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.ORANGE);
                    g.fillRect(x[i], y[0], UNIT_SIZE, UNIT_SIZE);
                } else {
//                    g.setColor(Color.YELLOW);
                    g.setColor(new Color(random.nextInt(255)
                            , random.nextInt(255),
                            random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            scoreTracking(g);
        } else if (beginning) {
            splashScreen(g);
        } else {
            gameOver(g);
        }
    }

    public void newFood() {
        foodX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        //shifting all the body to follow the head of snake
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
        }
    }

    public void checkFood() { //checking the collision between the head and food
        if (foodX == x[0] && foodY == y[0]) {
            bodyParts++;
            foodEaten++;
            newFood();
        }
    }

    public void checkCollisions() {
        // Check if head collies with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // Check if head collies with borders
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        //stopping the action actionPerformed
        if (!running)
            timer.stop();
    }

    public void scoreTracking(Graphics g) { //drawing the score of the game
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics fontMetrics = getFontMetrics(g.getFont()); //this to get the very center of the string
        g.drawString("Score: " + foodEaten, (SCREEN_WIDTH - fontMetrics.stringWidth("Score: " + foodEaten)) / 2,
                g.getFont().getSize());
    }

    public void gameOver(Graphics g) { //drawing the score of the game
        scoreTracking(g);
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics fontMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over!!!", (SCREEN_WIDTH - fontMetrics.stringWidth("Game Over!!!")) / 2,
                SCREEN_HEIGHT / 2);
        g.drawString("Press ENTER to start",
                (SCREEN_WIDTH - fontMetrics.stringWidth("Press ENTER to start")) / 2,
                (SCREEN_HEIGHT / 2) + 50);
    }

    public void splashScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics fontMetrics = getFontMetrics(g.getFont());
        g.drawString("SNACK GAME", ((SCREEN_WIDTH - fontMetrics.stringWidth("Press ENTER to start")) / 2) + 80,
                250);
        g.drawString("Press ENTER to start",
                (SCREEN_WIDTH - fontMetrics.stringWidth("Press ENTER to start")) / 2,
                SCREEN_HEIGHT / 2);
    }

    public void resetData() {
        running = true;
        Arrays.fill(x, 0);
        Arrays.fill(y, 0);
        timer.start();
        direction = 'R';
        foodEaten = 0;
        bodyParts = 3;
        beginning = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }


    public class MyKeyAdapter extends KeyAdapter { //to get the event from the key

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_ENTER -> resetData();
            }
        }
    }
}
