package androidLearn.frame.easemobexample.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 明帅 on 2016-01-25.
 */
public class FileUtils {

  private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

  public static boolean copyFile(String srcPath, String destPath) {
    boolean isCopyOk = false;

    File sourceFile = new File(srcPath);
    File destFile = new File(destPath);

    byte[] buffer = null;
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    // 如果此时没有文件夹目录就创建
    String canonicalPath = "";
    try {
      canonicalPath = destFile.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!destFile.exists()) {
      if (canonicalPath.lastIndexOf(File.separator) >= 0) {
        canonicalPath = canonicalPath.substring(0, canonicalPath.lastIndexOf(File.separator));
        File dir = new File(canonicalPath);
        if (!dir.exists()) {
          dir.mkdirs();
        }
      }
    }

    try {
      bis = new BufferedInputStream(new FileInputStream(sourceFile), DEFAULT_BUFFER_SIZE);
      bos = new BufferedOutputStream(new FileOutputStream(destFile), DEFAULT_BUFFER_SIZE);
      buffer = new byte[DEFAULT_BUFFER_SIZE];
      int len = 0;
      while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
        bos.write(buffer, 0, len);
      }
      bos.flush();
      isCopyOk = sourceFile != null && sourceFile.length() == destFile.length();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bos != null) {
          bos.close();
          bos = null;
        }
        if (bis != null) {
          bis.close();
          bis = null;
        }
        buffer = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return isCopyOk;
  }

  public static boolean deleteFile(String srcPath){
    if(TextUtils.isEmpty(srcPath)){
      return false;
    }

    File file = new File(srcPath);
    if(!file.exists() || !file.isFile()){
      return false;
    }

    return file.delete();
  }
}
