package Main;

import Sound.Sound;
import entity.Bullet;
import entity.Player;
import entity.ShooterEnemy;
import object.HealGem;
import object.KeyObject;
import object.TimeGem;
import tiles.TileManage;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

// GamePanel ক্লাস, যেটা গেমের মূল স্ক্রিন এবং ম্যানেজমেন্টের কাজ করে
public class GamePanel extends JPanel implements Runnable {

    // স্ক্রিন সেটআপ
    final int originalTileSize = 16;
    final int scale = 4; // ৪ = বড় ডিসপ্লে (১৬*৪=৬৪px টাইল), ৩ = ছোট
    public final int tile_size = originalTileSize * scale;

    // স্ক্রিনের কলাম ও রো সংখ্যা
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tile_size * maxScreenCol; // স্ক্রিনের মোট প্রস্থ
    public final int screenHeight = tile_size * maxScreenRow; // স্ক্রিনের মোট উচ্চতা

    // ওয়ার্ল্ডের আকার
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tile_size * maxWorldCol;
    public final int worldHeight = tile_size * maxWorldRow;

    final int FPS = 60; // ফ্রেম পার সেকেন্ড
    Thread gameThread; // গেম চালানোর জন্য থ্রেড

    // কীবোর্ড ইনপুটের জন্য অবজেক্ট
    KeyHandler keyboard = new KeyHandler();

    // প্লেয়ারের ডিফল্ট অবস্থান ও স্পিড
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    public Player player = new Player(this, keyboard); // প্লেয়ার অবজেক্ট
    public TileManage tileM = new TileManage(this); // টাইল ম্যানেজমেন্ট অবজেক্ট
    public Sound sound = new Sound(); // সাউন্ড ম্যানেজমেন্ট অবজেক্ট
    public KeyObject[] obj = new KeyObject[30]; // অবজেক্ট গুলোর জন্য অ্যারে (10+ keys support)
    public AssetSet assetSet = new AssetSet(this); // গেমের বিভিন্ন অ্যাসেট সেট করার জন্য অবজেক্ট
    public Collision colissionChecker = new Collision(this); // কলিশন চেক করার জন্য অবজেক্ট

    public final List<Bullet> bullets = new ArrayList<>();
    public final List<ShooterEnemy> shooterEnemies = new ArrayList<>();

    // simple game state
    boolean paused = false;

    // level system
    private static final int LEVEL_TIME_LIMIT_SEC = 90;
    private static final int[] REQUIRED_KEYS_PER_LEVEL = {5, 6, 7, 8, 10}; // 5 levels: L1=5, L5=10
    private int level = 1;
    private int keysCollectedThisLevel = 0;
    private long levelStartNano = 0L;
    private int levelBonusTimeSeconds = 0; // added by time gems
    private boolean transitioning = false;
    private long transitionStartNano = 0L;
    private String bannerText = "";
    private String damageIndicator = "";
    private int damageIndicatorFrames = 0;
    private String gemIndicatorText = "";
    private int gemIndicatorFrames = 0;
    private int gemIndicatorX = 0, gemIndicatorY = 0;
    private boolean pendingNextLevel = false;

