package androidLearn.frame.easemobexample.im.easemob;

import android.content.Context;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.ImClient;
import androidLearn.frame.easemobexample.im.connection.ImConnectListener;
import androidLearn.frame.easemobexample.im.connection.ImConnectStatus;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.ImMessageListener;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.service.AccountManager;
import androidLearn.frame.easemobexample.utils.UiUtils;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EMClient extends ImClient implements ImConnectListener,ImMessageListener {

  private static EMClient mClient;
  private Context mContext;
  private final Map<String, EMConv>  mConversationList = Collections.synchronizedMap(new HashMap<String, EMConv>());
  private boolean mConnected = false;
  private boolean mIsReceiverRegisted = false;
  private final AtomicBoolean isConvLoadedFromDb = new AtomicBoolean(false);
  private EMMsgManager mMsgManager;
  private EMConnectManager mConnectManager;

  private EMClient() {

  }

  public static EMClient getInstance() {
    if (mClient == null) {
      mClient = new EMClient();
    }

    return mClient;
  }

  @Override
  public void init(Context context) {
    this.mContext = context;

    //以下部分是环信SDK相关初始化内容
    EMChat.getInstance().setEnv(EMChatConfig.EMEnvMode.EMProductMode);  //生产模式
    EMChat.getInstance().setAutoLogin(false);
    EMChat.getInstance().init(context);
    EMChatManager.getInstance().getChatOptions().setShowNotificationInBackgroud(false);
    EMChatManager.getInstance().getChatOptions().setNotifyBySoundAndVibrate(false);
    EMChatManager.getInstance().getChatOptions().setRequireDeliveryAck(true);   //要求送达回执

    //全局连接状态管理类
    mConnectManager = new EMConnectManager(mConnectListenerManager);
    //全局消息管理类
    mMsgManager = new EMMsgManager(mMessageListenerManager, mConversationListenerManager);
  }

  @Override
  public void close(final ImClientCallBack callBack) {
    try {
      isConvLoadedFromDb.set(false);
      mConversationList.clear();
      mConnected = false;
      clearConversationListener();
      unRegisterConnectHandler();
      unRegisterMessageHandler();
      //此方法为同步方法
      EMChatManager.getInstance().logout();
      if (callBack != null) {
        callBack.onCallBack(true, null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (callBack != null) {
        callBack.onCallBack(false, e.getMessage());
      }
    }
  }

  @Override
  public void open(final ImClientCallBack callBack) {

    String id = getUserId();
    String pwd = getUserPwd();
    if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pwd)) {  //这种情况就是本地保存的IM账户信息是空，应该是被踢下线的情况
      if (callBack != null) {
        callBack.onCallBack(false, "账号信息有误");
      }
//      if(AccountManager.getInstance().isLogin()) {
//        AccountManager.getInstance().logout(null, false);
//        UiUtils.showKickedNotification(mContext);
//      }
      return;
    }

    open(id, pwd, callBack);
  }

  @Override
  public synchronized void open(String id, String pwd, final ImClientCallBack callBack) {
    if(isConnected()){
      if(callBack != null){
        callBack.onCallBack(true, null);
      }
      return;
    }

    EMChatManager.getInstance().login(id, pwd, new EMCallBack() {//回调
      @Override
      public void onSuccess() {
        mConnected = true;
        //开启连接状态监听
        registerConnectHandler();

        //遇到一次调用后环信sdk堆栈溢出，应该是在网络设置了代理但是代理失效后产生的，太tm受不了了
        EMChatManager.getInstance().updateCurrentUserNick(getUserNickName());

        EMChatManager.getInstance().getChatOptions().setNumberOfMessagesLoaded(10);
        EMChatManager.getInstance().loadAllConversations();
        isConvLoadedFromDb.set(true);


        //登录成功后加载一次会话列表
        queryConversation(null, new ImClientConversationListCallBack() {

          @Override
          public void onCallBack(boolean success, List<ImConversation> list) {

            if (callBack != null) {
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                  callBack.onCallBack(true, null);
                  registerMessageHandler();   //如果有回调，在回调完成后再开启接收消息的处理，避免UI上会话列表初始化时收到新消息导致会话列表的混乱
                }
              });
            } else {
              //开启消息接收
              registerMessageHandler();
            }
            getConversationListenerManager().onConversationListChanged();
          }
        });


      }

      @Override
      public void onProgress(int progress, String status) {

      }

      @Override
      public void onError(final int code, final String message) {
        mConnected = false;
        new Handler(Looper.getMainLooper()).post(new Runnable() {

          @Override
          public void run() {
            if (callBack != null) {
              String err = mContext.getString(R.string.im_login_fail);
              if (code == EMError.INVALID_PASSWORD_USERNAME) {
                err = mContext.getString(R.string.im_login_err_invalid_user);
              }
              callBack.onCallBack(false, err);
            }
          }
        });

      }
    });
  }

  @Override
  public ImConversation getConversation(String convId) {
    if (!TextUtils.isEmpty(convId)) {
      EMConv conv = mConversationList.get(convId);
      if (conv == null) {
        conv = new EMConv(EMChatManager.getInstance().getConversation(convId));
        mConversationList.put(convId, conv);
      }
      return conv;
    }
    return null;
  }

  @Override
  public ImConversation getConversation(String convId, String name, String avatar, String orderId) {
    if (!TextUtils.isEmpty(convId)) {
      EMConv conv = mConversationList.get(convId);
      if (conv == null) {
        EMConversation emConv = EMChatManager.getInstance().getConversation(convId);
        conv = new EMConv(emConv);

        //使用EMChatManager.getInstance().getConversation创建新会话时，环信不会把会话插入conversation_list数据库
        //这会导致调用会话的setExtInfo失败，因为环信只做了数据库的update处理而没有insert
        //所以先手动插入一条消息，这样环信就会往conversation_list数据库里插入会话，之后我们再调用会话的setExtInfo，最后再删除这条插入的消息
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        cmdMsg.setReceipt(convId);
        EMChatManager.getInstance().saveMessage(cmdMsg);  //临时插入一条消息
        conv.setExtInfo(name, avatar, orderId);           //保存内容到会话扩展字段，这个操作十分重要，因为发消息时需要读取这里的内容
        emConv.removeMessage(cmdMsg.getMsgId());          //删除插入的消息

        mConversationList.put(convId, conv);
        mConversationListenerManager.onConversationCreate(conv);
      } else {
        conv.setExtInfo(name, avatar, orderId);
      }
      return conv;
    }
    return null;
  }


  ImConversation getConversation(String convId, ImMessage message) {
    return getConversation(convId, message.getSenderName(), message.getSenderAvatar(), message.getOrderId());
  }

  /*
  * 保证只进行一次queryClientIds = null的调用，多次进行这种调用可能会产生异常问题
   */
  private synchronized void queryConversation(List<String> queryClientIds, ImClientConversationListCallBack callback) {
    if (isLoggedIn() && !isConvLoadedFromDb.get()) { //必须要先登陆成功，否则调用loadAllConversations会报异常
      EMChatManager.getInstance().loadAllConversations();
      isConvLoadedFromDb.set(true);
    }
    List<ImConversation> list = new ArrayList<>();
    if (queryClientIds == null) {
      final Hashtable<String, EMConversation> table = EMChatManager.getInstance().getAllConversations();
      for (Map.Entry<String, EMConversation> e : table.entrySet()) {
        EMConversation emConv = e.getValue();
        String id = emConv.getUserName();
        EMConv conv;
        if (!mConversationList.containsKey(id)) {
          conv = new EMConv(emConv);
          ImMessage message = conv.getLastMessage();
          if (message != null && !message.isSendMessage()) {
            conv.setExtInfo(message.getSenderName(), message.getSenderAvatar(), message.getOrderId());
          }
        } else {
          conv = mConversationList.get(id);
          conv.setConv(emConv);
        }
        mConversationList.put(id, conv);
        list.add(conv);
      }
    } else {
      for (String buddyid :
          queryClientIds) {
        ImConversation conv = getConversation(buddyid);
        if (conv != null) {
          list.add(conv);
        }
      }
    }

    if (callback != null) {
      callback.onCallBack(true, list);
    }
  }

  @Override
  public void getAllConversations(ImClientConversationListCallBack callback) {
    List<ImConversation> list = new ArrayList<>();
    if (list.size() == 0) {
      queryConversation(null, null);
    }
    synchronized (mConversationList) {
      for (Map.Entry<String, EMConv> e : mConversationList.entrySet()) {
        EMConv emConv = e.getValue();
        list.add(emConv);
      }
    }

    if(callback != null){
      callback.onCallBack(true, list);
    }
  }

  private void registerMessageHandler() {

    //注册收到消息监听 及 消息送达监听
    if (!mIsReceiverRegisted) {
      mIsReceiverRegisted = true;
      addMessageListener(this);
      EMChatManager.getInstance().registerEventListener(mMsgManager);
      EMChat.getInstance().setAppInited();
    }
  }

  private void unRegisterMessageHandler() {
    if (mIsReceiverRegisted) {
      mIsReceiverRegisted = false;
      clearMessageListener();
      EMChatManager.getInstance().unregisterEventListener(mMsgManager);
    }
  }

  private void registerConnectHandler(){
    //注册EMClient自己的网络监听处理
    addConnectListener(this);

    EMChatManager.getInstance().removeConnectionListener(mConnectManager);  //环信sdk有bug，addConnectionListener有可能重复添加对象
    EMChatManager.getInstance().addConnectionListener(mConnectManager);
  }

  private void unRegisterConnectHandler(){
    clearConnectListener();
    EMChatManager.getInstance().removeConnectionListener(mConnectManager);
  }

  @Override
  public boolean isConnected() {
    return EMChatManager.getInstance().isConnected();
  }

  @Override
  public int getAllUnreadMsgCount() {
    return EMChatManager.getInstance().getUnreadMsgsCount();
  }

  @Override
  public void ping() {
    EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

    String action = "ping";//action可以自定义，在广播接收时可以收到
    CmdMessageBody cmdBody = new CmdMessageBody(action);
    String toUsername = SERVER_BUDDY;//发送给某个人
    cmdMsg.setReceipt(toUsername);
    cmdMsg.addBody(cmdBody);
    EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
      @Override
      public void onSuccess() {

      }

      @Override
      public void onError(int i, String s) {

      }

      @Override
      public void onProgress(int i, String s) {

      }
    });
  }

  @Override
  public boolean isLoggedIn() {
    return EMChat.getInstance().isLoggedIn();
  }

  private EMChatManager getEMChatManager() {
    return EMChatManager.getInstance();
  }

  @Override
  public int getConnectListenerPriority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean onImConnectStatusChanged(boolean connected, ImConnectStatus status) {
    if (connected) {
      if (!mConnected) {
//          UiUtils.showToast(mContext, "图文咨询功能恢复");
      }
    } else {
      if (ImConnectStatus.ACCOUNT_KICKED == status || ImConnectStatus.ACCOUNT_ERROR == status) {
        if (AccountManager.getInstance().isLogin()) {
          AccountManager.getInstance().logout(null, false);
          UiUtils.showKickedNotification(mContext);
        }
      } else if (ImConnectStatus.NETWORK_DISCONNECT == status && mConnected) {
        UiUtils.showToast(mContext, mContext.getString(R.string.im_disconnect));
      }
    }

    mConnected = connected;

    return true;
  }

  @Override
  public int getMessageListenerPriority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean onMessageSent(ImMessage message, ImConversation conversation) {
    return false;
  }

  @Override
  public boolean onMessageReceived(ImMessage message, ImConversation conversation) {
    if (message != null) {
      UiUtils.showNewMessageNotification(mContext, message);
    }
    return true;
  }

  @Override
  public void onMessageReceipt(ImMessage message, ImConversation conversation) {

  }

  @Override
  public boolean onMessageUpdated(ImMessage message, ImConversation conversation) {
    return false;
  }
}
