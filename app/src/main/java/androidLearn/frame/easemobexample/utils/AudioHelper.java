package androidLearn.frame.easemobexample.utils;

import android.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class AudioHelper {
  private static AudioHelper audioHelper;
  private MediaPlayer mediaPlayer;
  private WeakReference<Runnable> finishCallback;
  private String audioPath;
  private boolean onceStart = false;

  private AudioHelper() {
    mediaPlayer = new MediaPlayer();
  }

  public static AudioHelper getInstance() {
    if (audioHelper == null) {
      audioHelper = new AudioHelper();
    }
    return audioHelper;
  }

  public String getAudioPath() {
    return audioPath;
  }

  public void stopPlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
//      mediaPlayer.release();
//      mediaPlayer = null;
    }
  }

  public void pausePlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.pause();
    }
  }

  public boolean isPlaying() {
    if(mediaPlayer != null){
      return mediaPlayer.isPlaying();
    }
    return false;
  }

  public void restartPlayer() {
    if (mediaPlayer != null && mediaPlayer.isPlaying() == false) {
      mediaPlayer.start();
    }
  }

  public synchronized void playAudio(String path, Runnable finishCallback) {
    if (onceStart) {
      mediaPlayer.reset();
    }
    tryRunFinishCallback();
    audioPath = path;
    AudioHelper.this.finishCallback = new WeakReference<>(finishCallback);
    try {
      File file = new File(path);
      FileInputStream fis = new FileInputStream(file);
      mediaPlayer.setDataSource(fis.getFD());
//      mediaPlayer.setDataSource(path);
      mediaPlayer.prepare();
      mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          tryRunFinishCallback();
        }
      });
      mediaPlayer.start();
      onceStart = true;
    } catch (IOException e) {
      e.printStackTrace();
      tryRunFinishCallback();
    }
  }

  public void tryRunFinishCallback() {
    if (finishCallback != null) {
      Runnable runnable = finishCallback.get();
      if(runnable != null){
        runnable.run();
      }
      finishCallback = null;
    }
  }
}
