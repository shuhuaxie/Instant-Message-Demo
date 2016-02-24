package androidLearn.frame.easemobExample.im.easemob;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.data.entity.Badge;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.entity.ImAudioMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImEndMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImImageMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImTextMessage;
import androidLearn.frame.easemobExample.utils.BitmapUtils;
import androidLearn.frame.easemobExample.utils.PinYin;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EMConv implements ImConversation {

  private EMConversation mConv;
  private final StringBuffer mSearchStr = new StringBuffer();

  public EMConv(EMConversation conversation) {
    mConv = conversation;
  }

  public void setConv(EMConversation conversation) {
    mConv = conversation;
  }

  private static class Attr {
    String name;
    String icon;
    String orderId;

    public Attr() {
      name = "";
      icon = "";
      orderId = "";
    }
  }

  @Override
  public ImMessage createTextMessage(final String msg, final ImConversationSendMessagesCallBack callBack) {
    //创建一条文本消息
    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
    //设置消息body
    TextMessageBody txtBody = new TextMessageBody(msg);
    message.addBody(txtBody);
    //设置接收人
    message.setReceipt(mConv.getUserName());
    final ImTextMessage m = new ImTextMessage(EMMsg.createMessage(message), getOrderId());
    saveMessageToDatebase(message);
    mConv.addMessage(message);
    EMClient.getInstance().getMessageListenerManager().onMessageSent(m, this);
    return m;
  }

  @Override
  public ImMessage createAudioMessage(String filePath, int seconds, final ImConversationSendMessagesCallBack callBack) {
//    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
//    File file = new File(filePath);
//    VoiceMessageBody body = new VoiceMessageBody(file, seconds);
//    message.addBody(body);
//    message.setReceipt(mConv.getUserName());

    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
    //设置消息body
    TextMessageBody txtBody = new TextMessageBody(App.getInstance().getString(R.string.voice));
    message.addBody(txtBody);
    //设置接收人
    message.setReceipt(mConv.getUserName());

    final ImAudioMessage msg = new ImAudioMessage(EMMsg.createMessage(message), getOrderId(), filePath, seconds);
    //创建录音文件时文件名是用时间生成的，生成ImAudioMessage对象后文件名会改成messageid，所以VoiceMessageBody里也要跟着改一下
//    ((VoiceMessageBody)message.getBody()).setLocalUrl(msg.getAudioUri());
    saveMessageToDatebase(message);
    mConv.addMessage(message);
    EMClient.getInstance().getMessageListenerManager().onMessageSent(msg, this);
    return msg;
  }

  @Override
  public ImMessage createImageMessage(String filePath, final ImConversationSendMessagesCallBack callBack) {
//    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
//
//    ImageMessageBody body = new ImageMessageBody(new File(filePath));
//    // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
//    // body.setSendOriginalImage(true);
//    message.addBody(body);
//    message.setReceipt(mConv.getUserName());

    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
    //设置消息body
    TextMessageBody txtBody = new TextMessageBody(App.getInstance().getString(R.string.picture));
    message.addBody(txtBody);
    //设置接收人
    message.setReceipt(mConv.getUserName());

    final ImImageMessage msg = new ImImageMessage(EMMsg.createMessage(message), getOrderId(), filePath);
    saveMessageToDatebase(message);
    mConv.addMessage(message);
    EMClient.getInstance().getMessageListenerManager().onMessageSent(msg, this);
    return msg;
  }

  @Override
  public ImMessage createEndMessage(String text, ImConversationSendMessagesCallBack callBack) {
    //创建一条文本消息
    final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
    //设置消息body
    TextMessageBody txtBody = new TextMessageBody(text);
    message.addBody(txtBody);
    //设置接收人
    message.setReceipt(mConv.getUserName());
    final ImEndMessage m = new ImEndMessage(EMMsg.createMessage(message), getOrderId());
    saveMessageToDatebase(message);
    mConv.addMessage(message);
    EMClient.getInstance().getMessageListenerManager().onMessageSent(m, this);
    return m;
  }

  @Override
  public void getMessages(int limit, ImConversationGetMessagesCallBack callBack) {
    List<EMMessage> list = mConv.getAllMessages();
    if (callBack != null) {
      List<ImMessage> l = new ArrayList<>();
      if (list != null) {
        for (EMMessage msg :
            list) {
          ImMessage m = ImMessage.createMessage(EMMsg.createMessage(msg));
          l.add(m);
        }
      }
      callBack.onFinish(true, l);
    }
  }

  @Override
  public void getMessages(Object token, int limit, ImConversationGetMessagesCallBack callBack) {
    List<EMMessage> list = mConv.loadMoreMsgFromDB((String) token, limit);
    if (callBack != null) {
      List<ImMessage> l = new ArrayList<>();
      if (list != null) {
        for (EMMessage msg :
            list) {
          ImMessage m = ImMessage.createMessage(EMMsg.createMessage(msg));
          l.add(m);
        }
      }
      callBack.onFinish(true, l);
    }
  }

  @Override
  public String getConversationId() {
    return mConv.getUserName();
  }

  @Override
  public String getBuddyName() {
//    PatientInfo patient = PatientManager.getInstance().getPatient(mConv.getUserName());
//    if (patient != null) {
//      return patient.name;
//    } else {
      return ImClient.formatNameFromId(getConversationId());
//    }
  }

  @Override
  public String getBuddyId() {
    return mConv.getUserName();
  }

  @Override
  public ImMessage getLastMessage() {
    EMMessage msg = mConv.getLastMessage();
    if (msg != null) {
      return ImMessage.createMessage(EMMsg.createMessage(msg));
    }
    return null;
  }

  @Override
  public void setLastMessage(ImMessage message) {

  }

  private void internalSendMessage(final ImMessage message, final ImConversationSendMessagesCallBack callBack){
//    message.prepareSend();
    EMMessage msg = (EMMessage) message.getMessageObject();
    EMChatManager.getInstance().sendMessage(msg, new EMCallBack() {

      @Override
      public void onSuccess() {
        message.onSendFinish(true);
        if (callBack != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              callBack.onFinish(true, message);
            }
          });
        }
      }

      @Override
      public void onError(int code, String error) {
        message.onSendFinish(false);
        if (callBack != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              callBack.onFinish(false, message);
            }
          });
        }
      }

      @Override
      public void onProgress(int progress, String status) {
      }

    });
  }

  @Override
  public void sendMessage(final ImMessage message, final ImConversationSendMessagesCallBack callBack) {

//    if(message instanceof ImFileMessage){
//      if(TextUtils.isEmpty(message.getContentString())){  //没有content说明上传还没有成功，需要先上传文件
//        ((ImFileMessage)message).uploadFile(new ImFileMessage.uploadFileCallback() {
//          @Override
//          public void onUploadFinish(final boolean success, String err) {
//            if(success){
//              internalSendMessage(message, callBack);
//            }
//            else{
//              if(callBack != null){
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                  @Override
//                  public void run() {
//                    callBack.onFinish(success, message);
//                  }
//                });
//              }
//            }
//          }
//        });
//        return;
//      }
//    }

    message.prepareSend(new ImConversationSendMessagesCallBack() {

      @Override
      public void onFinish(boolean success, ImMessage msg) {
        if (success) {
          saveMessageToDatebase((EMMessage) message.getMessageObject());
          internalSendMessage(message, callBack);
        }
        else{
          if(callBack != null){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                callBack.onFinish(false, message);
              }
            });
          }
        }
      }
    });


  }

  @Override
  public void removeMessage(ImMessage message) {
    message.onMessageRemoved();
    mConv.removeMessage(message.getMessageId());
  }

  @Override
  public void setUnReadCount(int count) {
    if (0 == count) {
      mConv.markAllMessagesAsRead();
    }
    Badge.updateBadgeCount(Badge.BadgeType.BADGE_TYPE_IM_MESSAGE, count);
  }

  @Override
  public int getUnReadCount() {
    return mConv.getUnreadMsgCount();
  }

  @Override
  public String getSearchString() {
    if (mSearchStr.length() == 0) {
      MakeSearchString();
    }
    return mSearchStr.toString();
  }

  private Attr getAttr() {
    String attr = mConv.getExtField();
    if (!TextUtils.isEmpty(attr)) {
      return new Gson().fromJson(attr, Attr.class);
    }

    return new Attr();
  }

  @Override
  public void setName(String name) {
    synchronized (this) {
      Attr attr = getAttr();
      attr.name = name;
      mConv.setExtField(new Gson().toJson(attr));
    }
  }

  @Override
  public String getName() {
    synchronized (this) {
      Attr attr = getAttr();
      if (attr != null && !TextUtils.isEmpty(attr.name)) {
        return attr.name;
      }
      return getBuddyName();
    }
  }

  @Override
  public void setIcon(String icon) {
    synchronized (this) {
      Attr attr = getAttr();
      attr.icon = icon;
      mConv.setExtField(new Gson().toJson(attr));
    }
  }

  @Override
  public String getIcon() {
    synchronized (this) {
      Attr attr = getAttr();
      if (attr != null && !TextUtils.isEmpty(attr.icon)) {
        return attr.icon;
      }
      return BitmapUtils.DEFAULT_AVATAR_URI;
    }
  }

  @Override
  public void setOrderId(String id) {
    synchronized (this){
      if (!TextUtils.isEmpty(id)) {
        Attr attr = getAttr();
        attr.orderId = id;
        mConv.setExtField(new Gson().toJson(attr));
      }
    }
  }

  @Override
  public String getOrderId() {
    synchronized (this) {
      Attr attr = getAttr();
      if (attr != null && !TextUtils.isEmpty(attr.orderId)) {
        return attr.orderId;
      }
      return "";
    }
  }

  @Override
  public void setExtInfo(String name, String icon, String orderId) {
    synchronized (this) {
      Attr attr = getAttr();
      if (attr != null) {
        attr.name = name;
        attr.icon = icon;
        attr.orderId = orderId;
        mConv.setExtField(new Gson().toJson(attr));
      }
    }
  }

