package androidLearn.frame.easemobExample.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChatReceiver extends BroadcastReceiver {


  @Override
  public void onReceive(final Context context, Intent intent) {
//    if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) ||
//        "android.intent.action.USER_PRESENT".equals(intent.getAction())){
//      if(AccountManager.getInstance().isLogin()){
//        //登录IM
//        App.getInstance().getImClient().open(new ImClient.ImClientCallBack() {
//          @Override
//          public void onCallBack(boolean success, String errMsg) {
//            ChatService.startChatService(context);
//          }
//        });
//        //启动推送
//        AccountManager.getInstance().startPush();
//      }
//    }
//    else if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())){
//      if(CommonUtils.isNetworkAvailable()                   //网络连通
//          && AccountManager.getInstance().isLogin()         //server账户已登录
//          && !App.getInstance().getImClient().isLoggedIn()  //IM账户未登录
//          ){
//        App.getInstance().getImClient().open(new ImClient.ImClientCallBack() {
//          @Override
//          public void onCallBack(boolean success, String errMsg) {
//            ChatService.startChatService(context);
//          }
//        });
//      }
//    }
//    else if(MainActivity.ACTION_NEW_MESSAGE.equals(intent.getAction())){
//      Intent i = new Intent(context, MainActivity.class);
//      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//      i.setAction(MainActivity.ACTION_NEW_MESSAGE);
//      i.putExtra(MainActivity.EXTRA_MESSAGE_BUDDYID, intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE_BUDDYID));
//      context.startActivity(i);
//    }
//    else if("testmsg".equals(intent.getAction())){
////      UiUtils.showToast(context, App.getInstance().getImClient().isConnected() ? "Im连接":"im断开");
//    }
  }
}
