package androidLearn.frame.easemobexample.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.utils.PathUtils;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

public class RecordButton extends Button {
  public static final int BACK_RECORDING = R.drawable.text_field_input_pressed;
  public static final int BACK_IDLE = R.drawable.text_field_input;
  public static final int RELEASE_DRAW = R.drawable.text_field_siri_close;
  public static final int SLIDE_UP_TO_CANCEL = 0;
  public static final int RELEASE_TO_CANCEL = 1;
  private static final int MIN_INTERVAL_TIME = 1000;// 2s
  private static int[] recordImageIds = {
      R.drawable.text_field_siri_icon1,
      R.drawable.text_field_siri_icon2,
      R.drawable.text_field_siri_icon3,
      R.drawable.text_field_siri_icon4,
      R.drawable.text_field_siri_icon5,
      R.drawable.text_field_siri_icon6,
      R.drawable.text_field_siri_icon7,
      R.drawable.text_field_siri_icon8,
      R.drawable.text_field_siri_icon9,
      R.drawable.text_field_siri_icon10,
  };
  private TextView textView;
  private String outputPath = null;
  private RecordEventListener recordEventListener;
  private long startTime;
  private Dialog recordIndicator;
  private View view;
  private MediaRecorder recorder;
  private ObtainDecibelThread thread;
  private Handler volumeHandler;
  private ImageView imageView;
  private int status;
  private OnDismissListener onDismiss = new OnDismissListener() {

    @Override
    public void onDismiss(DialogInterface dialog) {
      stopRecording();
    }
  };
  private String conversationId;

  public RecordButton(Context context) {
    super(context);
    init();
  }

  public RecordButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public RecordButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public void setSavePath(String path) {
    outputPath = path;
  }

  public void setConversationId(String conversationId){
    this.conversationId = conversationId;
  }

  public void setRecordEventListener(RecordEventListener listener) {
    recordEventListener = listener;
  }

  private void init() {
    volumeHandler = new ShowVolumeHandler();
    setBackgroundResource(BACK_IDLE);
    setText(getResources().getString(R.string.button_pushtotalk));
    initRecordDialog();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (conversationId == null)
      return false;
    int action = event.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        setText(getResources().getString(R.string.button_releasetostop));
        startRecord();
        break;
      case MotionEvent.ACTION_UP:
        setText(getResources().getString(R.string.button_pushtotalk));
        if (status == RELEASE_TO_CANCEL) {
          cancelRecord();
        } else {
          finishRecord();
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (event.getY() < 0) {
          status = RELEASE_TO_CANCEL;
        } else {
          status = SLIDE_UP_TO_CANCEL;
        }
        setTextViewByStatus();
        break;
      case MotionEvent.ACTION_CANCEL:
        cancelRecord();
        break;
    }
    return true;
  }

  public int getColor(int id) {
    return getContext().getResources().getColor(id);
  }

  private void setTextViewByStatus() {
    if (status == RELEASE_TO_CANCEL) {
      textView.setTextColor(getColor(R.color.font_color_red));
      textView.setText(R.string.release_to_cancel);
    } else if (status == SLIDE_UP_TO_CANCEL) {
      textView.setTextColor(getColor(android.R.color.white));
      textView.setText(R.string.move_up_to_cancel);
    }
  }

  private void startRecord() {
    setSavePath(PathUtils.getRecordPathByCurrentTime(conversationId));
    startTime = System.currentTimeMillis();
    setBackgroundResource(BACK_RECORDING);
    startRecording();
    recordIndicator.show();
  }

  private void initRecordDialog() {
    recordIndicator = new Dialog(getContext(),
        R.style.chat_record_button_toast_dialog_style);

    view = inflate(getContext(), R.layout.chat_record_layout, null);
    imageView = (ImageView) view.findViewById(R.id.imageView);
    textView = (TextView) view.findViewById(R.id.textView);
    recordIndicator.setContentView(view, new LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT));
    recordIndicator.setOnDismissListener(onDismiss);

    LayoutParams lp = recordIndicator.getWindow().getAttributes();
    lp.gravity = Gravity.CENTER;
  }

  private void removeFile() {
    File file = new File(outputPath);
    if (file.exists()) {
      file.delete();
    }
  }

  private void finishRecord() {
    stopRecording();
    recordIndicator.dismiss();
    setBackgroundResource(BACK_IDLE);

    long intervalTime = System.currentTimeMillis() - startTime;
    if (intervalTime < MIN_INTERVAL_TIME) {
      Toast.makeText(getContext(), getContext().getString(R.string.chat_record_button_pleaseSayMore), Toast.LENGTH_SHORT).show();
      removeFile();
      return;
    }

    int sec = Math.round(intervalTime * 1.0f / 1000);
    if (recordEventListener != null) {
      recordEventListener.onFinishedRecord(outputPath, sec);
    }
  }

  private void cancelRecord() {
    stopRecording();
    setBackgroundResource(BACK_IDLE);
    recordIndicator.dismiss();
    Toast.makeText(getContext(), getContext().getString(R.string.chat_cancelRecord),
        Toast.LENGTH_SHORT).show();
    removeFile();
  }

  private void startRecording() {
    try {
      if (recorder == null) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(8000);
        recorder.setAudioEncodingBitRate(64);
        recorder.setOutputFile(outputPath);
        recorder.prepare();
      } else {
        recorder.reset();
        recorder.setOutputFile(outputPath);
      }
      recorder.start();
      thread = new ObtainDecibelThread();
      thread.start();
      recordEventListener.onStartRecord();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void stopRecording() {
    if (thread != null) {
      thread.exit();
      thread = null;
    }
    if (recorder != null) {
      try {
        recorder.stop();
      } catch (Exception e) {
      } finally {
        recorder.release();
        recorder = null;
      }
    }
  }

  public interface RecordEventListener {
    public void onFinishedRecord(String audioPath, int secs);

    void onStartRecord();
  }

  private class ObtainDecibelThread extends Thread {
    private volatile boolean running = true;

    public void exit() {
      running = false;
    }

    @Override
    public void run() {
      while (running) {
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (recorder == null || !running) {
          break;
        }
        int x = recorder.getMaxAmplitude();
        int size = recordImageIds.length - 1;
        volumeHandler.sendEmptyMessage(x * size / 32767);
//        if (x != 0) {
//          int f = (int) (10 * Math.log(x) / Math.log(10));
//          int index = (f - 18) / size;
//          if (index < 0) index = 0;
//          if (index > size) index = size;
//          volumeHandler.sendEmptyMessage(index);
//        }
      }
    }

  }

  class ShowVolumeHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (status == RELEASE_TO_CANCEL) {
        imageView.setImageResource(RELEASE_DRAW);
      } else if (status == SLIDE_UP_TO_CANCEL) {
        imageView.setImageResource(recordImageIds[msg.what]);
      }
    }
  }
}
