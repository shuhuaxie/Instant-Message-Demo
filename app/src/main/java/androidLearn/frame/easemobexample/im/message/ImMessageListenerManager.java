package androidLearn.frame.easemobExample.im.message;

import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;

public interface ImMessageListenerManager {
  void addMessageHandler(ImMessageListener handler);
  void removeMessageHandler(ImMessageListener handler);
  void clearMessageHandler();

  void onMessageUpdated(final ImMessage message, final ImConversation conversation);
  void onMessageReceived(ImMessage message, ImConversation conversation);
  void onMessageSent(ImMessage message, ImConversation conversation);
}
