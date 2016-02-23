package androidLearn.frame.easemobexample.service;

import android.app.Activity;
import android.content.Intent;
import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.MainActivity;
import androidLearn.frame.easemobexample.data.CacheKeys;
import androidLearn.frame.easemobexample.data.DataManager;
import androidLearn.frame.easemobexample.data.LocalCacheUtils;
import androidLearn.frame.easemobexample.data.entity.User;
import androidLearn.frame.easemobexample.utils.BitmapLoader;
import androidLearn.frame.easemobexample.utils.PathUtils;

import android.text.TextUtils;


public class AccountManager {

  private static AccountManager mInstance;

  private AccountManager() {
  }

  public static AccountManager getInstance() {
    if (mInstance == null) {
      mInstance = new AccountManager();
    }

    return mInstance;
  }

  public void saveUser(User user) {
    User oldUser = getUser();
    if (oldUser != null && oldUser.avatar != null && !oldUser.avatar.equals(user.avatar)) {
      PathUtils.deleteAvatarCache(oldUser.id);
    }
    BitmapLoader.loadImage(App.getInstance(), user.avatar);
    DataManager.getInstance().put(CacheKeys.USER, user, 0);
    LocalCacheUtils.getInstance(App.getInstance()).setObject(CacheKeys.USER, user);
  }
//
  public void clearUser() {
    DataManager.getInstance().put(CacheKeys.USER, null, 0);
    LocalCacheUtils.getInstance(App.getInstance()).setObject(CacheKeys.USER, null);
  }
//
  public User getUser() {
    User user = DataManager.getInstance().get(CacheKeys.USER);
    if (user == null) {
      user = getLocalUser();
    }

    return user;
  }

  public User getLocalUser() {
    return LocalCacheUtils.getInstance(App.getInstance()).getObject(CacheKeys.USER, User.class);
  }

  public String getToken() {
    User user = getUser();
    if (user != null) {
      return user.token;
    }
    return null;
  }

  public boolean isLogin() {
    return !TextUtils.isEmpty(getToken());
  }

  public void login(Activity activity, User user) {
    AccountManager.getInstance().saveUser(user);
    Intent intent = new Intent(App.getInstance(), MainActivity.class);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    activity.startActivity(intent);

  }

  public void logout(Activity activity, final boolean showLoginActivity) {
    //停止ChatService
//    ChatService.stopChatService(App.getInstance());
//    //取消别名和设备的绑定，否则用别的账号登录后还会收到前一账号的推送通知
//    //清除用户缓存
//    clearUser();
//    CommonUtils.sendBadge(0);
    //清除患者相关缓存
//    PatientListManager.getInstance().clearCache();
//    PatientManager.getInstance().clearCache();
    //关闭IM连接
//    ImClient imClient = App.getInstance().getImClient();
//    if (imClient != null && imClient.isLoggedIn()) {
//      imClient.close(null);
//    }

//    if (showLoginActivity && activity != null) {
//      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//        Intent intent = new Intent(App.getInstance(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        activity.startActivity(intent);
//      } else {
//        Intent intent = new Intent(App.getInstance(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        activity.startActivity(intent);
//        activity.finish();
//
//        intent = new Intent(App.getInstance(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        App.getInstance().startActivity(intent);
//      }
//    }

  }

//  public void saveDoctorInfo(DoctorInfoResponse.Doctor doctor) {
////    User user = getUser();
////    if (user != null) {
////      saveUser(user);
////    }
//  }

  public void startPush() {
//    User user = getUser();
//    if (user != null) {
//      String alias;
//      if (CommonUtils.isDebugMode()) {
//        alias = "dev_" + user.id;
//      } else {
//        alias = user.id;
//      }
//      JPushInterface.resumePush(App.getInstance());
//      JPushInterface.setAlias(App.getInstance(), alias, new TagAliasCallback() {
//        @Override
//        public void gotResult(int i, String s, Set<String> set) {
//          Log.i("JPush", s);
//        }
//      });
//    }
  }
}
