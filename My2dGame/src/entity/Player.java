package entity;

import Main.GamePanel;
import Main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

// Player ক্লাস: Entity থেকে extend করা, Player এর movement আর drawing handle করে
public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyboard;
    public int screenX;
    public int screenY;
    private int screenXDefault;
    private int screenYDefault;

    // Constructor
    public Player(GamePanel gp, KeyHandler keyboard) {
        this.gp = gp;
        this.keyboard = keyboard;

        // Player কে screen-এর মাঝে বসানো
        screenXDefault = (gp.screenWidth / 2) - (gp.tile_size / 2);
        screenYDefault = (gp.screenHeight / 2) - (gp.tile_size / 2);
        screenX = screenXDefault;
        screenY = screenYDefault;

        // Collision এর জন্য solid area ঠিক করা
        solid = new Rectangle(8, 16, 32, 32);
        solidAreaX = 8;
        solidAreaY = 16;

        // Player এর starting value সেট করা
        setDefaultValues();
        // Player এর ছবিগুলা লোড করা
        getPlayerPixels();
    }

    // Player এর image/pixels লোড করা
    public void getPlayerPixels() {
        try {
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/left01.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/left02.png")));
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/up01.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/up02.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/down01.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/down02.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/right01.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/right02.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Default value (starting position) সেট করা
    public void setDefaultValues() {
        worldX = gp.tile_size * 23;
        worldY = gp.tile_size * 21;
        speed = 4;
        direction = "left";
    }

    // black area prevent করার জন্য camera clamp (player আর সব object/tile এই screenX/screenY use করবে)
    public int getScreenX() {
        int x = screenXDefault;
        if (worldX < screenXDefault) {
            x = worldX;
        } else if (worldX > gp.worldWidth - (gp.screenWidth - screenXDefault)) {
            x = gp.screenWidth - (gp.worldWidth - worldX);
        }
        return x;
    }

    public int getScreenY() {
        int y = screenYDefault;
        if (worldY < screenYDefault) {
            y = worldY;
        } else if (worldY > gp.worldHeight - (gp.screenHeight - screenYDefault)) {
            y = gp.screenHeight - (gp.worldHeight - worldY);
        }
        return y;
    }

    public void resetToSpawn() {
        setDefaultValues();
    }

    // প্রতি frame এ player update করা হয়
    public void update() {
        // কোনো key press করা হলে direction সেট করা
        if (keyboard.upPressed || keyboard.rightPressed || keyboard.downPressed || keyboard.leftPressed) {
            if (keyboard.upPressed) {
                direction = "up";
            } else if (keyboard.downPressed) {
                direction = "down";
            } else if (keyboard.leftPressed) {
                direction = "left";
            } else if (keyboard.rightPressed) {
                direction = "right";
            }

            // প্রতিবার movement এর আগে collision flag reset
            colissionEntity = false;

            // Tile + world boundary collision
            gp.colissionChecker.checkTile(this);

            // Object collision / pickup
            int index = gp.colissionChecker.checkObject(this, true);
            if (index != 999) {
                gp.collectKey(index);
            }

            // যদি collision না থাকে, তাহলে move হবে
            if (!colissionEntity) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }

            // Animation changing system (sprite)
            spriteCounter++;
            if (spriteCounter > 5) {
                imageChange = (imageChange == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    // Player কে draw করা হয়
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case "left":
                image = (imageChange == 1) ? left1 : left2;
                break;
            case "down":
                image = (imageChange == 1) ? down1 : down2;
                break;
            case "up":
                image = (imageChange == 1) ? up1 : up2;
                break;
            case "right":
                image = (imageChange == 1) ? right1 : right2;
                break;
        }

        int drawX = getScreenX();
        int drawY = getScreenY();
        g2.drawImage(image, drawX, drawY, gp.tile_size, gp.tile_size, null);
    }
}
//১. Player ক্লাসের কাজ কী?
//        Player এর move, draw, collision detection এবং animation handle করে।
//
//        ২. update() ফাংশনে কী হয়?
//        Input check করে, move করে, collision detect করে, animation আপডেট করে।
//
//        ৩. draw() ফাংশনে কী হয়?
//        Player এর direction অনুযায়ী সঠিক image draw করা হয় screen এ।
//
//        ৪. solid Rectangle কেন ব্যবহার করছো?
//        Collision detection এর জন্য player এর ভিতরে একটা ছোটো invisible box রাখা হয়েছে।