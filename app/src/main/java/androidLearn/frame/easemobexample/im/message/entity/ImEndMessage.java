package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import android.text.TextUtils;

public class ImEndMessage extends ImMessage {

  public ImEndMessage(ImMessageInternalInterface internalMessage, String orderId) {
    super(internalMessage, orderId);
  }

  protected ImEndMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  @Override
  public String getContentString(){
    String text = super.getContentString();
    if(TextUtils.isEmpty(text)){
      text = getText();
    }

    return text;
  }

  @Override
  public String getTitle(){
    return App.getInstance().getString(R.string.conversation_over);
  }

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_end;
  }
}
