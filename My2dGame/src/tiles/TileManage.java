package tiles;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Objects;

public class TileManage {
    GamePanel gp;
    public Tiles[] tile;
    public int mapData [][];
    public TileManage(GamePanel gp)
    {
        this.gp = gp;
        tile = new Tiles[10];
        mapData = new int [gp.maxWorldRow][gp.maxWorldCol];
        tileImage();
        loadLevel(1);
    }
    public void tileImage ()
    {
        try {
            tile[0] = new Tiles();
            tile[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/grass.png")));
            tile[1] = new Tiles();
            tile[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/wall.png")));
            tile[2] = new Tiles();
            tile[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/water.png")));
            tile[3] = new Tiles();
            tile[3].image = ImageIO.read (Objects.requireNonNull(getClass().getResourceAsStream("/tiles/earth.png")));
            tile[4] = new Tiles();
            tile[4].image = ImageIO.read (Objects.requireNonNull(getClass().getResourceAsStream("/tiles/grass.png")));
            tile[5] = new Tiles();
            tile[5].image = ImageIO.read (Objects.requireNonNull(getClass().getResourceAsStream("/tiles/sand.png")));

            // extra decorative/solid tile
            tile[6] = new Tiles();
            tile[6].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/tree.png")));

            // collisions
            tile[1].collision = true; // wall
            tile[2].collision = true; // water
            tile[6].collision = true; // tree
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadLevel(int level) {
        // base fill: grass
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                mapData[row][col] = 0;
            }
        }

        // Hard border so player never sees black void (also solid)
        for (int col = 0; col < gp.maxWorldCol; col++) {
            mapData[0][col] = 1;
            mapData[gp.maxWorldRow - 1][col] = 1;
        }
        for (int row = 0; row < gp.maxWorldRow; row++) {
            mapData[row][0] = 1;
            mapData[row][gp.maxWorldCol - 1] = 1;
        }

        // level-wise obstacles (deterministic, pro look)
        generateLevelObstacles(level);
    }

    private void generateLevelObstacles(int level) {
        // spawn safe zone
        int spawnCol = 23;
        int spawnRow = 21;

        // clear a safe area around spawn
        clearArea(spawnCol, spawnRow, 4);

        // create some "paths" using earth/sand for nicer look
        drawHorizontalPath(5 + level, 2 + level, gp.maxWorldCol - 6 - level, 5); // earth
        drawVerticalPath(5 + level, 3 + level, gp.maxWorldRow - 6 - level, 5);  // earth

        // add ponds + walls/trees based on difficulty
        Random r = new Random(1337L + level * 999L);

        int ponds = Math.min(1 + level, 4);
        for (int i = 0; i < ponds; i++) {
            int w = 4 + r.nextInt(4 + level);
            int h = 3 + r.nextInt(4 + level);
            int x = 2 + r.nextInt(gp.maxWorldCol - w - 3);
            int y = 2 + r.nextInt(gp.maxWorldRow - h - 3);
            if (distanceManhattan(x, y, spawnCol, spawnRow) < 10) continue;
            fillRect(y, x, h, w, 2); // water
            outlineRect(y, x, h, w, 5); // sand shore
        }

        int walls = 20 + level * 18;
        for (int i = 0; i < walls; i++) {
            int x = 2 + r.nextInt(gp.maxWorldCol - 4);
            int y = 2 + r.nextInt(gp.maxWorldRow - 4);
            if (distanceManhattan(x, y, spawnCol, spawnRow) < 8) continue;
            if (mapData[y][x] == 2) continue;
            mapData[y][x] = 1; // wall
        }

        int trees = 30 + level * 20;
        for (int i = 0; i < trees; i++) {
            int x = 2 + r.nextInt(gp.maxWorldCol - 4);
            int y = 2 + r.nextInt(gp.maxWorldRow - 4);
            if (distanceManhattan(x, y, spawnCol, spawnRow) < 7) continue;
            int num = mapData[y][x];
            if (num == 1 || num == 2) continue;
            mapData[y][x] = 6; // tree
        }

        // keep spawn area always open
        clearArea(spawnCol, spawnRow, 4);
    }

    private void clearArea(int centerCol, int centerRow, int radius) {
        for (int row = centerRow - radius; row <= centerRow + radius; row++) {
            for (int col = centerCol - radius; col <= centerCol + radius; col++) {
                if (row <= 0 || row >= gp.maxWorldRow - 1 || col <= 0 || col >= gp.maxWorldCol - 1) continue;
                mapData[row][col] = 0; // grass
            }
        }
    }

    private void drawHorizontalPath(int row, int startCol, int endCol, int tileNum) {
        if (row <= 0 || row >= gp.maxWorldRow - 1) return;
        for (int col = Math.max(1, startCol); col <= Math.min(gp.maxWorldCol - 2, endCol); col++) {
            mapData[row][col] = tileNum;
        }
    }

    private void drawVerticalPath(int col, int startRow, int endRow, int tileNum) {
        if (col <= 0 || col >= gp.maxWorldCol - 1) return;
        for (int row = Math.max(1, startRow); row <= Math.min(gp.maxWorldRow - 2, endRow); row++) {
            mapData[row][col] = tileNum;
        }
    }

    private void fillRect(int startRow, int startCol, int height, int width, int tileNum) {
        for (int row = startRow; row < startRow + height; row++) {
            for (int col = startCol; col < startCol + width; col++) {
                if (row <= 0 || row >= gp.maxWorldRow - 1 || col <= 0 || col >= gp.maxWorldCol - 1) continue;
                mapData[row][col] = tileNum;
            }
        }
    }

    private void outlineRect(int startRow, int startCol, int height, int width, int tileNum) {
        for (int col = startCol; col < startCol + width; col++) {
            setIfInside(startRow - 1, col, tileNum);
            setIfInside(startRow + height, col, tileNum);
        }
        for (int row = startRow; row < startRow + height; row++) {
            setIfInside(row, startCol - 1, tileNum);
            setIfInside(row, startCol + width, tileNum);
        }
    }

    private void setIfInside(int row, int col, int tileNum) {
        if (row <= 0 || row >= gp.maxWorldRow - 1 || col <= 0 || col >= gp.maxWorldCol - 1) return;
        // don't overwrite hard collisions with shore
        if (mapData[row][col] == 1) return;
        mapData[row][col] = tileNum;
    }

    private int distanceManhattan(int c1, int r1, int c2, int r2) {
        return Math.abs(c1 - c2) + Math.abs(r1 - r2);
    }
    public void mapDataset(String s)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(s));
            int row = 0;
            int col = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow)
            {
                String line = reader.readLine();
                while (col < gp.maxWorldCol)
                {
                    String[] numbers = line.split (" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapData[row][col] = num;
                    col++;
                }
                if (col == gp.maxWorldCol)
                {
                    col = 0;
                    row++;
                }
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void draw(Graphics2D g2)
    {
        int row = 0;
        int col = 0;
        while (row < gp.maxWorldRow && col < gp.maxWorldCol)
        {
            int num = mapData[row][col];
            int worldX = col * gp.tile_size;
            int worldY = row * gp.tile_size;
            int screenX = worldX - gp.player.worldX  + gp.player.getScreenX();
            int screenY = worldY - gp.player.worldY + gp.player.getScreenY();
            g2.drawImage(tile[num].image, screenX,screenY, gp.tile_size,gp.tile_size, null);

            col++;
            if (col == gp.maxWorldCol)
            {
                col = 0;
                row++;
            }
        }
    }


}
//tileImage()
//
//এখানে গেমের বিভিন্ন টাইলসের ইমেজ লোড করা হয়, যেমন ঘাস, দেয়াল, পানি ইত্যাদি। এগুলো সব .png ফাইলের মাধ্যমে লোড হয়।
//
//mapDataset(String s)
//
//এখানে একটি ম্যাপ ডাটা ফাইল (যেমন, world01.txt) পড়া হয়, যাতে গেমের ম্যাপের তথ্য (টাইলের অবস্থান) থাকে।
//
//ফাইলটি পড়ে প্রত্যেক টাইলের জন্য একটি নাম্বার দেওয়া হয়। এই নাম্বার দ্বারা টাইলসের ধরণ নির্ধারণ হয় (যেমন ঘাস, পানি, দেয়াল ইত্যাদি)।
//
//draw(Graphics2D g2)
//
//এই মেথডটি গেম স্ক্রীনে টাইলসগুলো আঁকতে সাহায্য করে। প্রতিটি টাইলের অবস্থান এবং তার ইমেজ স্ক্রীনে নির্দিষ্ট অবস্থানে ড্র করে।
//
//প্লেয়ারটির অবস্থান অনুসারে স্ক্রীনে টাইলসগুলোর সঠিক অবস্থান নির্ধারণ করা হয়।


