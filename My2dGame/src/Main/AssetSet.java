package Main;

import entity.ShooterEnemy;
import object.HealGem;
import object.key;
import object.TimeGem;
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

    /** টাইল ওয়াকেবল কিনা (null-safe). */
    private boolean isWalkable(int tileNum) {
        if (tileNum < 0 || tileNum >= gp.tileM.tile.length) return false;
        tiles.Tiles t = gp.tileM.tile[tileNum];
        return t != null && !t.collision;
    }

    // নির্দিষ্ট level + count অনুযায়ী keys place করবে (walkable tiles এ) + HealGems and TimeGems
    public void placeKeysForLevel(int level, int keyCount) {
        clearObjects();

        Random r = new Random(2026L + level * 777L);
        Set<Integer> used = new HashSet<>();

        int spawnCol = 23;
        int spawnRow = 21;
        int placed = 0;
        int safety = 0;

        // Place keys first
        while (placed < keyCount && safety < 20000) {
            safety++;
            int col = 2 + r.nextInt(gp.maxWorldCol - 4);
            int row = 2 + r.nextInt(gp.maxWorldRow - 4);
            int dSpawn = Math.abs(col - spawnCol) + Math.abs(row - spawnRow);
            if (dSpawn < 8) continue;
            int tileNum = gp.tileM.mapData[row][col];
            if (!isWalkable(tileNum)) continue;
            int idx = row * gp.maxWorldCol + col;
            if (used.contains(idx)) continue;
            used.add(idx);
            if (placed >= gp.obj.length) break;
            gp.obj[placed] = new key();
            gp.obj[placed].worldX = col * gp.tile_size;
            gp.obj[placed].worldY = row * gp.tile_size;
            placed++;
        }

        // HealGems: level অনুযায়ী (L1=3, L2=4, L3=5 ...)
        int healCount = 2 + level;
        safety = 0;
        while (placed < keyCount + healCount && safety < 20000) {
            safety++;
            int col = 2 + r.nextInt(gp.maxWorldCol - 4);
            int row = 2 + r.nextInt(gp.maxWorldRow - 4);
            int dSpawn = Math.abs(col - spawnCol) + Math.abs(row - spawnRow);
            if (dSpawn < 8) continue;
            int tileNum = gp.tileM.mapData[row][col];
            if (!isWalkable(tileNum)) continue;
            int idx = row * gp.maxWorldCol + col;
            if (used.contains(idx)) continue;
            used.add(idx);
            if (placed >= gp.obj.length) break;
            gp.obj[placed] = new HealGem();
            gp.obj[placed].worldX = col * gp.tile_size;
            gp.obj[placed].worldY = row * gp.tile_size;
            placed++;
        }

        // TimeGems: level অনুযায়ী (L1=3, L2=4, L3=5 ...)
        int timeCount = 2 + level;
        safety = 0;
        while (placed < keyCount + healCount + timeCount && safety < 20000) {
            safety++;
            int col = 2 + r.nextInt(gp.maxWorldCol - 4);
            int row = 2 + r.nextInt(gp.maxWorldRow - 4);
            int dSpawn = Math.abs(col - spawnCol) + Math.abs(row - spawnRow);
            if (dSpawn < 8) continue;
            int tileNum = gp.tileM.mapData[row][col];
            if (!isWalkable(tileNum)) continue;
            int idx = row * gp.maxWorldCol + col;
            if (used.contains(idx)) continue;
            used.add(idx);
            if (placed >= gp.obj.length) break;
            gp.obj[placed] = new TimeGem();
            gp.obj[placed].worldX = col * gp.tile_size;
            gp.obj[placed].worldY = row * gp.tile_size;
            placed++;
        }

            }

    /** Place shooter enemies for the level. অন্তত একটা স্পনের কাছেই থাকবে যাতে দেখা যায়। */
    public void placeEnemiesForLevel(int level) {
        gp.shooterEnemies.clear();
        Random r = new Random(3030L + level * 999L);
        Set<Integer> used = new HashSet<>();
        int spawnCol = 23;
        int spawnRow = 21;
        int count = Math.min(2, level + 1);
        int safety = 0;
        while (gp.shooterEnemies.size() < count && safety < 5000) {
            safety++;
            int col = 5 + r.nextInt(gp.maxWorldCol - 10);
            int row = 5 + r.nextInt(gp.maxWorldRow - 10);
            int dSpawn = Math.abs(col - spawnCol) + Math.abs(row - spawnRow);
            if (dSpawn < 10) continue;
            int tileNum = gp.tileM.mapData[row][col];
            if (!isWalkable(tileNum)) continue;
            int idx = row * gp.maxWorldCol + col;
            if (used.contains(idx)) continue;
            used.add(idx);
            int worldX = col * gp.tile_size;
            int worldY = row * gp.tile_size;
            gp.shooterEnemies.add(new ShooterEnemy(gp, worldX, worldY));
            gp.tileM.mapData[row][col] = 3; // এনিমি জায়গায় ঘাস না, earth
        }
        // অন্তত ১টা এনিমি স্পনের কাছেই (ডান-নিচে) যাতে শুরুতে ই দেখা যায়
        int nearCol = spawnCol + 5;
        int nearRow = spawnRow + 2;
        if (nearRow < gp.maxWorldRow && nearCol < gp.maxWorldCol && isWalkable(gp.tileM.mapData[nearRow][nearCol])) {
            gp.shooterEnemies.add(new ShooterEnemy(gp, nearCol * gp.tile_size, nearRow * gp.tile_size));
            gp.tileM.mapData[nearRow][nearCol] = 3; // এনিমি জায়গায় earth (ঘাস নাই)
        }
        if (gp.shooterEnemies.isEmpty()) {
            int fc = spawnCol + 6;
            gp.shooterEnemies.add(new ShooterEnemy(gp, fc * gp.tile_size, spawnRow * gp.tile_size));
            if (spawnRow < gp.maxWorldRow && fc < gp.maxWorldCol) gp.tileM.mapData[spawnRow][fc] = 3;
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