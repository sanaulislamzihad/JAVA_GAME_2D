package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

// Key object class, যেটা Gems হিসেবে কাজ করবে
public class key extends KeyObject {

    // Constructor - Name আর image set করে
    public key() {
        name = "Gems";  // Key object এর নাম 'Gems' রাখা হয়েছে

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//        key class এর কাজ কী?
//        👉 KeyObject class কে extend করে একটা key object তৈরি করে, যেটা game-এ gem হিসেবে কাজ করবে (গেমে collectible item হিসেবে কাজ করে)।
//
//        ➔ name কেন "Gems" রাখা হয়েছে?
//        👉 Game-এ key কে gem নামে ব্যবহার করতে চাইলে, তার name "Gems" দেওয়া হয়েছে।
//
//        ➔ ImageIO.read() কেন ব্যবহার করা হয়েছে?
//        👉 Image ফাইলকে load করে key object এর image হিসাবে সেট করা হচ্ছে, যাতে screen এ দেখা যায়।