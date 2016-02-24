package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import android.text.TextUtils;

import com.google.gson.Gson;

public class ImClinicMessage extends ImMessage {

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_clinic;
  }

  public static class ClinicData{
    public String title;
    public String content;
    public String detail;
    public String link_text;
    public String order_id;
  }

  private ClinicData data;

  protected ImClinicMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  @Override
  public String getTitle(){
    return App.getInstance().getString(R.string.appointment);
  }

  public ClinicData getClinicData(){
    if(data == null){
      String content = getContentString();
      if(TextUtils.isEmpty(content)){
        content = getText();
      }
      if(!TextUtils.isEmpty(content)){
        data = new Gson().fromJson(content, ClinicData.class);
      }
    }

    return data;
  }
}
