package androidLearn.frame.easemobexample.utils;

import androidLearn.frame.easemobexample.App;
import android.os.Environment;
import java.io.File;

public class PathUtils {
  private static File checkAndMkdirs(File file) {
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  private static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  /**
   * 有 sdcard 的时候，小米是 /storage/sdcard0/Android/data/com.avoscloud.chat/cache/
   * 无 sdcard 的时候，小米是 /data/data/com.avoscloud.chat/cache
   * 依赖于包名。所以不同应用使用该库也没问题，要有点理想。
   *
   * @return
   */
  private static File getAvailableCacheDir() {
    if (isExternalStorageWritable()) {
      return App.getInstance().getExternalCacheDir();
    } else {
      // 只有此应用才能访问。拍照的时候有问题，因为拍照的应用写入不了该文件
      return App.getInstance().getCacheDir();
    }
  }

  /**
   * 可能文件会被清除掉，需要检查是否存在
   *
   * @param id
   * @return
   */
  public static String getChatFilePath(String id) {
    String path = new File(getAvailableCacheDir(), id).getAbsolutePath();
//    LogUtils.d("path = ", path);
    return path;
  }

  public static String getConversationFilePath(String conversationId) {
    File file = new File(getAvailableCacheDir(), conversationId);
    checkAndMkdirs(file);
    return file.getAbsolutePath();
  }

  /**
   * 录音保存的地址
   *
   * @return
   */
  public static String getRecordPathByCurrentTime(String conversationId) {
    return new File(getConversationFilePath(conversationId), "audio_" + System.currentTimeMillis()).getAbsolutePath();
  }

  /**
   * 拍照保存的地址
   *
   * @return
   */
  public static String getPicturePathByCurrentTime() {
    String path = new File(getAvailableCacheDir(), "picture_" + System.currentTimeMillis()).getAbsolutePath();
//    LogUtils.d("picture path ", path);
    return path;
  }

  public static String getPicturePathByMessageId(String conversationId, String messageId, boolean thumbnail) {
    String pre = thumbnail ? "th_" : "img_";
    String path = new File(getConversationFilePath(conversationId), pre + messageId).getAbsolutePath();
    return path;
  }

  public static String getAudioPathByMessageId(String conversationId, String messageId) {
    String path = new File(getConversationFilePath(conversationId), "audio_" + messageId).getAbsolutePath();
    return path;
  }

  public static boolean isImageFileExist(String conversationId, String messageId, boolean thumbnail) {
    String path = getPicturePathByMessageId(conversationId,messageId,thumbnail);
    File file = new File(path);
    if (file.exists()) {
      if (file.length() > 0) {
        return true;
      } else {
        file.delete();
      }
    }
    return false;
  }

  //获取头像缓存路径，该路径的文件可能不存在
  public static String getAvatarCachePath(String userid){
    String path = new File(getAvailableCacheDir(), "ava_" + userid).getAbsolutePath();
    return path;
  }

  public static String getRealAvatarPath(String userid, String defaultImg){
    if(isAvatarCacheExist(userid)){
      return "file://" + getAvatarCachePath(userid);  //为了bitmaploader能直接加载， 路径前要加“file://”
    }
    return defaultImg;
  }

  public static boolean isAvatarCacheExist(String userid) {
    String path = getAvatarCachePath(userid);
    File file = new File(path);
    if (file.exists()) {
      if (file.length() > 0) {
        return true;
      } else {
        file.delete();
      }
    }
    return false;
  }

  public static void deleteAvatarCache(String userid){
    String path = getAvatarCachePath(userid);
    File file = new File(path);
    if (file.exists()) {
      file.delete();
    }
  }
}
