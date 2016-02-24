package androidLearn.frame.easemobExample.im.easemob;

import androidLearn.frame.easemobExample.im.message.ImMessageStatus;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.im.message.entity.ImMessageInternalInterface;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.GsonBuilder;


public class EMMsg implements ImMessageInternalInterface {

  protected EMMessage message;

  public static EMMsg createMessage(EMMessage message) {
    switch (getType(message)) {
      case notice_image:
        return EMImageMsg.createMessage(message);
      case notice_voice:
        return EMAudioMsg.createMessage(message);
    }
    return new EMMsg(message);
  }

  protected EMMsg(EMMessage message) {
    this.message = message;
  }

  @Override
  public String getMessageId() {
    return message.getMsgId();
  }

  @Override
  public String getConversationId() {
    if (isSendMessage()) {
      return message.getTo();
    } else {
      return message.getFrom();
    }
  }

  @Override
  public String getText() {
    String text = "";
    if(message.getType() == EMMessage.Type.TXT){
      TextMessageBody txtBody = (TextMessageBody) message.getBody();
      text = txtBody.getMessage();
    }

    return text;
  }

  @Override
  public ImMessageType getType() {
    return getType(message);
  }

  protected static ImMessageType getType(EMMessage message) {
    ImMessageType type;
    switch (message.getType()) {
      case IMAGE:
        type = ImMessageType.notice_image;
        break;
      case VOICE:
        type = ImMessageType.notice_voice;
        break;
      default:
        type = ImMessageType.notice_text;
        break;
    }
    return type;
  }

  @Override
  public Object getToken() {
    return message.getMsgId();
  }

  @Override
  public String getSenderId() {
    return message.getFrom();
  }

  @Override
  public String getSendToId() {
    return message.getTo();
  }

  @Override
  public long getTime() {
    return message.getMsgTime();
  }

  @Override
  public void setRead(boolean isRead) {

  }

  @Override
  public boolean isRead() {
    return false;
  }

  @Override
  public ImMessageStatus getStatus() {
    ImMessageStatus status = ImMessageStatus.Create;
    switch (message.status) {
      case SUCCESS:
        status = ImMessageStatus.Success;
        break;
      case FAIL:
        status = ImMessageStatus.Fail;
        break;
      case INPROGRESS:
        status = ImMessageStatus.Progressing;
        break;
      case CREATE:
        status = ImMessageStatus.Create;
        break;
    }
    return status;
  }

  @Override
  public void setStatus(ImMessageStatus status) {
    switch (status) {
      case Success:
        message.status = EMMessage.Status.SUCCESS;
        break;
      case Fail:
        message.status = EMMessage.Status.FAIL;
        break;
      case Progressing:
        message.status = EMMessage.Status.INPROGRESS;
        break;
      case Create:
        message.status = EMMessage.Status.CREATE;
        break;
    }
  }

  @Override
  public boolean isSendMessage() {
    return message.direct == EMMessage.Direct.SEND;
  }

  @Override
  public Object getMessageObject() {
    return message;
  }

  @Override
  public void setMessageObject(Object msg) {
    message = (EMMessage) msg;
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (value instanceof String) {
      message.setAttribute(key, (String) value);
    } else {
      message.setAttribute(key, new GsonBuilder().disableHtmlEscaping().create().toJson(value));
    }
  }

  @Override
  public Object getAttribute(String key) {
    return message.getStringAttribute(key, null);
  }

}
