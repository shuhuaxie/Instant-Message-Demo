package androidLearn.frame.easemobExample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.utils.AudioHelper;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


import java.lang.ref.WeakReference;


public class PlayButton extends ImageView implements View.OnClickListener {
  private String path;
  private boolean leftSide;
  private AnimationDrawable anim;

  private static WeakReference<PlayButton> mPlayingButton;

  public PlayButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    leftSide = getLeftFromAttrs(context, attrs);
    setLeftSide(leftSide);
    setOnClickListener(this);
  }

  public void setLeftSide(boolean leftSide) {
    this.leftSide = leftSide;
    stopRecordAnimation();
  }

  public boolean getLeftFromAttrs(Context context, AttributeSet attrs) {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatPlayBtn);
    boolean left = true;
    for (int i = 0; i < typedArray.getIndexCount(); i++) {
      int attr = typedArray.getIndex(i);
      if (attr == R.styleable.ChatPlayBtn_left) {
        left = typedArray.getBoolean(attr, true);
      }
    }
    return left;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public void onClick(View v) {
    if (AudioHelper.getInstance().isPlaying() == true) {
      if(mPlayingButton != null){
        PlayButton btn = mPlayingButton.get();
        if(btn != null && !btn.equals(this)){
          AudioHelper.getInstance().pausePlayer();
          btn.stopRecordAnimation();
          play();
        }
        else{
          mPlayingButton = null;
          AudioHelper.getInstance().pausePlayer();
          stopRecordAnimation();
        }
      }
    } else {
      play();
    }
  }

  private void play(){
    mPlayingButton = new WeakReference<>(this);
    startRecordAnimation();
    AudioHelper.getInstance().playAudio(path, new Runnable() {
      @Override
      public void run() {
        stopRecordAnimation();
      }
    });
  }

  public void stopPlay(){
    if (AudioHelper.getInstance().isPlaying() == true && AudioHelper.getInstance().getAudioPath().equals(path)) {
      mPlayingButton = null;
      AudioHelper.getInstance().stopPlayer();
      stopRecordAnimation();
    }
  }

  private void startRecordAnimation() {
    if (leftSide) {
      setImageResource(R.anim.chat_anim_voice_left);
    } else {
      setImageResource(R.anim.chat_anim_voice_right);
    }
    anim = (AnimationDrawable) getDrawable();
    anim.start();
  }

  private void stopRecordAnimation() {
    if (leftSide) {
      setImageResource(R.drawable.sound_left_1);
    } else {
      setImageResource(R.drawable.sound_right_1);
    }
    if (anim != null) {
      anim.stop();
    }
  }

  public static void stopAllPlay(){
    if(mPlayingButton != null){
      PlayButton btn = mPlayingButton.get();
      if(btn != null){
        AudioHelper.getInstance().stopPlayer();
        btn.stopRecordAnimation();
      }
      mPlayingButton = null;
    }
  }
}
