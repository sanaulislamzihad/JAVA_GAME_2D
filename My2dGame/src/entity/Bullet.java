package entity;

import Main.GamePanel;
import java.awt.*;
import java.awt.geom.AffineTransform;

/** Bullet fired by shooter enemy. যেদিকে যাচ্ছে সেদিকে ঘুরে (rotate) দেখায়। */
public class Bullet {
    public int worldX, worldY;
    public double vx, vy;
    public boolean active = true;
    private static final int SIZE = 12;
    private static final int LEN = 16; // দৈর্ঘ্য (দিক বরাবর)
    private static final int HITBOX_OFF = 4;

    public Bullet(int worldX, int worldY, double vx, double vy) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.vx = vx;
        this.vy = vy;
    }

    public void update(GamePanel gp) {
        if (!active) return;
        worldX += (int) vx;
        worldY += (int) vy;
        // Deactivate if off world
        if (worldX < -SIZE || worldY < -SIZE
                || worldX > gp.worldWidth + SIZE || worldY > gp.worldHeight + SIZE) {
            active = false;
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(worldX + HITBOX_OFF, worldY + HITBOX_OFF, SIZE - 2 * HITBOX_OFF, SIZE - 2 * HITBOX_OFF);
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        if (!active) return;
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
        double angle = Math.atan2(vy, vx);
        AffineTransform old = g2.getTransform();
        g2.translate(screenX + SIZE / 2.0, screenY + SIZE / 2.0);
        g2.rotate(angle);
        g2.setColor(Color.ORANGE);
        g2.fillRoundRect(-LEN / 2, -SIZE / 2, LEN, SIZE, 4, 4);
        g2.setTransform(old);
    }
}
