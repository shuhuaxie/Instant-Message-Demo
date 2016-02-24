package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by 明帅 on 2015/10/13.
 * *********************************病历消息数据结构示例***************************************
 * {
 * "name": "李连杰",
 * "gender": "male",
 * "age": 28,
 * "detail": "病情描述：大夫您好，我加班太累了，睡觉睡不醒的感觉，越睡越想睡，起来又很累，全身不对劲，检查又没病。",
 * "attachments": [
 * {
 * "origin": "http://api-staging.huimeibest.com/static/image/message-1.jpg",
 * "thumbnail": "http://api-staging.huimeibest.com/static/image/message-1-thumbnail.jpg"
 * },
 * {
 * "origin": "http://api-staging.huimeibest.com/static/image/message-2.jpg",
 * "thumbnail": "http://api-staging.huimeibest.com/static/image/message-2-thumbnail.jpg"
 * },
 * {
 * "origin": "http://api-staging.huimeibest.com/static/image/message-3.jpg",
 * "thumbnail": "http://api-staging.huimeibest.com/static/image/message-3-thumbnail.jpg"
 * },
 * {
 * "origin": "http://api-staging.huimeibest.com/static/image/message-4.jpg",
 * "thumbnail": "http://api-staging.huimeibest.com/static/image/message-4-thumbnail.jpg"
 * },
 * {
 * "origin": "http://api-staging.huimeibest.com/static/image/message-5.jpg",
 * "thumbnail": "http://api-staging.huimeibest.com/static/image/message-5-thumbnail.jpg"
 * }
 * ]
 * }
 */
public class ImConsultMessage extends ImMessage {

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_record;
  }

  public static class ConsultData {

    public static class Attachments {
      public String origin;
      public String thumbnail;
    }

    public String name;
    public String gender;
    public int age;
    public String detail;
    public Attachments[] attachments;
  }

  private ConsultData data;

  protected ImConsultMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  @Override
  public String getTitle() {
    return App.getInstance().getString(R.string.medical_record);
  }

  public ConsultData getConsultData() {
    if (data == null) {
      String content = getContentString();
      if(TextUtils.isEmpty(content)){
        content = getText();
      }
      if (!TextUtils.isEmpty(content)) {
        data = new Gson().fromJson(content, ConsultData.class);
      }
    }

    return data;
  }
}
