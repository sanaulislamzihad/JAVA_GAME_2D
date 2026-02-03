package Main;

import javax.swing.*;
import java.awt.*;

// DriverClass: প্রোগ্রামের main entry point, JFrame তৈরি করে GamePanel চালায়
public class DriverClass {
    public static void main(String[] args) {

        // নতুন একটা window তৈরি করা (JFrame)
        JFrame window = new JFrame();

        // X বোতাম চাপলে প্রোগ্রাম বন্ধ হবে
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window রিসাইজ করা যাবে – গেম ফিক্স রেজোলিউশনে স্কেল হয়ে দেখাবে
        window.setResizable(true);

        // Window এর title সেট করা
        window.setTitle("The Legend of Zelda");

        GamePanel gamePanel = new GamePanel();

        // গেম ফিক্স সাইজে, উইন্ডো বড় করলে মাঝে গেম – গেমপ্লে ঠিক
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.BLACK);
        wrapper.add(gamePanel);
        window.add(wrapper);

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
