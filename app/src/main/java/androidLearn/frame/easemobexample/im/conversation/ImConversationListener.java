package androidLearn.frame.easemobExample.im.conversation;


public interface ImConversationListener {
  int getConversationListenerPriority();
  boolean onConversationCreate(ImConversation conversation);      //创建了新的会话
  boolean onConversationListChanged();
}
