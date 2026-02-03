package Sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class Sound {
    Clip clip,clipGems;
    URL[] soundURL = new URL[3];

    public Sound ()
    {
        soundURL[0] = getClass().getResource("/sound/Minish Village.wav");
        soundURL[1] = getClass().getResource("/sound/Gems.wav");
        soundURL[2] = getClass().getResource("/sound/fanfare.wav");
    }
    public void setClip (int i)
    {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);


        } catch (Exception e) {

        }
    }
    public void play ()
    {
        clip.start();

    }
    public void loop ()
    {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop ()
    {
        clip.stop();
    }

}
