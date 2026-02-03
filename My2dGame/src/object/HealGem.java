package object;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/** Collectible gem that heals the player. সবসময় সবুজ রঙে দেখা যাবে। */
public class HealGem extends KeyObject {

    public static final int HEAL_AMOUNT = 1;

    public HealGem() {
        name = "HealGem";
        try {
            java.io.InputStream s = getClass().getResourceAsStream("/gem/heal.png");
            if (s != null) image = ImageIO.read(s);
            else image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
        } catch (IOException e) {
            image = null;
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.tile_size, gp.tile_size, null);
        }
    }
}
