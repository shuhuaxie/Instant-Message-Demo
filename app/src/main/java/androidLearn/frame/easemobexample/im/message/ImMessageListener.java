package androidLearn.frame.easemobExample.im.message;


import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;

public interface ImMessageListener {
  int getMessageListenerPriority();

  boolean onMessageSent(ImMessage message, ImConversation conversation);        //发送了一条消息（不管成功与否）
  boolean onMessageReceived(ImMessage message, ImConversation conversation);    //收到对方发送的消息
  void onMessageReceipt(ImMessage message, ImConversation conversation);        //自己发送的消息被对方成功接收
  boolean onMessageUpdated(ImMessage message, ImConversation conversation);    //需要进行传输的消息如图片语音等传输完成，用于更新状态
}
