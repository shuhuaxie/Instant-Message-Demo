package androidLearn.frame.easemobExample.im.easemob;

import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.conversation.ImConversationListenerManager;
import androidLearn.frame.easemobExample.im.message.ImMessageListenerManager;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;

public class EMMsgManager implements EMEventListener {

  private ImMessageListenerManager IMLM;
  private ImConversationListenerManager ICLM;

  public EMMsgManager(ImMessageListenerManager msgManager, ImConversationListenerManager convManager){
    IMLM = msgManager;
    ICLM = convManager;
  }

  @Override
  public void onEvent(EMNotifierEvent event) {


    switch (event.getEvent()) {
      case EventNewMessage:
      case EventOfflineMessage: {
        EMMessage message = (EMMessage) event.getData();
        EMConversation conversation = EMChatManager.getInstance().getConversation(message.getUserName());
        conversation.addMessage(message);

        final ImMessage msg = ImMessage.createMessage(EMMsg.createMessage(message));
        final ImConversation conv = EMClient.getInstance().getConversation(conversation.getUserName(), msg);

        if (message.getType() == EMMessage.Type.VOICE || message.getType() == EMMessage.Type.IMAGE) {
          if (message.status == EMMessage.Status.INPROGRESS) {
            ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {
              @Override
              public void onSuccess() {
                IMLM.onMessageUpdated(msg, conv);
              }

              @Override
              public void onError(int i, String s) {
                IMLM.onMessageUpdated(msg, conv);
              }

              @Override
              public void onProgress(int i, String s) {
                msg.setProgress(i);
                IMLM.onMessageUpdated(msg, conv);
              }
            });
          }
        }

        msg.onMessageReceived();
        IMLM.onMessageReceived(msg, conv);
      }

      break;
      case EventDeliveryAck: {
//        final EMMessage message = (EMMessage) event.getData();
//        final EMConversation conversation = EMChatManager.getInstance().getConversation(message.getUserName());
//
//        if (message != null && conversation != null) {
//          message.isDelivered = true;
//          ImMessage m = new EMMsg(message);
//          ImConversation conv = EMClient.getInstance().getConversation(conversation.getUserName(), m);
//          onMessageRecipt(m, conv);
//        }
      }

      break;

      case EventConversationListChanged: {
        ICLM.onConversationListChanged();
      }
      break;
    }
  }
}
