package androidLearn.frame.easemobExample.im.conversation;


import androidLearn.frame.easemobExample.im.message.entity.ImMessage;

import java.util.List;

public interface ImConversation {

  interface ImConversationGetMessagesCallBack {
    void onFinish(boolean success, List<ImMessage> list);
  }

  interface ImConversationSendMessagesCallBack {
    void onFinish(boolean success, ImMessage msg);
  }

  ImMessage createTextMessage(String msg, final ImConversationSendMessagesCallBack callBack);

  ImMessage createAudioMessage(final String filePath, int seconds, final ImConversationSendMessagesCallBack callBack);

  ImMessage createImageMessage(final String filePath, final ImConversationSendMessagesCallBack callBack);

  ImMessage createEndMessage(String text, final ImConversationSendMessagesCallBack callBack);

  void getMessages(int limit, final ImConversationGetMessagesCallBack callBack);

  void getMessages(Object token, int limit, final ImConversationGetMessagesCallBack callBack);

  String getConversationId();

  String getBuddyName();

  String getBuddyId();

  ImMessage getLastMessage();

  void setLastMessage(ImMessage message);

  void sendMessage(ImMessage message, final ImConversationSendMessagesCallBack callBack);

  void removeMessage(ImMessage message);

  void setUnReadCount(int count);

  int getUnReadCount();

  String getSearchString();

  void setName(String name);

  String getName();

  void setIcon(String icon);

  String getIcon();

  void setOrderId(String id);

  String getOrderId();

  void setExtInfo(String name, String icon, String orderId);
}
