package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.data.response.BaseResponse;
import androidLearn.frame.easemobExample.im.easemob.EMAudioMsg;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.utils.PathUtils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImAudioMessage extends ImFileMessage {

  public interface ImAudioMessageInternalInterface {
    /**
     * 获取语音消息的时长
     */
    double getDuration();

    /**
     * 获取语音消息本地文件对应的URI
     */
    String getAudioUri();
  }

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_voice;
  }

  private static class AudioData {
    public String url;
    //    public String url_mp3;
    public int duration;
  }

  private AudioData mData;

  protected ImAudioMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);

    if (internalMessage instanceof EMAudioMsg) {
      //兼容旧环信消息
      mData = new AudioData();
      try {
        ImAudioMessageInternalInterface audioMessge = (ImAudioMessageInternalInterface) mMessage;
        mData.url = audioMessge.getAudioUri();
        mData.duration = (int) audioMessge.getDuration();
        moveCacheFile(mData.url);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    String content = getContentString();
    if (!TextUtils.isEmpty(content)) {
      mData = new Gson().fromJson(content, AudioData.class);
    }
    if (mData != null && !TextUtils.isEmpty(mData.url)) {
      final String localFileUri = getDownloadLocalFilePath();
      File downFile = new File(localFileUri);
      if (downFile == null || !downFile.exists()) {
        downloadFile();
      }
    }

    if (mData == null) {
      mData = new AudioData();
      //如果是发送消息，尝试恢复内容
      if (isSendMessage()) {
        //设置为本地文件地址
        mData.url = getUploadLocalFilePath();
        //尝试从数据库中读取音频长度
//        MessageData data = MessageData.getMessageData(internalMessage.getMessageId());
//        if(data != null){
//          mData.duration = Integer.parseInt(data.ext);
//        }
//        else{ //如果数据库不存在，尝试直接分析文件内容取得长度
        File file = new File(getUploadLocalFilePath());
        if (file != null && file.exists()) {
          mData.duration = getAmrDuration(file);
//          }
        }
      }
    }
  }

  @Override
  public String getUploadFileExtensionName() {
    return ".amr";
  }

  public ImAudioMessage(ImMessageInternalInterface internalMessage, String orderId, String path,
                        double duration) {
    super(internalMessage, orderId, path);
    mData = new AudioData();
    mData.duration = (int) duration;
    moveCacheFile(path);
    saveTempSendData(path, String.valueOf(mData.duration));
  }


  public double getDuration() {
    return mData.duration;
  }

  public String getAudioUri() {
    File file = new File (getUploadLocalFilePath());
    if (file.exists())
    return getUploadLocalFilePath();
    return getDownloadLocalFilePath();
  }

  @Override
  public String getTitle() {
    return App.getInstance().getString(R.string.voice);
  }

  @Override
  protected String onUploadSuccess(String uploadAddress) {

    String err = "upload file error. response:" + uploadAddress;
    try {
      if (!TextUtils.isEmpty(uploadAddress)) {
//        AudioUploadResponse r = new Gson().fromJson(uploadAddress, AudioUploadResponse.class);
//        if (r.status == 0 && r.data != null && r.data.attachment != null) {
        AudioData data = new AudioData();
        data.url = uploadAddress;
//          data.url_mp3 = r.data.attachment.url_mp3;
        data.duration = mData.duration;
        setAttribute(ImMessage.ATTR_MSG_CONTENT, new GsonBuilder().disableHtmlEscaping().create().toJson(data));
        super.onUploadSuccess(uploadAddress);
        return null;
//        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    onUploadFail(err);
    return err;
  }

  @Override
  protected String getDownloadUrl() {
    return mData.url;
  }

  @Override
  protected String getUploadLocalFilePath() {
    String cachePath = PathUtils.getAudioPathByMessageId(getConversationId(), "upload_" + getMessageId());
    if ((TextUtils.isEmpty(cachePath) || !new File(cachePath).exists()) && isSendMessage()) {
//      MessageData data = getMessageData();
//      if (data != null) {
//        cachePath = data.local;
//      }
    }

    return cachePath;
  }

  @Override
  protected String getDownloadLocalFilePath() {
    return PathUtils.getAudioPathByMessageId(getConversationId(), "download_" + getMessageId());
  }

  @Override
  public void onMessageReceived() {
    downloadFile();
  }

  private static class AudioUploadResponseData {
    public Attachment attachment;
  }

  private static class Attachment {
    public String url;
//    public String url_mp3;
  }

  private static class AudioUploadResponse extends BaseResponse {
    public AudioUploadResponseData data;
  }

  private static int getAmrDuration(File file) {
    long duration = -1;
    int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
        0, 0};
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = new RandomAccessFile(file, "rw");
      long length = file.length();// 文件的长度
      int pos = 6;// 设置初始位置
      int frameCount = 0;// 初始帧数
      int packedPos = -1;

      byte[] datas = new byte[1];// 初始数据值
      while (pos <= length) {
        randomAccessFile.seek(pos);
        if (randomAccessFile.read(datas, 0, 1) != 1) {
          duration = length > 0 ? ((length - 6) / 650) : 0;
          break;
        }
        packedPos = (datas[0] >> 3) & 0x0F;
        pos += packedSize[packedPos] + 1;
        frameCount++;
      }

      duration += frameCount * 20;// 帧数*20
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (randomAccessFile != null) {
        try {
          randomAccessFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (duration < 0) {
      return 0;
    }
    return (int) ((duration / 1000) + 1);
  }
}
