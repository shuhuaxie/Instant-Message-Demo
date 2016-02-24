package androidLearn.frame.easemobExample.im.conversation;

/**
 * Created by 明帅 on 2015/8/31.
 * IM的全局消息处理接口，全局的消息处理类需继承此接口，用来在收到消息后分配给每个注册过的ImConversationHandler
 */
public interface ImConversationListenerManager {
  void addConversationHandler(ImConversationListener handler);
  void removeConversationHandler(ImConversationListener handler);
  void clearConversationHandler();

  void onConversationCreate(ImConversation conversation);
  void onConversationListChanged();
}
