import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {

    class Block {

        int x, y, width, height;
        Image image;

        Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
        }
    }

    int boardWidth = 750, boardHeight = 250;
    int velocityY = 0, gravity = 1, velocityX = -12;
    boolean gameOver = false; 
    int score = 0;

    Image dinosaurImage, dinosaurDeadImage, dinosaurJumpImage;
    Image catcusImage1, catcusImage2, catcusImage3;

    int dinosaurWidth = 88, dinosaurHeight = 94;
    int dinosaurX = 50, dinosaurY = boardHeight - dinosaurHeight;

    int cactusWidth1 = 34, cactusWidth2 = 69, cactusWidth3 = 102, cactusHeight = 70;
    int cactusX = 700, cactusY = boardHeight - cactusHeight;

    Block dinosaur;
    ArrayList<Block> cactusArray;
    Timer gameLoop, placeCactusTimer;

    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        dinosaurImage = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinosaurDeadImage = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dinosaurJumpImage = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        catcusImage1 = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        catcusImage2 = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        catcusImage3 = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImage);
        cactusArray = new ArrayList<Block>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        placeCactusTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });
        placeCactusTimer.start();

    }

    public void placeCactus() {
        if(gameOver) {
            return;
        }
        double placeCactusChance = Math.random();
        if(placeCactusChance > 0.90) {
            Block cactus = new Block(cactusX, cactusY, cactusWidth3, cactusHeight, catcusImage3);
            cactusArray.add(cactus);
        }
        else if(placeCactusChance > 0.70) {
            Block cactus = new Block(cactusX, cactusY, cactusWidth2, cactusHeight, catcusImage2);
            cactusArray.add(cactus);
        }
        else if(placeCactusChance > 0.50) {
            Block cactus = new Block(cactusX, cactusY, cactusWidth1, cactusHeight, catcusImage1);
            cactusArray.add(cactus);
        }

        if(cactusArray.size() > 10) {
            cactusArray.remove(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

        for(int i=0; i<cactusArray.size(); i++) {
            Block cactus = cactusArray.get(i);
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        g.setColor(Color.black);
        g.setFont(new Font("Courier", Font.PLAIN, 32));
        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), 10, 35);
        }
        else {
            g.drawString("Score: " + String.valueOf(score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        dinosaur.y += velocityY;

        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.image = dinosaurImage;
        }

        for(int i=0; i<cactusArray.size(); i++) {
            Block cactus = cactusArray.get(i);
            cactus.x += velocityX;

            if(collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.image = dinosaurDeadImage;
            }
        }

        score++;
    }

    boolean collision(Block a, Block b) {
        return (a.x < b.x + b.width) && (a.x + a.width > b.x) && (a.y < b.y + b.height) && (a.y + a.height > b.y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.image = dinosaurJumpImage;
            }

            if(gameOver) {
                dinosaur.y = dinosaurY;
                dinosaur.image = dinosaurImage;
                velocityY = 0;
                cactusArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