//  private void setMessageAttribute(EMMessage message) {
//    User user = AccountManager.getInstance().getUser();
//    if (user != null) {
//      message.setAttribute(EMMsg.ATTR_NICKNAME, user.name);
//      message.setAttribute(EMMsg.ATTR_AVATAR, user.avatar);
//    }
//    String orderid = getOrderId();
//    message.setAttribute(EMMsg.ATTR_ORDER_ID, orderid);
//    message.setAttribute(EMMsg.ATTR_MSG_TIME, String.valueOf(message.getMsgTime()));
//  }

  private void MakeSearchString() {
    mSearchStr.delete(0, mSearchStr.length());

    String nick = getName();
    mSearchStr.append(nick);
    mSearchStr.append("\n");

    List<String> list = PinYin.getPinYin(nick);
    if (list != null) {
      for (String item : list) {
        mSearchStr.append(item);
        mSearchStr.append("\n");
      }
    }
  }

  public void saveMessageToDatebase(EMMessage msg){
    EMChatManager.getInstance().saveMessage(msg);
    EMChatManager.getInstance().updateMessageBody(msg);
//    try {
//      Class clazz = Class.forName("com.easemob.chat.EMConversationManager");
//      Method getInstance = clazz.getMethod("getInstance");
//      getInstance.setAccessible(true);
//      Object instance = getInstance.invoke(clazz);
//      Method saveMessage = instance.getClass().getMethod("saveMessage", EMMessage.class);
//      saveMessage.setAccessible(true);
//      saveMessage.invoke(instance, msg);
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    } catch (NoSuchMethodException e) {
//      e.printStackTrace();
//    } catch (InvocationTargetException e) {
//      e.printStackTrace();
//    } catch (IllegalAccessException e) {
//      e.printStackTrace();
//    } catch(Exception e){
//      e.printStackTrace();
//    }
  }
}
