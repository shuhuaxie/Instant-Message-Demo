package androidLearn.frame.easemobExample.im;

import android.content.Context;
import androidLearn.frame.easemobExample.data.entity.User;
import androidLearn.frame.easemobExample.im.connection.ImConnectListener;
import androidLearn.frame.easemobExample.im.connection.ImConnectListenerManager;
import androidLearn.frame.easemobExample.im.connection.ImConnectManager;
import androidLearn.frame.easemobExample.im.connection.ImConnectStatus;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.conversation.ImConversationListener;
import androidLearn.frame.easemobExample.im.conversation.ImConversationListenerManager;
import androidLearn.frame.easemobExample.im.conversation.ImConversationManager;
import androidLearn.frame.easemobExample.im.message.ImMessageListener;
import androidLearn.frame.easemobExample.im.message.ImMessageListenerManager;
import androidLearn.frame.easemobExample.im.message.ImMessageManager;
import androidLearn.frame.easemobExample.service.AccountManager;


import java.util.List;

public abstract class ImClient {

  public static final String DUMMY_HOST = "mingshuai";
  public static final String SERVER_BUDDY = "5605363f83cdf80d598be721";

  public static final String INTENT_IM_OPEN = "im.open";
  public static final String EXTRA_IM_OPEN_SUCCESS = "open.success";

  protected ImMessageListenerManager mMessageListenerManager = new ImMessageManager();
  protected ImConnectListenerManager mConnectListenerManager = new ImConnectManager();
  protected ImConversationListenerManager mConversationListenerManager = new ImConversationManager();

  public interface ImClientCallBack {
    void onCallBack(boolean success, String errMsg);
  }

  public interface ImClientConversationListCallBack {
    void onCallBack(boolean success, List<ImConversation> list);
  }

  /**
   * 初始化im
   */
  public abstract void init(Context context);

  /**
   * 关闭im
   */
  public abstract void close(final ImClientCallBack callBack);

  /**
   * 登录IM，多用于重连
   */
  public abstract void open(final ImClientCallBack callBack);

  /**
   * 使用指定账号密码登录IM
   */
  public abstract void open(String id, String pwd, final ImClientCallBack callBack);

  /**
   * 获取指定id的会话，没有则创建
   */
  public abstract ImConversation getConversation(String convId);

  /**
   * 获取指定id的会话，没有则创建。同时会保存该回话的联系人名称、头像和订单id，用于发送消息时候填充相应扩展字段
   */
  public abstract ImConversation getConversation(String convId, String name, String avatar, String orderId);

  /**
   * 获取本地所有会话
   */
  public abstract void getAllConversations(ImClientConversationListCallBack callback);

  /**
   * 获取所有未读消息数量
   */
  public abstract int getAllUnreadMsgCount();

  /**
   * IM是否处于连接状态，如果没登录或者网络中断，会返回false
   * 更多用于判断和网络状态相关的地方
   */
  public abstract boolean isConnected();

  /**
   * IM是否登录过，与isConnected不同，只要登录过，即使因为网络原因断线，该函数也返回true
   * 更多用于判断和登录状态相关的地方
   */
  public abstract boolean isLoggedIn();

  /**
   * 一个自定义的处理，有助于维持长连接，不一定非要实现
   */
  public abstract void ping();

  public static String getUserId() {
    User user = AccountManager.getInstance().getUser();
    if (user != null) {
      return user.imId;
    }
    return "";
  }

  public static String getUserPwd() {
    User user = AccountManager.getInstance().getUser();
    if (user != null) {
      return user.imPwd;
    }
    return "";
  }

  public static String getUserNickName() {
    User user = AccountManager.getInstance().getUser();
    if (user != null) {
      return user.name;
    }
    return "";
  }

  public ImMessageListenerManager getMessageListenerManager() {
    return mMessageListenerManager;
  }

  public void addMessageListener(ImMessageListener handler) {
    if (mMessageListenerManager != null) {
      mMessageListenerManager.addMessageHandler(handler);
    }
  }

  public void removeMessageListener(ImMessageListener handler) {
    if (mMessageListenerManager != null) {
      mMessageListenerManager.removeMessageHandler(handler);
    }
  }

  public void clearMessageListener() {
    if (mMessageListenerManager != null) {
      mMessageListenerManager.clearMessageHandler();
    }
  }

  public void addConnectListener(ImConnectListener handler) {
    if (mConnectListenerManager != null) {
      mConnectListenerManager.addConnectHandler(handler);
    }
  }

  public void removeConnectListener(ImConnectListener handler) {
    if (mConnectListenerManager != null) {
      mConnectListenerManager.removeConnectHandler(handler);
    }
  }

  public void clearConnectListener() {
    if (mConnectListenerManager != null) {
      mConnectListenerManager.clearConnectHandler();
    }
  }

  /**
   * 能够返回更加具体的连接状态，包括被踢下线的判断等
   */
  public ImConnectStatus getConnectStatus() {
    return mConnectListenerManager.getConnectStatus();
  }

  public ImConversationListenerManager getConversationListenerManager() {
    return mConversationListenerManager;
  }

  public void addConversationListener(ImConversationListener handler) {
    if (mConversationListenerManager != null) {
      mConversationListenerManager.addConversationHandler(handler);
    }
  }

  public void removeConversationListener(ImConversationListener handler) {
    if (mConversationListenerManager != null) {
      mConversationListenerManager.removeConversationHandler(handler);
    }
  }

  public void clearConversationListener() {
    if (mConversationListenerManager != null) {
      mConversationListenerManager.clearConversationHandler();
    }
  }

  public static String formatNameFromId(String id) {
    StringBuffer name = new StringBuffer("");
    name.append(id);
    if (name.length() > 6) {
      name.delete(6, name.length());
    }
    name.insert(0, "[用户]");
    return name.toString();
  }
}
