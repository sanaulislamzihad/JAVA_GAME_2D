package Main;

import entity.Entity;

// Collision ক্লাস: প্লেয়ার বা অন্য entity এর সাথে অবজেক্টের ধাক্কা লাগলো কিনা সেটা চেক করে
public class Collision {
    GamePanel gp; // GamePanel রেফারেন্স

    // কনস্ট্রাক্টর: GamePanel এর রেফারেন্স রিসিভ করে
    public Collision(GamePanel gp) {
        this.gp = gp;
    }

    // checkTile(): entity next move এ solid tiles এ ধাক্কা খাবে কিনা check করে
    public void checkTile(Entity e) {

        int leftWorldX = e.worldX + e.solidAreaX;
        int rightWorldX = e.worldX + e.solidAreaX + e.solid.width;
        int topWorldY = e.worldY + e.solidAreaY;
        int bottomWorldY = e.worldY + e.solidAreaY + e.solid.height;

        int leftCol = leftWorldX / gp.tile_size;
        int rightCol = rightWorldX / gp.tile_size;
        int topRow = topWorldY / gp.tile_size;
        int bottomRow = bottomWorldY / gp.tile_size;

        switch (e.direction) {
            case "up":
                topRow = (topWorldY - e.speed) / gp.tile_size;
                if (isOutOfWorld(leftCol, rightCol, topRow, topRow) || isCollisionTile(leftCol, rightCol, topRow, topRow)) {
                    e.colissionEntity = true;
                }
                break;
            case "down":
                bottomRow = (bottomWorldY + e.speed) / gp.tile_size;
                if (isOutOfWorld(leftCol, rightCol, bottomRow, bottomRow) || isCollisionTile(leftCol, rightCol, bottomRow, bottomRow)) {
                    e.colissionEntity = true;
                }
                break;
            case "left":
                leftCol = (leftWorldX - e.speed) / gp.tile_size;
                if (isOutOfWorld(leftCol, leftCol, topRow, bottomRow) || isCollisionTile(leftCol, leftCol, topRow, bottomRow)) {
                    e.colissionEntity = true;
                }
                break;
            case "right":
                rightCol = (rightWorldX + e.speed) / gp.tile_size;
                if (isOutOfWorld(rightCol, rightCol, topRow, bottomRow) || isCollisionTile(rightCol, rightCol, topRow, bottomRow)) {
                    e.colissionEntity = true;
                }
                break;
        }
    }

    private boolean isOutOfWorld(int leftCol, int rightCol, int topRow, int bottomRow) {
        return leftCol < 0 || rightCol >= gp.maxWorldCol || topRow < 0 || bottomRow >= gp.maxWorldRow;
    }

    private boolean isCollisionTile(int leftCol, int rightCol, int topRow, int bottomRow) {
        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                int tileNum = gp.tileM.mapData[row][col];
                if (gp.tileM.tile[tileNum].collision) {
                    return true;
                }
            }
        }
        return false;
    }

    // checkObject(): প্লেয়ার বা অন্য entity অবজেক্টের সাথে ধাক্কা খাইতেছে কিনা চেক করে
    public int checkObject(Entity e, boolean player) {
        int index = 999; // যদি কোনো অবজেক্টের সাথে ধাক্কা না লাগে

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) { // যদি অবজেক্ট খালি না হয়

                // entity আর অবজেক্টের solid area এর বাস্তব অবস্থান বের করা
                e.solid.x = e.worldX + e.solid.x;
                e.solid.y = e.worldY + e.solid.y;
                gp.obj[i].solid.x = gp.obj[i].worldX + gp.obj[i].solid.x;
                gp.obj[i].solid.y = gp.obj[i].worldY + gp.obj[i].solid.y;

                // Entity কোন দিকে যাচ্ছে তার উপর নির্ভর করে চেক করা
                switch (e.direction) {
                    case "up" :
                        e.solid.y -= e.speed;
                        if (e.solid.intersects(gp.obj[i].solid)) { // যদি ধাক্কা খায়
                            if (gp.obj[i].collision == true) { // যদি অবজেক্ট collision করা থাকে
                                e.colissionEntity = true;
                            }
                            if (player == true) { // যদি প্লেয়ার হয়
                                index = i; // কোন অবজেক্টের সাথে লাগছে সেটা ফেরত দিব
                            }
                        }
                        break;

                    case "down":
                        e.solid.y += e.speed;
                        if (e.solid.intersects(gp.obj[i].solid)) {
                            if (gp.obj[i].collision == true) {
                                e.colissionEntity = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;

                    case "left" :
                        e.solid.x -= e.speed;
                        if (e.solid.intersects(gp.obj[i].solid)) {
                            if (gp.obj[i].collision == true) {
                                e.colissionEntity = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;

                    case "right" :
                        e.solid.x += e.speed;
                        if (e.solid.intersects(gp.obj[i].solid)) {
                            if (gp.obj[i].collision == true) {
                                e.colissionEntity = true;
                            }
                            if (player == true) {
                                index = i;
                            }
                        }
                        break;
                }

                // সব কিছুর অবস্থান আগের জায়গায় রিস্টোর করা
                e.solid.x = e.solidAreaX;
                e.solid.y = e.solidAreaY;
                gp.obj[i].solid.x = gp.obj[i].solidAreaX;
                gp.obj[i].solid.y = gp.obj[i].solidAreaY;
            }
        }
        return index; // অবজেক্টের index রিটার্ন করবে (যদি লাগে), না হলে 999
    }
}
//1. Collision ক্লাসের কাজ কী?
//        প্লেয়ার বা entity কোনো অবজেক্টের সাথে ধাক্কা খাচ্ছে কিনা তা চেক করা।
//
//        2. checkObject() ফাংশন কী করে?
//        Entity'র গতিপথে স্পিড যোগ করে চেক করে intersect হয়েছে কিনা অবজেক্টের সাথে।
//
//        ধাক্কা লাগলে collisionEntity = true করে।
//
//        3. index = i কেন রিটার্ন করছি?
//        কোন অবজেক্টের সাথে ধাক্কা খেয়েছে সেটা জানাতে।
//
//        4. e.solidAreaX / solidAreaY কেন আগের মতো ফেরত আনছি?
//        কারণ solid area'র মুল অবস্থান চেঞ্জ না করে কেবল যাচাই করার জন্য সাময়িক চেঞ্জ করি।