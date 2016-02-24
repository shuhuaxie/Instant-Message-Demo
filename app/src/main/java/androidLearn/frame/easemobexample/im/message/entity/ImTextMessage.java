package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.im.message.ImMessageType;
import android.text.TextUtils;



public class ImTextMessage extends ImMessage {

  public ImTextMessage(ImMessageInternalInterface internalMessage, String orderId){
    super(internalMessage, orderId);
    setAttribute(ATTR_MSG_CONTENT, internalMessage.getText());
  }

  protected ImTextMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  @Override
  public String getText() {
    String txt = super.getText();
    if(TextUtils.isEmpty(txt)){
      txt = getContentString();
    }

    return txt;
  }

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_text;
  }
}
