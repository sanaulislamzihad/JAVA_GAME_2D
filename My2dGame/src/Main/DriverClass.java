package Main;

import javax.swing.*;

// DriverClass: প্রোগ্রামের main entry point, JFrame তৈরি করে GamePanel চালায়
public class DriverClass {
    public static void main(String[] args) {

        // নতুন একটা window তৈরি করা (JFrame)
        JFrame window = new JFrame();

        // X বোতাম চাপলে প্রোগ্রাম বন্ধ হবে
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window রিসাইজ করা যাবে না
        window.setResizable(false);

        // Window এর title সেট করা
        window.setTitle("The Legend of Zelda");

        // GamePanel অবজেক্ট তৈরি করা
        GamePanel gamePanel = new GamePanel();

        // GamePanel কে window-র ভিতরে add করা
        window.add(gamePanel);

        // Component গুলা their preferred size অনুযায়ী adjust করা
        window.pack();

        // Window screen-এর মাঝখানে দেখা যাবে
        window.setLocationRelativeTo(null);

        // Window visible করা
        window.setVisible(true);
        // Key input কাজ করার জন্য focus ensure
        gamePanel.requestFocusInWindow();

        // Game শুরু করার জন্য দরকারি অবজেক্ট তৈরি করা (Key বসানো, Sound চালানো ইত্যাদি)
        gamePanel.setupGame();

        // GameThread চালু করা (FPS অনুযায়ী update আর repaint হবে)
        gamePanel.startingGameThread();
    }
}
