package androidLearn.frame.easemobExample.utils;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidLearn.frame.easemobExample.LoginActivity;
import androidLearn.frame.easemobExample.MainActivity;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.widget.ProgressDialogFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class UiUtils {

  private static Paint sDividerPaint;
  private static final int NEW_MESSAGE_REQUEST_CODE = 1000;
  private static final int CONNECT_LOST_REQUEST_CODE = 1001;
  private static Toast mToast;

  static {
    sDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    sDividerPaint.setColor(0xffdadada);
  }

  public static ProgressDialogFragment showProgressDialog(Context context, String text) {
    return showProgressDialog(context, text, false);
  }

  public static ProgressDialogFragment dismiss(ProgressDialogFragment dialogFragment) {
    if (dialogFragment != null) {
      dialogFragment.dismissAllowingStateLoss();
    }
    return null;
  }

  public static ProgressDialogFragment showProgressDialog(Context context, String text, boolean cancelable) {
    ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
    Bundle args = new Bundle();
    args.putString(ProgressDialogFragment.ARGUMENT_TEXT, text);
    dialogFragment.setArguments(args);
    dialogFragment.setCancelable(cancelable);
    FragmentTransaction transaction = ((Activity) context).getFragmentManager().beginTransaction();
    transaction.add(dialogFragment, "hm_progress_dialog");
    transaction.commitAllowingStateLoss();
    return dialogFragment;
  }

  public static Paint getDividerPaint() {
    return sDividerPaint;
  }

  public static void showToast(Context context, String text) {
    showToast(context, text, true);
  }

  public static void showToast(Context context, String text, boolean cancelPreviousToast) {
    if(TextUtils.isEmpty(text)){
      return;
    }
    if(mToast == null) {
      mToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
    } else {
      mToast.setText(text);
    }

    mToast.show();
  }
  public static void hideKeyBoard(Activity activity) {
    // 多使用几个方法...
    try {
      View view = activity.getCurrentFocus();
      if (view != null) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken()
            , InputMethodManager.HIDE_NOT_ALWAYS);
      }
    } catch (Exception e) {

    }
  }


  public static void cancelNewMessageNotification(Context context) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NEW_MESSAGE_REQUEST_CODE);
  }
  public static void cancelAllNotification(Context context) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }
  public static void showKeyBoard(Activity activity, View view) {
    try {
      ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
          showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    } catch (Exception e) {

    }
  }

  public static void setTopImageHeight(ImageView imageView, Activity activity, float ratio) {
    WindowManager wm1 = activity.getWindowManager();
    int width1 = wm1.getDefaultDisplay().getWidth();
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width1
        , (int) (width1 * ratio));
    imageView.setLayoutParams(layoutParams);
  }

  public static void showKickedNotification(Context context) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

    Intent intent = new Intent(context, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent contentIntent = PendingIntent.getActivity(context, CONNECT_LOST_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    builder.setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(context.getText(R.string.im_account_kicked))
        .setWhen(System.currentTimeMillis())
        .setContentTitle(context.getText(R.string.im_account_kicked))
        .setContentText(context.getText(R.string.im_click_to_relogin))
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
    ;

    Notification notification = builder.build();
    notificationManager.notify(CONNECT_LOST_REQUEST_CODE, notification);


//    Notification notification = new Notification();
//    notification.icon = R.mipmap.icon_notification;
//    notification.tickerText = "您的账户已在其他地方登录";
//    notification.when = System.currentTimeMillis();
//    //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
//    //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
//    //FLAG_ONGOING_EVENT 通知放置在正在运行
//    //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
//    //notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//    notification.flags |= Notification.FLAG_AUTO_CANCEL;
//    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//    //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
//    //DEFAULT_LIGHTS  使用默认闪光提示
//    //DEFAULT_SOUND   使用默认提示声音
//    //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
//    notification.defaults = Notification.DEFAULT_ALL;
//    notification.ledARGB = Color.RED;
//    notification.ledOnMS = 5000; //闪光时间，毫秒
//
//    // 设置通知的事件消息
//    CharSequence contentTitle = context.getString(R.string.im_account_kicked); // 通知栏标题
//    CharSequence contentText = context.getString(R.string.im_click_to_relogin); // 通知栏内容
//
////    Intent notificationIntent =new Intent(PushReceiver.INTENT_PUSH);
////    notificationIntent.putExtra("model", model);
////    notificationIntent.putExtra("PUSH", true);
////    PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//
//    // 把Notification传递给NotificationManager
//    notificationManager.notify(CONNECT_LOST_REQUEST_CODE, notification);
  }

  public static void showNewMessageNotification(Context context, ImMessage message) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

    Intent intent = new Intent(context, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.setAction(MainActivity.ACTION_NEW_MESSAGE);
    intent.putExtra(MainActivity.EXTRA_MESSAGE_BUDDYID, message.getSenderId());
    PendingIntent contentIntent = PendingIntent.getActivity(context, NEW_MESSAGE_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    Bitmap avatar = null;
    if (PathUtils.isAvatarCacheExist(message.getSenderId())) {
      avatar = BitmapFactory.decodeFile(PathUtils.getAvatarCachePath(message.getSenderId()));
    } else {
      avatar = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    builder.setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(avatar)
        .setTicker(context.getText(R.string.im_new_message))
        .setWhen(System.currentTimeMillis())
        .setContentTitle(String.format(context.getString(R.string.im_new_message_buddy), message.getSenderName()))
        .setContentText(message.getTitle())
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_SOUND)
    ;

    Notification notification = builder.build();
    notificationManager.notify(NEW_MESSAGE_REQUEST_CODE, notification);
    CommonUtils.vibrate();
    avatar.recycle();


//    Notification notification = new Notification();
//    notification.icon = R.mipmap.icon_notification;
//    notification.largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//    notification.tickerText = "您有新消息";
//    notification.when = System.currentTimeMillis();
//    //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
//    //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
//    //FLAG_ONGOING_EVENT 通知放置在正在运行
//    //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
//    //notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//    notification.flags |= Notification.FLAG_AUTO_CANCEL;
//    notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//    //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
//    //DEFAULT_LIGHTS  使用默认闪光提示
//    //DEFAULT_SOUND   使用默认提示声音
//    //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
//    notification.defaults = Notification.DEFAULT_ALL;
//    notification.ledARGB = Color.BLUE;
//    notification.ledOnMS = 5000; //闪光时间，毫秒
//
//    // 设置通知的事件消息
//    CharSequence contentTitle = String.format("来自%s的新消息", sender); // 通知栏标题
//    CharSequence contentText = content; // 通知栏内容
//
////    Intent notificationIntent =new Intent(PushReceiver.INTENT_PUSH);
////    notificationIntent.putExtra("model", model);
////    notificationIntent.putExtra("PUSH", true);
////    PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//
//    // 把Notification传递给NotificationManager
//    notificationManager.notify(NEW_MESSAGE_REQUEST_CODE, notification);
  }


}
