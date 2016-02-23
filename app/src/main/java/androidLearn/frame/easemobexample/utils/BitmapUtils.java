package androidLearn.frame.easemobexample.utils;

import android.content.res.Resources;
import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.widget.PortraitView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BitmapUtils {

  public static class Size{
    public double width;
    public double height;
  }

  public static final String DEFAULT_AVATAR_URI = "drawable://" + R.drawable.icon;
  public static final int DEFAULT_AVATAR_HEIGHT = App.getInstance().getResources().getDimensionPixelSize(R.dimen.size_avatar);
  private static final boolean ifUseAvatarCache = false;   //是否使用缓存头像图片
  private static final int SCALE_RATE = 2;

  public static Size getImageSize(String srcPath){
    Size size = new Size();
    try {
      int degree = BitmapUtils.getDegress(srcPath);
      FileDescriptor fd = new FileInputStream(srcPath).getFD();
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inSampleSize = 1;
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFileDescriptor(fd, null, options);
      if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
        return size;
      }
      if(degree == 90 || degree == 270){  //图片需要旋转的时候
        size.width = options.outHeight;
        size.height = options.outWidth;
      }
      else{
        size.width = options.outWidth;
        size.height = options.outHeight;
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return size;
  }

  //保存图像到本地缓存文件
  public static void saveBitmapToCacheFile(final String filename, final Bitmap bitmap, final boolean thumbnail, final int maxHeight) {

    File file = new File(filename);
    if (!file.exists() || file.length() == 0) {
      Bitmap outBitmap = null;
      try {
        int quality = thumbnail ? 70 : 100;
        FileOutputStream out = new FileOutputStream(file);
        if (thumbnail) {
          outBitmap = small(bitmap, maxHeight);
        } else {
          outBitmap = bitmap;
        }
        outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        out.flush();
        out.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (outBitmap != null && !outBitmap.equals(bitmap)) {
          outBitmap.recycle();
        }
      }
    }

  }

  private static Bitmap small(Bitmap bitmap, int maxHeight) {
    Matrix matrix = new Matrix();

    float scale;
    if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0 && maxHeight < bitmap.getHeight()) {
      scale = (float) maxHeight / (bitmap.getHeight() * SCALE_RATE);   //缩略图不用那么大，尽量减小文件大小
      matrix.postScale(scale, scale); //长和宽放大缩小的比例
      return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    } else {
      return bitmap;
    }
  }

  public static void showAvatar(final String userId, final String url, final PortraitView view) {
    String uri;

    if (ifUseAvatarCache) {
      uri = PathUtils.getRealAvatarPath(userId, url);
    } else {
      uri = url;
    }

    BitmapLoader.displayImage(App.getInstance(), uri, view, new BitmapLoader.BitmapLoadingListener() {
      @Override
      public void onLoadingStarted(String s, View view) {

      }

      @Override
      public void onLoadingFailed(String s, View view, String failReason) {

      }

      @Override
      public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        if (!DEFAULT_AVATAR_URI.equals(s)) {
          String filename = PathUtils.getAvatarCachePath(userId);
          BitmapUtils.saveBitmapToCacheFile(filename, bitmap, true, DEFAULT_AVATAR_HEIGHT * SCALE_RATE); //头像缩略图的大小不再调整，保证通知栏显示的头像图标大小合适，所以这里要乘SCALE_RATE
        }
      }
    });
  }

  /**
   * get the orientation of the bitmap {@link ExifInterface}
   *
   * @param path
   * @return
   */
  public final static int getDegress(String path) {
    int degree = 0;
    try {
      ExifInterface exifInterface = new ExifInterface(path);
      int orientation = exifInterface.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;
  }

  /**
   * rotate the bitmap
   *
   * @param bitmap
   * @param degress
   * @return
   */
  public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
    if (bitmap != null) {
      Matrix m = new Matrix();
      m.postRotate(degress);
      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
      return bitmap;
    }
    return bitmap;
  }

  /**
   * caculate the bitmap sampleSize
   *
   * @param options
   * @return
   */
  public final static int caculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (rqsW == 0 || rqsH == 0) return 1;
    if (height > rqsH || width > rqsW) {
      final int heightRatio = Math.round((float) height / (float) rqsH);
      final int widthRatio = Math.round((float) width / (float) rqsW);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }

  /**
   * 压缩指定路径的图片，并得到图片对象
   *
   * @param path bitmap source path
   * @return Bitmap {@link Bitmap}
   */
  public final static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(path, options);
  }

  /**
   * 压缩某个输入流中的图片，可以解决网络输入流压缩问题，并得到图片对象
   *
   * @param is bitmap source path
   * @return Bitmap {@link Bitmap}
   */
  public final static Bitmap compressBitmap(InputStream is, int reqsW, int reqsH) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ReadableByteChannel channel = Channels.newChannel(is);
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      while (channel.read(buffer) != -1) {
        buffer.flip();
        while (buffer.hasRemaining()) baos.write(buffer.get());
        buffer.clear();
      }
      byte[] bts = baos.toByteArray();
      Bitmap bitmap = compressBitmap(bts, reqsW, reqsH);
      is.close();
      channel.close();
      baos.close();
      return bitmap;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 压缩指定byte[]图片，并得到压缩后的图像
   *
   * @param bts
   * @param reqsW
   * @param reqsH
   * @return
   */
  public final static Bitmap compressBitmap(byte[] bts, int reqsW, int reqsH) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
    options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeByteArray(bts, 0, bts.length, options);
  }

  /**
   * 压缩已存在的图片对象，并返回压缩后的图片
   *
   * @param bitmap
   * @param reqsW
   * @param reqsH
   * @return
   */
  public final static Bitmap compressBitmap(Bitmap bitmap, int reqsW, int reqsH) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] bts = baos.toByteArray();
      Bitmap res = compressBitmap(bts, reqsW, reqsH);
      baos.close();
      return res;
    } catch (IOException e) {
      e.printStackTrace();
      return bitmap;
    }
  }

  /**
   * 压缩资源图片，并返回图片对象
   *
   * @param res   {@link Resources}
   * @param resID
   * @param reqsW
   * @param reqsH
   * @return
   */
  public final static Bitmap compressBitmap(Resources res, int resID, int reqsW, int reqsH) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resID, options);
    options.inSampleSize = caculateInSampleSize(options, reqsW, reqsH);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resID, options);
  }


  /**
   * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
   * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
   *
   * @param bitmap
   * @param maxBytes 压缩后的图像最大大小 单位为byte
   * @return
   */
  public final static Bitmap compressBitmap(Bitmap bitmap, long maxBytes) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      int options = 90;
      while (baos.toByteArray().length > maxBytes && options > 0) {
        baos.reset();
        bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
        options -= 10;
      }
      byte[] bts = baos.toByteArray();
      Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
      baos.close();
      return bmp;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 得到指定路径图片的options
   *
   * @param srcPath
   * @return Options {@link BitmapFactory.Options}
   */
  public final static BitmapFactory.Options getBitmapOptions(String srcPath) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(srcPath, options);
    return options;
  }

}
