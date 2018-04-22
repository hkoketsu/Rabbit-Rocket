/**
Audio for the game. Both background music and sound effects.
@author Hiroki Koketsu
@version 2018-04-21 10h00
*/

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.*;

public class Audio {
  private Clip background_clip;
  private Clip shoot_clip;
  private Clip exp_clip;
  private Clip damage_clip;
  private Clip go_clip;
  private Clip alien_clip;

  public Audio() {
    background_clip = createClip(new File("audio/distantfuture.wav"));
  }

  // background music
  public void playBackgroundMusic(){
    background_clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

  // stop background music for when it becomes game over
  public void stopBackgroundMusic() {
    background_clip.close();
  }

  // shooting sound
  public void playShootSound() {
    shoot_clip = createClip(new File("audio/shot.wav"));
    playSound(shoot_clip);
  }

  // damage sound
  public void playDamageSound() {
    damage_clip = createClip(new File("audio/damage.wav"));
    playSound(damage_clip);
  }

  // explosion sound for star shot
  public void playExpSound() {
    exp_clip = createClip(new File("audio/exp.wav"));
    playSound(exp_clip);
  }

  // sound for alien shot
  public void playAlienSound() {
    alien_clip = createClip(new File("audio/alien.wav"));
    playSound(alien_clip);
  }

  // explosion sound for game over
  public void playGameOverSound() {
    go_clip = createClip(new File("audio/gameover.wav"));
    playSound(go_clip);
  }

  // play the sound in general. close the file if the audio is still playing.
  private void playSound(Clip c) {
    if(c.isRunning()) {
      c.close();
    }
    c.start();
  }

  public static Clip createClip(File path) {
    try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
      AudioFormat af = ais.getFormat();
      DataLine.Info dataLine = new DataLine.Info(Clip.class,af);
      Clip c = (Clip)AudioSystem.getLine(dataLine);
      c.open(ais);
      return c;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
    return null;
  }
}
