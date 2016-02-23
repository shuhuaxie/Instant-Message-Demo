package androidLearn.frame.easemobexample.im.easemob;

import androidLearn.frame.easemobexample.im.message.ImMessageType;
import androidLearn.frame.easemobexample.im.message.entity.ImAudioMessage;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import android.text.TextUtils;

import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.google.gson.Gson;

public class EMAudioMsg extends EMMsg implements ImAudioMessage.ImAudioMessageInternalInterface {

  private static class AudioData{
    public String amr;
    public String mp3;
    public double duration;
  }

  private AudioData mData;

  public static EMAudioMsg createMessage(EMMessage message){
    return new EMAudioMsg(message);
  }

  private EMAudioMsg(EMMessage message) {
    super(message);

    String content = message.getStringAttribute(ImMessage.ATTR_MSG_CONTENT, null);
    if(!TextUtils.isEmpty(content)){
      mData = new Gson().fromJson(content, AudioData.class);
      if(mData == null){  //防出错，实际不应该有空的时候
        mData = new AudioData();
      }
    }
    else{
      //兼容老版本协议
      mData = new AudioData();
      if(getType() == ImMessageType.notice_voice){
        VoiceMessageBody body = (VoiceMessageBody) message.getBody();
        mData.amr = body.getLocalUrl();
        mData.duration = body.getLength();
      }
    }
  }

  @Override
  public double getDuration(){
    return mData.duration;
  }

  @Override
  public String getAudioUri() {
    return mData.amr;
  }
}
