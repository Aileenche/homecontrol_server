package eu.sunrave.homecontrol_server.Threads;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Admin on 01.01.2016.
 */
public class MediaPlayer implements Runnable {
    private static final int BUFFER_SIZE = 4096;

    @Override
    public void run() {
        URL audioFilePath = null;
        try {
            audioFilePath = new URL("http://listen.technobase.fm/tunein-aacplus-pls");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        MediaPlayer player = this;
        player.play(audioFilePath);
    }

    /**
     * Play a given audio file.
     *
     * @param audioFilePath Path of the audio file.
     */
    void play(URL audioFilePath) {
        //File audioFile = new File(audioFilePath);
        try {
            URL s = new URL("http://listen.technobase.fm/tunein-mp3-pls");
            URLConnection uc = s.openConnection();
            InputStream is = uc.getInputStream();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();
            System.out.println("Playback started.");
            byte[] bytesBuffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                audioLine.write(bytesBuffer, 0, bytesRead);
            }
            audioLine.drain();
            audioLine.close();
            audioStream.close();
            System.out.println("Playback completed.");
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
    }
}