    // কনস্ট্রাক্টর - গেমপ্যানেল সেটআপ
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // স্ক্রিনের সাইজ নির্ধারণ
        this.setBackground(Color.black); // ব্যাকগ্রাউন্ড কালো করা
        this.setDoubleBuffered(true); // রেন্ডারিং ভালো করার জন্য ডাবল বাফারিং চালু
        this.addKeyListener(keyboard); // কীবোর্ড লিসেনার অ্যাড করা
        this.setFocusable(true); // ফোকাসযোগ্য করা
    }

    // গেম থ্রেড শুরু করার মেথড
    public void startingGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    long start = System.nanoTime(); // গেম শুরু হওয়ার সময়
    double playTime; // গেমের চলার সময় ট্র্যাক করার ভেরিয়েবল

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; // প্রতি ফ্রেমের সময় (৬০ FPS অনুযায়ী)
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            long current = System.nanoTime();
            update(); // গেমের অবস্থা আপডেট করা
            playTime = (current - start) / 1000000000.0; // প্লে টাইম আপডেট করা
            repaint(); // স্ক্রিনে নতুন করে আঁকা

            try {
                long remainingTime = (long) (nextDrawTime - System.nanoTime());
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep(remainingTime); // বাকি সময় অপেক্ষা করা
                nextDrawTime = nextDrawTime + drawInterval; // পরবর্তী ড্র সময় ঠিক করা
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // গেম আপডেট করার মেথড (প্রতিবার প্লেয়ারের অবস্থা আপডেট হয়)
    public void update() {
        // pause toggle edge
        if (keyboard.pausePressed) {
            paused = !paused;
            keyboard.pausePressed = false;
        }
        if (paused) return;

        // transition freeze (small banner time)
        if (transitioning) {
            double t = (System.nanoTime() - transitionStartNano) / 1_000_000_000.0;
            if (t >= 1.5) {
                transitioning = false;
                if (pendingNextLevel) {
                    pendingNextLevel = false;
                    startLevel(level + 1);
                } else {
                    bannerText = "";
                }
            }
            return;
        }

        // time limit check
        if (levelStartNano != 0L) {
            double elapsed = (System.nanoTime() - levelStartNano) / 1_000_000_000.0;
            int limit = LEVEL_TIME_LIMIT_SEC + levelBonusTimeSeconds;
            if (elapsed >= limit) {
                failLevel();
                return;
            }
        }

        if (player.hp <= 0) {
            failLevel();
            return;
        }

        player.update();

        for (ShooterEnemy e : shooterEnemies) e.update(bullets);
        for (Bullet b : bullets) b.update(this);
        // Bullet vs player
        Rectangle playerBox = new Rectangle(
                player.worldX + player.solidAreaX,
                player.worldY + player.solidAreaY,
                player.solid.width,
                player.solid.height);
        for (Bullet b : bullets) {
            if (!b.active) continue;
            if (b.getHitbox().intersects(playerBox)) {
                player.takeDamage(1);
                showDamageIndicator(1);
                b.active = false;
            }
        }
        bullets.removeIf(b -> !b.active);
        if (damageIndicatorFrames > 0) damageIndicatorFrames--;
        if (gemIndicatorFrames > 0) gemIndicatorFrames--;
    }

    DecimalFormat df = new DecimalFormat("0.00"); // সময় সুন্দরভাবে ফরম্যাট করার জন্য

    // স্ক্রিনে আঁকার মেথড
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;

        tileM.draw(g2); // টাইল আঁকা
        // অবজেক্ট গুলো আঁকা
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }
        // স্কোর বা টাইম দেখানোর জন্য লেখা
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.setColor(Color.white);
        g2.drawString("Level: " + level, tile_size * 11, 50);
        g2.drawString("Time: " + df.format(playTime), tile_size * 11, 78);

        int required = getRequiredKeys(level);
        g2.drawString("Keys: " + keysCollectedThisLevel + "/" + required, tile_size * 11, 106);

        int timeLeft = getTimeLeftSeconds();
        g2.drawString("Left: " + formatTime(timeLeft), tile_size * 11, 134);
        g2.drawString("HP: " + player.hp + "/" + Player.MAX_HP, tile_size * 11, 162);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.YELLOW);
        g2.drawString("v2", tile_size * 11, 182);

        for (ShooterEnemy e : shooterEnemies) e.draw(g2, this);
        for (Bullet b : bullets) b.draw(g2, this);
        player.draw(g2); // প্লেয়ার আঁকা
        if (damageIndicatorFrames > 0 && !damageIndicator.isEmpty()) {
            int px = player.getScreenX() + tile_size / 2;
            int py = player.getScreenY() - 8 - (60 - damageIndicatorFrames) / 3;
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(Color.RED);
            g2.drawString(damageIndicator, px - 15, py);
        }
        if (gemIndicatorFrames > 0 && !gemIndicatorText.isEmpty()) {
            int py = gemIndicatorY - 10 - (50 - gemIndicatorFrames) / 2;
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.setColor(new Color(50, 220, 100));
            int w = g2.getFontMetrics().stringWidth(gemIndicatorText);
            g2.drawString(gemIndicatorText, gemIndicatorX - w / 2, py);
        }

        if (!bannerText.isEmpty()) {
            g2.setFont(new Font("Arial", Font.BOLD, 42));
            g2.setColor(new Color(255, 255, 255, 230));
            int x = screenWidth / 2 - 180;
            int y = 80;
            g2.drawString(bannerText, x, y);
        }

        if (paused) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawString("PAUSED", screenWidth / 2 - 110, screenHeight / 2);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.drawString("Press P to resume", screenWidth / 2 - 85, screenHeight / 2 + 30);
        }

        g2.dispose(); // গ্রাফিক্স রিসোর্স ছেড়ে দেওয়া
    }

    // গেম শুরু করার জন্য প্রয়োজনীয় সেটআপ
    public void setupGame() {
        startLevel(1);
        playMusic(0); // মিউজিক চালানো
    }

    // মিউজিক চালানোর মেথড
    public void playMusic(int i) {
        sound.setClip(i);
        sound.play();
        sound.loop();
    }

    // মিউজিক বন্ধ করার মেথড
    public void soundSTOP() {
        sound.stop();
    }

    // বিশেষ কোনো সাউন্ড চালানোর মেথড (যেমন Gem collect করার সময়)
    public void setGemSound(int i) {
        sound.setClip(i);
        sound.play();
    }

    /** Handle collecting any object (key, HealGem, TimeGem) by name. */
    public void collectObject(int objectIndex) {
        if (objectIndex < 0 || objectIndex >= obj.length) return;
        if (obj[objectIndex] == null) return;

        String name = obj[objectIndex].name;
        setGemSound(1);
        obj[objectIndex] = null;

        if ("Gems".equals(name)) {
            keysCollectedThisLevel++;
            int required = getRequiredKeys(level);
            if (keysCollectedThisLevel >= required) completeLevel();
        } else if ("HealGem".equals(name)) {
            player.heal(HealGem.HEAL_AMOUNT);
            showGemIndicator(player.getScreenX() + tile_size / 2, player.getScreenY(), "+1", true);
        } else if ("TimeGem".equals(name)) {
            addTimeToLevel(TimeGem.TIME_SECONDS);
            showGemIndicator(player.getScreenX() + tile_size / 2, player.getScreenY(), "+1", true);
        }
    }

    /** জেম নিলে সবুজ +1 ভাসমান দেখাতে। */
    public void showGemIndicator(int screenX, int screenY, String text, boolean green) {
        gemIndicatorText = text;
        gemIndicatorFrames = 50;
        gemIndicatorX = screenX;
        gemIndicatorY = screenY;
    }

    public void addTimeToLevel(int seconds) {
        levelBonusTimeSeconds += seconds;
    }

    /** গুলি লাগলে ড্যামেজ সংখ্যা দেখাতে (যেমন -1)। */
    public void showDamageIndicator(int damage) {
        damageIndicator = "-" + damage;
        damageIndicatorFrames = 60;
    }

    private void startLevel(int newLevel) {
        if (newLevel < 1) newLevel = 1;
        if (newLevel > REQUIRED_KEYS_PER_LEVEL.length) newLevel = REQUIRED_KEYS_PER_LEVEL.length;

        level = newLevel;
        keysCollectedThisLevel = 0;
        levelBonusTimeSeconds = 0;
        player.resetToSpawn();
        bullets.clear();
        shooterEnemies.clear();
        tileM.loadLevel(level);
        assetSet.placeKeysForLevel(level, getRequiredKeys(level));
        assetSet.placeEnemiesForLevel(level);

        levelStartNano = System.nanoTime();
        bannerText = "LEVEL " + level;
        transitioning = true;
        transitionStartNano = System.nanoTime();
    }

    private void completeLevel() {
        if (level >= REQUIRED_KEYS_PER_LEVEL.length) {
            bannerText = "YOU WIN!";
            transitioning = true;
            transitionStartNano = System.nanoTime();
            pendingNextLevel = true;
            level = 0;
            return;
        }
        bannerText = "LEVEL CLEAR!";
        transitioning = true;
        transitionStartNano = System.nanoTime();
        pendingNextLevel = true;
    }

    private void failLevel() {
        bannerText = "TIME UP!";
        transitioning = true;
        transitionStartNano = System.nanoTime();
        startLevel(level);
    }

    public int getLevel() { return level; }

    private int getRequiredKeys(int level) {
        int idx = Math.max(0, Math.min(REQUIRED_KEYS_PER_LEVEL.length - 1, level - 1));
        return REQUIRED_KEYS_PER_LEVEL[idx];
    }

    private int getTimeLeftSeconds() {
        if (levelStartNano == 0L) return LEVEL_TIME_LIMIT_SEC + levelBonusTimeSeconds;
        double elapsed = (System.nanoTime() - levelStartNano) / 1_000_000_000.0;
        int limit = LEVEL_TIME_LIMIT_SEC + levelBonusTimeSeconds;
        int left = (int) Math.ceil(limit - elapsed);
        return Math.max(0, left);
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d", m, s);
    }

}
//1. DriverClass এর কাজ কী?
//        প্রোগ্রামের main class, এখানে JFrame তৈরি করে GamePanel সেটাপ করে ও গেম শুরু করে।
//
//        2. window.pack() কেন ব্যবহার করছি?
//        যাতে JFrame এর সাইজ GamePanel এর preferred size অনুযায়ী হয়, manually দিতে না লাগে।
//
//        3. gamePanel.setupGame() কেন?
//        গেমের শুরুতে অবজেক্ট (key বসানো, sound চালানো) ইত্যাদি সেটাপ করে।
//
//        4. startingGameThread() কী করে?
//        আলাদা একটা Thread চালু করে, যেখানে প্রতিবার update() আর repaint() হয় — তাই গেম চলতে থাকে।