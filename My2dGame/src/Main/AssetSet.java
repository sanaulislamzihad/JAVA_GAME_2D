package Main;

import object.key;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// AssetSet ক্লাস: গেমের অবজেক্ট (যেমন চাবি) সেট করার জন্য ব্যবহৃত হয়
public class AssetSet {
    GamePanel gp; // GamePanel এর রেফারেন্স

    // কনস্ট্রাক্টর: GamePanel রেফারেন্স নিয়ে অবজেক্ট সেট করে
    AssetSet(GamePanel gp) {
        this.gp = gp;
    }

    public void clearObjects() {
        for (int i = 0; i < gp.obj.length; i++) {
            gp.obj[i] = null;
        }
    }

    // নির্দিষ্ট level + count অনুযায়ী keys place করবে (walkable tiles এ)
    public void placeKeysForLevel(int level, int keyCount) {
        clearObjects();

        Random r = new Random(2026L + level * 777L);
        Set<Integer> used = new HashSet<>();

        int spawnCol = 23;
        int spawnRow = 21;
        int placed = 0;
        int safety = 0;

        while (placed < keyCount && safety < 20000) {
            safety++;
            int col = 2 + r.nextInt(gp.maxWorldCol - 4);
            int row = 2 + r.nextInt(gp.maxWorldRow - 4);

            // spawn এর কাছে key রাখবো না
            int dSpawn = Math.abs(col - spawnCol) + Math.abs(row - spawnRow);
            if (dSpawn < 8) continue;

            int tileNum = gp.tileM.mapData[row][col];
            if (gp.tileM.tile[tileNum].collision) continue;

            int idx = row * gp.maxWorldCol + col;
            if (used.contains(idx)) continue;
            used.add(idx);

            if (placed >= gp.obj.length) break;
            gp.obj[placed] = new key();
            gp.obj[placed].worldX = col * gp.tile_size;
            gp.obj[placed].worldY = row * gp.tile_size;
            placed++;
        }
    }
}
//1. AssetSet ক্লাসের কাজ কী?
//        গেমের মধ্যে অবজেক্ট (যেমন চাবি) তৈরি করে ও তাদের অবস্থান নির্ধারণ করে।
//
//        2. setObject() মেথডে কি হচ্ছে?
//        Key অবজেক্ট তৈরি করে gp.obj অ্যারেতে রাখা হচ্ছে।
//
//        প্রতিটি চাবির স্ক্রিনে কোথায় থাকবে সেটা ঠিক করা হচ্ছে (worldX, worldY দিয়ে)।
//
//        3. কেন gp.tile_size দিয়ে গুন করা?
//        কারণ স্ক্রিনের পিক্সেল অনুযায়ী সঠিক অবস্থানে বসানোর জন্য (টাইলের আকারের সাথে মিলিয়ে)।
//
//        4. AssetSet Constructor এ setObject() কল করছি কেন?
//        যাতে গেম শুরু হওয়ার সাথে সাথে অবজেক্টগুলো অটোমেটিক সেট হয়ে যায়