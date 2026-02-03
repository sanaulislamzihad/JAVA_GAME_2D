package object;

import Main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

// KeyObject class যা যে কোনো object এর জন্য base class হিসেবে কাজ করে
public class KeyObject {

    // KeyObject এর properties
    public BufferedImage image;  // Object এর image
    public String name;  // Object এর নাম
    public int hasKey;  // কতগুলো key আছে ট্র্যাক করতে
    public boolean collision = false;  // Collision এর জন্য flag
    public int worldX, worldY;  // World এ object's position
    public Rectangle solid = new Rectangle(0, 0, 48, 48);  // Collision area (solid box)
    public int solidAreaX = 0;  // Solid box এর x offset
    public int solidAreaY = 0;  // Solid box এর y offset

    // Object কে draw করার method
    public void draw(Graphics2D g2, GamePanel gp) {
        // screen এ object এর position calculate করা হচ্ছে, player এর relative position অনুযায়ী
        int screenX = worldX - gp.player.worldX + gp.player.getScreenX();  // Horizontal position calculation
        int screenY = worldY - gp.player.worldY + gp.player.getScreenY();  // Vertical position calculation
        g2.drawImage(image, screenX, screenY, gp.tile_size, gp.tile_size, null);  // image draw করা হচ্ছে
    }
}
//➔ KeyObject ক্লাসের কাজ কী?
//        👉 এটা মূলত সকল collectible objects এর জন্য base class হিসেবে কাজ করে। image, name, collision box ইত্যাদি properties থাকে।
//
//        ➔ solid Rectangle কেন?
//        👉 Collision detection এর জন্য object এর একটি invisible box (solid area) দেওয়া হয়, যাতে গেমে player object's সাথে ধাক্কা খাচ্ছে কিনা তা check করা যায়।
//
//        ➔ draw() method কী করে?
//        👉 এই method টি object এর image screen এ draw করে, player এর position অনুযায়ী সঠিক স্থানে।
//
