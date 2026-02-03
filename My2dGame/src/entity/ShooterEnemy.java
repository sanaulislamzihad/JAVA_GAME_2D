package entity;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/** Enemy – মানুষের মতো স্প্রাইট (প্লেয়ার স্প্রাইট + লাল টিন্ট), প্লেয়ার থেকে আলাদা। */
public class ShooterEnemy {
    public int worldX, worldY;
    private final GamePanel gp;
    private int fireCooldown = 0;
    private static final int FIRE_INTERVAL_BASE = 95;
    private static final int FIRE_INTERVAL_MIN = 40;
    private static final double BULLET_SPEED = 4.0;
    private static final int SIZE = 48;
    private BufferedImage enemyImage;
    private boolean useRedTint; // কাস্টম enemy.png থাকলে false

    public ShooterEnemy(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.fireCooldown = FIRE_INTERVAL_BASE / 2;
        try {
            InputStream stream = getClass().getResourceAsStream("/enemy/enemy.png");
            if (stream != null) {
                enemyImage = ImageIO.read(stream);
                useRedTint = false; // আপনার ভয়ংকর স্প্রাইট যেমন আছে তেমন
            } else {
                enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/down01.png")));
                useRedTint = true;
            }
        } catch (IOException e) {
            try {
                enemyImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/down01.png")));
                useRedTint = true;
            } catch (IOException e2) {
                enemyImage = null;
            }
        }
    }

    public void update(List<Bullet> bullets) {
        fireCooldown--;
        if (fireCooldown <= 0) {
            int lvl = gp.getLevel();
            int interval = Math.max(FIRE_INTERVAL_MIN, FIRE_INTERVAL_BASE - lvl * 10);
            fireCooldown = interval;
            // Aim at player center
            int px = gp.player.worldX + gp.player.solid.width / 2 + gp.player.solidAreaX;
            int py = gp.player.worldY + gp.player.solid.height / 2 + gp.player.solidAreaY;
            int ex = worldX + SIZE / 2;
            int ey = worldY + SIZE / 2;
            double dx = px - ex;
            double dy = py - ey;
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                dx /= len;
                dy /= len;
                Bullet b = new Bullet(ex - 6, ey - 6, dx * BULLET_SPEED, dy * BULLET_SPEED);
                bullets.add(b);
            }
        }
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
        // যেদিকে গুলি ছোড়ে (প্লেয়ারের দিকে) সেদিকেই এনিমি ফটো ঘুরিয়ে আঁকা
        int px = gp.player.worldX + gp.player.solid.width / 2 + gp.player.solidAreaX;
        int py = gp.player.worldY + gp.player.solid.height / 2 + gp.player.solidAreaY;
        int ex = worldX + SIZE / 2;
        int ey = worldY + SIZE / 2;
        double dx = px - ex;
        double dy = py - ey;
        double angle = Math.atan2(dy, dx);

        if (enemyImage != null) {
            AffineTransform old = g2.getTransform();
            g2.translate(screenX + SIZE / 2.0, screenY + SIZE / 2.0);
            g2.rotate(angle);
            g2.drawImage(enemyImage, -SIZE / 2, -SIZE / 2, SIZE, SIZE, null);
            if (useRedTint) {
                g2.setColor(new Color(200, 0, 0, 110));
                g2.fillRect(-SIZE / 2, -SIZE / 2, SIZE, SIZE);
            }
            g2.setTransform(old);
        } else {
            g2.setColor(new Color(180, 50, 50));
            g2.fillRect(screenX, screenY, SIZE, SIZE);
            g2.setColor(Color.RED);
            g2.drawRect(screenX, screenY, SIZE, SIZE);
        }
    }
}
