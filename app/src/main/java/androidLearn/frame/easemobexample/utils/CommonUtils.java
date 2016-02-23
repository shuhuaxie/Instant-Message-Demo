package androidLearn.frame.easemobexample.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidLearn.frame.easemobexample.App;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CommonUtils {
  /**
   * 检测Sdcard是否存在
   *
   * @return
   */
  public static boolean isExitsSdCard() {
    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
      return true;
    else
      return false;
  }

  public static void sendBadge(int number) {
    sendBadgeToSamsumg(number);
    sendBadgeToSony(number);
    sendBadgeToApex(number);
    sendBadgeToXiaoMi(number);
    sendBadgeToHuawei(number);
  }

  private static void sendBadgeToSony(int number) {
    Context context = App.getInstance();
    boolean isShow = true;
    if (0 >= number) {
      isShow = false;
    }
    Intent localIntent = new Intent();
    localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
    localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
    localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context));//启动页
    localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(number));//数字
    localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
    context.sendBroadcast(localIntent);
  }

  private static void sendBadgeToSamsumg(int number) {
    Context context = App.getInstance();
    Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
    localIntent.putExtra("badge_count", number);//数字
    localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
    localIntent.putExtra("badge_count_class_name", getLauncherClassName(context)); //启动页
    context.sendBroadcast(localIntent);
  }

  private static void sendBadgeToApex(int number) {
    Context context = App.getInstance();

    final String ACTION_COUNTER_CHANGED = "com.anddoes.launcher.COUNTER_CHANGED";
    final String EXTRA_NOTIFY_PACKAGE = "package";
    final String EXTRA_NOTIFY_CLASS = "class";
    final String EXTRA_NOTIFY_COUNT = "count";

    final String packageName = context.getPackageName();
    final String className = getLauncherClassName(context);

    Intent intent = new Intent(ACTION_COUNTER_CHANGED);
    intent.putExtra(EXTRA_NOTIFY_PACKAGE, packageName);
    intent.putExtra(EXTRA_NOTIFY_CLASS, className);
    intent.putExtra(EXTRA_NOTIFY_COUNT, number);
    context.sendBroadcast(intent);
  }

  private static void sendBadgeToXiaoMi(int count) {
    Context context = App.getInstance();

    try {
      Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
      Object miuiNotification = miuiNotificationClass.newInstance();
      Field field = miuiNotification.getClass().getDeclaredField("messageCount");
      field.setAccessible(true);
      field.set(miuiNotification, String.valueOf(count == 0 ? "" : count));  // 设置信息数-->这种发送必须是miui 6才行
    } catch (Exception e) {
      // miui 6之前的版本
      Intent localIntent = new Intent(
          "android.intent.action.APPLICATION_MESSAGE_UPDATE");
      localIntent.putExtra(
          "android.intent.extra.update_application_component_name",
          context.getPackageName() + "/" + getLauncherClassName(context));
      localIntent.putExtra(
          "android.intent.extra.update_application_message_text", String.valueOf(count == 0 ? "" : count));
      context.sendBroadcast(localIntent);
    }
  }

  private static void sendBadgeToHuawei(int number) {
    Context context = App.getInstance();

    Bundle localBundle = new Bundle();
    localBundle.putString("package", context.getPackageName());
    localBundle.putString("class", getLauncherClassName(context));
    localBundle.putInt("badgenumber", number);
    try{
      context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
    }
    catch(Exception e){
    }
  }

  public static void sendBadgeToVIVO(int number) {
    Context context = App.getInstance();
    Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
    localIntent.putExtra("notificationNum", number);//数字
    localIntent.putExtra("packageName", context.getPackageName());//包名
    localIntent.putExtra("className", getLauncherClassName(context)); //启动页
    context.sendBroadcast(localIntent);
  }

  public static String getLauncherClassName(Context context) {

    PackageManager pm = context.getPackageManager();

    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
    for (ResolveInfo resolveInfo : resolveInfos) {
      String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
      if (pkgName.equalsIgnoreCase(context.getPackageName())) {
        String className = resolveInfo.activityInfo.name;
        return className;
      }
    }
    return null;
  }

  public static boolean isNetworkAvailable() {
    try {
      ConnectivityManager connectivity = (ConnectivityManager) App.getInstance()
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivity != null) {

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
          if (info.getState() == NetworkInfo.State.CONNECTED) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static boolean isScreenOn(){
    PowerManager pm = (PowerManager)App.getInstance().getSystemService(Context.POWER_SERVICE);
    return pm.isScreenOn();
  }

  public static void vibrate(){
    Vibrator vibrator = (Vibrator)App.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.cancel();
    long [] pattern = {100,300,100,300};
    vibrator.vibrate(pattern, -1);
  }

  public static String stringToMD5(String string) {
    byte[] hash;

    try {
      hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }

    StringBuilder hex = new StringBuilder(hash.length * 2);
    for (byte b : hash) {
      if ((b & 0xFF) < 0x10)
        hex.append("0");
      hex.append(Integer.toHexString(b & 0xFF));
    }

    return hex.toString();
  }

  public static boolean isDebugMode(){
    return true;
  }
}
