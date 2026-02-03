package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// KeyHandler ক্লাস: কীবোর্ডের বোতাম চেপে রাখার অবস্থা ধরে রাখে (WASD দিয়ে মুভমেন্টের জন্য)
public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed; // কোন দিকে প্রেস করা হয়েছে সেটা স্টোর করে
    public boolean pausePressed; // P চাপলে pause toggle করার signal

    @Override
    public void keyTyped(KeyEvent e) {
        // keyTyped এখানে দরকার নাই, খালি রেখে দিছে
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // কোন কি প্রেস করা হয়েছে তার কোড বের করে

        // W চেপে ধরলে upPressed true হয়
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        // S চেপে ধরলে downPressed true হয়
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        // A চেপে ধরলে leftPressed true হয়
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        // D চেপে ধরলে rightPressed true হয়
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        // P চাপলে pause toggle হবে (GamePanel update() এ handle করা হবে)
        if (code == KeyEvent.VK_P) {
            pausePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // কোন কি ছেড়ে দেওয়া হয়েছে তার কোড বের করে

        // W ছেড়ে দিলে upPressed false হয়
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        // S ছেড়ে দিলে downPressed false হয়
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        // A ছেড়ে দিলে leftPressed false হয়
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        // D ছেড়ে দিলে rightPressed false হয়
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
}
//        keyHandler ক্লাসের কাজ কী?
//        কীবোর্ডের কোন বোতাম চাপা হয়েছে বা ছাড়া হয়েছে সেটা ট্র্যাক করে।
//
//        2. কোন key দিয়ে কি হচ্ছে?
//        W চাপলে upPressed true হয় (উপরে যাওয়া)।
//
//        S চাপলে downPressed true হয় (নিচে যাওয়া)।
//
//        A চাপলে leftPressed true হয় (বামে যাওয়া)।
//
//        D চাপলে rightPressed true হয় (ডানে যাওয়া)।
//
//        3. keyPressed আর keyReleased এর পার্থক্য?
//        keyPressed: বোতাম চেপে ধরলে কাজ করে।
//
//        keyReleased: বোতাম ছেড়ে দিলেই কাজ করে।
//
//        4. keyTyped কেন খালি?
//        কারণ এখানে শুধু মুভমেন্ট দরকার ছিল, কোনো ক্যারেক্টার টাইপ করা লাগে না।