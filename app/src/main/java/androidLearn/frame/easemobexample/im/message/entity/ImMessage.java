package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.data.entity.User;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageStatus;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.service.AccountManager;
import androidLearn.frame.easemobExample.utils.CommonUtils;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ImMessage implements ImMessageInternalInterface {

  public static final String ATTR_NICKNAME = "nickname";
  public static final String ATTR_AVATAR = "avatar";
  public static final String ATTR_MSG_TYPE = "msg_type";
  public static final String ATTR_MSG_CONTENT = "msg_content";
  public static final String ATTR_ORDER_ID = "order_id";
  public static final String ATTR_MSG_TIME = "msg_time";

  ImMessageInternalInterface mMessage;
  protected Object tag;
  protected int progress = 0;
  protected static OkHttpClient httpClient = new OkHttpClient();

  public static ImMessage createMessage(ImMessageInternalInterface internalMessage) {

    ImMessage msg;

    switch (getTypeFromInternalMessage(internalMessage)) {
      case notice_image:
        msg = new ImImageMessage(internalMessage);
        break;
      case notice_voice:
        msg =  new ImAudioMessage(internalMessage);
        break;
      case notice_end:
        msg = new ImEndMessage(internalMessage);
        break;
      case notice_problem:
        msg = new ImProblemMessage(internalMessage);
        break;
      case notice_clinic:
        msg = new ImClinicMessage(internalMessage);
        break;
      case notice_record:
        msg = new ImConsultMessage(internalMessage);
        break;
      default:
        msg = new ImTextMessage(internalMessage);
        break;
    }

    //如果发送消息的状态是进行中，多半是之前发送过程中异常退出，那么就当失败处理
    if(msg.isSendMessage() && msg.getStatus() == ImMessageStatus.Progressing){
      if(!(msg instanceof ImFileMessage && MessageTransferManager.getInstance().isUploading(msg.getMessageId()))){
        msg.setStatus(ImMessageStatus.Fail);
      }
    }

    return  msg;
  }

  private static ImMessageType getTypeFromInternalMessage(ImMessageInternalInterface internalMessage) {
    ImMessageType type = internalMessage.getType();
    if (ImMessageType.notice_text == type) {
      String attr = (String) internalMessage.getAttribute(ATTR_MSG_TYPE);
      if (!TextUtils.isEmpty(attr)) {
        try {
          type = ImMessageType.valueOf(attr);
        } catch (IllegalArgumentException e) {
          type = ImMessageType.notice_text;
        }
      }
    }

    return type;
  }

  protected ImMessage prepareSend(String orderId) {
    User user = AccountManager.getInstance().getUser();
    if (user != null) {
      setAttribute(ATTR_NICKNAME, user.name);
      setAttribute(ATTR_AVATAR, user.avatar);
    }
    setAttribute(ATTR_ORDER_ID, orderId);
    setAttribute(ATTR_MSG_TIME, String.valueOf(getTime()));
    setAttribute(ATTR_MSG_TYPE, getType().name());
    return this;
  }

  protected ImMessage(ImMessageInternalInterface internalMessage) {
    mMessage = internalMessage;
  }

  protected ImMessage(ImMessageInternalInterface internalMessage, String orderId) {
    this(internalMessage);
    prepareSend(orderId);
  }

  @Override
  public String getMessageId() {
    return mMessage.getMessageId();
  }

  @Override
  public String getConversationId() {
    return mMessage.getConversationId();
  }

  @Override
  public String getText() {
    return mMessage.getText();
  }

//  @Override
//  public ImMessageType getType() {
//    return getRealType(mMessage);
//  }

  @Override
  public Object getToken() {
    return mMessage.getToken();
  }

  @Override
  public String getSenderId() {
    return mMessage.getSenderId();
  }

  @Override
  public String getSendToId() {
    return mMessage.getSendToId();
  }

  @Override
  public long getTime() {
    return mMessage.getTime();
  }

  @Override
  public void setRead(boolean isRead) {
    mMessage.setRead(isRead);
  }

  @Override
  public boolean isRead() {
    return mMessage.isRead();
  }

  @Override
  public ImMessageStatus getStatus() {
    return mMessage.getStatus();
  }

  @Override
  public void setStatus(ImMessageStatus status) {
    mMessage.setStatus(status);
    App.getInstance().getImClient().getMessageListenerManager().onMessageUpdated(this, App.getInstance().getImClient().getConversation(getConversationId()));
  }

  @Override
  public boolean isSendMessage() {
    return mMessage.isSendMessage();
  }

  @Override
  public Object getMessageObject() {
    return mMessage.getMessageObject();
  }

  @Override
  public void setMessageObject(Object msg) {
    mMessage.setMessageObject(msg);
  }

  @Override
  public void setAttribute(String key, Object value) {
    mMessage.setAttribute(key, value);
  }

  @Override
  public Object getAttribute(String key) {
    return mMessage.getAttribute(key);
  }

  public void setProgress(int progress) {
    if (0 <= progress && progress <= 100) {
      this.progress = progress;
    }
  }

  public int getProgress() {
    return progress;
  }

  /**
   * 用于在listview中缓存convertView，不要用于其他用途！！
   */
  public void setTag(Object tag) {
    this.tag = tag;
  }

  /**
   * 获取缓存的convertView，不要用于其他用途！！
   */
  public Object getTag() {
    return tag;
  }

  public String getSenderAvatar() {
    if (isSendMessage()) {
      User user = AccountManager.getInstance().getUser();
      if (user != null) {
        return user.avatar;
      }
    } else {
      return (String) getAttribute(ImMessage.ATTR_AVATAR);
    }

    return null;
  }

  public String getOrderId() {
    return (String) getAttribute(ImMessage.ATTR_ORDER_ID);
  }

  public String getSenderName() {
    String nick = (String) getAttribute(ImMessage.ATTR_NICKNAME);
    if (!TextUtils.isEmpty(nick)) {
      return nick;
    }

//    PatientInfo patient = PatientManager.getInstance().getPatient(getSenderId());
//    if (patient != null) {
//      return patient.name;
//    } else {
      return ImClient.formatNameFromId(getSenderId());
//    }
  }

  public String getTitle() {
    return getText();
  }

  public String getContentString() {
    return (String) getAttribute(ATTR_MSG_CONTENT);
  }

  public void prepareSend(ImConversation.ImConversationSendMessagesCallBack callBack){
    if(callBack != null){
      callBack.onFinish(true, null);
    }
  }

  public void onSendFinish(boolean success){
    setStatus(success ? ImMessageStatus.Success: ImMessageStatus.Fail);
    if(success){
      sendPush();
    }
  }

  public void onMessageReceived(){

  }

  public void onMessageRemoved(){

  }

  //如果患者不在线，则给患者的公众号发送消息通知有医生回复。医生端需要发送每条消息之后都请求这个接口
  private void sendPush() {
    String url = null;

    if(CommonUtils.isDebugMode()){
      url = "http://h5test.huimeibest.com/Callback/anwser";
    }
    else{
      url = "http://h5.huimeibest.com/Callback/anwser";
    }
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Map<String, String> b = new HashMap<>();
    b.put("order_id", getOrderId());
    b.put("msg_id", getMessageId());
    if(getType() == ImMessageType.notice_end){
      b.put("msg_content", App.getInstance().getString(R.string.im_conversation_end_push));
    }
    else{
      b.put("msg_content", getTitle());
    }

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    String json = gson.toJson(b);

    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Request request, IOException e) {

      }

      @Override
      public void onResponse(Response response) throws IOException {

      }
    });
  }
}
