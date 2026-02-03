package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

// Entity ক্লাস: Player বা অন্য সকল movable object's common properties রাখে
public class Entity {
    // World map এ position
    public int worldX, worldY;

    // Movement speed
    public int speed;

    // Movement animation এর জন্য image গুলো
    public BufferedImage left1, left2, up1, up2, down1, down2, right1, right2;

    // কোন direction এ চলছে
    public String direction;

    // Image switch করার জন্য counter
    public int imageChange = 1;
    public int spriteCounter = 0;

    // Collision detection এর জন্য solid area position
    public int solidAreaX, solidAreaY;

    // Solid area box
    public Rectangle solid;

    // Collision হয়েছে কিনা check করার জন্য
    public boolean colissionEntity = false;

    // Collect করা key এর সংখ্যা (static, মানে সবার জন্য common)
    public static int hasKey;
}
//➔ Entity ক্লাসের কাজ কী?
//        👉 Movable object (player বা enemy) এর position, speed, collision box, image animation track করে।
//
//        ➔ solid Rectangle কেন?
//        👉 Collision detect করার জন্য একটা invisible boundary।
//
//        ➔ hasKey static কেন?
//        👉 Player কতগুলো key collect করছে সেটা globally track করতে।
//
//        ➔ imageChange আর spriteCounter এর কাজ কী?
//        👉 Walking animation এর image গুলা switch করার জন্য।