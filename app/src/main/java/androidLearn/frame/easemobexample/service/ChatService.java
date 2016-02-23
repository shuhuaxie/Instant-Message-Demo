package androidLearn.frame.easemobexample.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class ChatService extends Service
//    implements ImMessageListener
{

  public static void startChatService(Context context) {
    try {
      Intent intent = new Intent(context, ChatService.class);
      context.startService(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void stopChatService(Context context) {
    try {
      Intent intent = new Intent(context, ChatService.class);
      context.stopService(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ChatBinder mBinder = new ChatBinder();
//  private PendingIntent pendingIntent;
//  private static final int ALARM_CODE = 123;
//  private static final String INTENT_TEST_CONNECT = "huimei_test_connect";
//  private volatile boolean hasStarted = false;
//  private static int SCHEDULE_TIME = 60 * 1000 * 5;   //定时任务，根据[连接/登录]状态不同进行不同处理

//  @Override
//  public int getMessageListenerPriority() {
//    return Integer.MAX_VALUE - 1;
//  }
//
//  @Override
//  public boolean onMessageSent(ImMessage message, ImConversation conversation) {
//    return false;
//  }
//
//  @Override
//  public boolean onMessageReceived(ImMessage message, ImConversation conversation) {
//    if (message != null) {
//      UiUtils.showNewMessageNotification(this, message);
//    }
//    return true;
//  }
//
//  @Override
//  public void onMessageReceipt(ImMessage message, ImConversation conversation) {
//
//  }
//
//  @Override
//  public boolean onMessageUpdated(ImMessage message, ImConversation conversation) {
//    return false;
//  }
//
  public class ChatBinder extends Binder {
    public ChatService getService() {
      return ChatService.this;
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    start();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
//    if (intent == null) {
//      //service restart;
//    } else {
//
//    }
//
//    if (!hasStarted) {    //防止意外终止，重启动
//      start();
//    }

    return START_STICKY;
  }

  public void onDestroy() {
    super.onDestroy();
    stop();
  }

  public void start() {

//    registerReceiver(mReceiver, new IntentFilter(INTENT_TEST_CONNECT));
//    pendingIntent = PendingIntent.getBroadcast(this, ALARM_CODE, new Intent(
//        INTENT_TEST_CONNECT), PendingIntent.FLAG_UPDATE_CURRENT);
//
//    schedule(SCHEDULE_TIME);
//    hasStarted = true;
  }

  private void stop() {
//    // Cancel Alarm.
//    AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
//    alarmManager.cancel(pendingIntent);
//
//    if (hasStarted) {
//      hasStarted = false;
//      try {
//        unregisterReceiver(mReceiver);
//      } catch (IllegalArgumentException e) {
//        //Ignore unregister errors.
//      }
//    }
  }

  public void schedule(long delayInMilliseconds) {
//    long nextAlarmInMilliseconds = System.currentTimeMillis()
//        + delayInMilliseconds;
//    AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
//    alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds,
//        pendingIntent);
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
//      if (INTENT_TEST_CONNECT.equals(intent.getAction())) {
//        ImClient imClient = App.getInstance().getImClient();
//        if (imClient != null) {
//          if (imClient.getConnectStatus() == ImConnectStatus.CONNECTED) {
//            //连接到了IM服务器，发送ping
//            imClient.ping();
//          } else if (imClient.getConnectStatus() == ImConnectStatus.NETWORK_DISCONNECT) {
//            //网络中断，不能太过相信sdk的自动重连功能，所以自己尝试进行一次连接
//            if (CommonUtils.isNetworkAvailable()) {
//              imClient.open(null);
//            }
//          } else {
//            //被踢下线，需要重登录时,
//            ChatService.stopChatService(context);
//            return;
//          }
//        }
//        schedule(SCHEDULE_TIME);
//      }
    }
  };
}
