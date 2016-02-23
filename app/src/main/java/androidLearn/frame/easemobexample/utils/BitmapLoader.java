package androidLearn.frame.easemobexample.utils;

import android.content.ContentResolver;
import android.content.Context;
import androidLearn.frame.easemobexample.App;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class BitmapLoader {

  public interface BitmapLoadingListener {
    void onLoadingStarted(String uri, View imageView);

    void onLoadingFailed(String uri, View imageView, String errMsg);

    void onLoadingComplete(String uri, View imageView, Bitmap bitmap);
  }

  enum LibType {
    ImageLoader,
    Glide,
  }

  private static LibType LIBTYPE = LibType.ImageLoader;

  private static ImageLoader imageLoader = null;
  private static String host = "";

  private synchronized static ImageLoader getImageLoader(Context context) {
    if (imageLoader == null) {
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
          context.getApplicationContext())
          .defaultDisplayImageOptions(getOptions(0))
          .diskCacheSize(50 * 1024 * 1024)
          .denyCacheImageMultipleSizesInMemory()
          .memoryCacheSizePercentage(10)
          .build();
      imageLoader = ImageLoader.getInstance();
      imageLoader.init(config);
    }

    return imageLoader;
  }

  public static void setHost(String host) {
    BitmapLoader.host = host;
  }

  private static DisplayImageOptions getOptions(int defaultRes) {
    DisplayImageOptions options = new DisplayImageOptions.Builder()
        .considerExifParams(true)
        .cacheOnDisk(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .cacheInMemory(true)
        .showImageForEmptyUri(defaultRes)
        .showImageOnFail(defaultRes)
        .showImageOnLoading(defaultRes)
//				.displayer(new FadeInBitmapDisplayer(300))
        .build();

    return options;
  }

  public static void displayImage(Context context, String url, ImageView imageView){
    displayImage(context, url, imageView, 0);
  }

  public static void displayImage(Context context, String url, ImageView imageView, int defaultRes) {
    switch (LIBTYPE) {
      case ImageLoader: {
        ImageLoader imageLoader = getImageLoader(context);
        imageLoader.displayImage(getRealUrl(url), imageView, getOptions(defaultRes));
      }
      break;
      case Glide: {
        Glide
            .with(context)
            .load(Uri.parse(getRealUrl(url)))
            .error(defaultRes)
            .into(imageView);
      }
      break;
    }
  }

  public static void displayImage(Context context, final String url, final ImageView imageView, final BitmapLoadingListener listener) {
    displayImage(context,url,imageView,0,listener);
  }

  public static void displayImage(Context context, final String url, final ImageView imageView, int defaultRes, final BitmapLoadingListener listener) {
    switch (LIBTYPE) {
      case ImageLoader: {
        ImageLoader imageLoader = getImageLoader(context);
        imageLoader.displayImage(getRealUrl(url), imageView, getOptions(defaultRes), new ImageLoadingListener() {

          @Override
          public void onLoadingStarted(String s, View view) {
            if (listener != null) {
              listener.onLoadingStarted(s, view);
            }
          }

          @Override
          public void onLoadingFailed(String s, View view, FailReason failReason) {
            if (listener != null && failReason != null && failReason.getCause() != null) {
              listener.onLoadingFailed(s, view, failReason.getCause().getMessage());
            }
          }

          @Override
          public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            if (listener != null) {
              listener.onLoadingComplete(s, view, bitmap);
            }
          }

          @Override
          public void onLoadingCancelled(String s, View view) {
            if (listener != null) {
              listener.onLoadingFailed(s, view, "cancelled");
            }
          }
        });
      }
      break;
      case Glide: {
        if(listener != null){
          listener.onLoadingStarted(getRealUrl(url), imageView);
        }
        Glide
            .with(context.getApplicationContext())
            .load(getUri(url))
            .asBitmap()
            .error(defaultRes)
            .placeholder(defaultRes)
            .listener(new RequestListener<Uri, Bitmap>() {
              @Override
              public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                if (listener != null) {
                  listener.onLoadingFailed(getRealUrl(url), imageView, e.getMessage());
                }
                return false;
              }

              @Override
              public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                  listener.onLoadingComplete(getRealUrl(url), imageView, resource);
                }
                return false;
              }
            })
            .into(imageView);
      }
      break;
    }

  }

  public static void loadImage(Context context, String url) {
    switch (LIBTYPE){
      case ImageLoader:
        ImageLoader imageLoader = getImageLoader(context);
        imageLoader.loadImageSync(getRealUrl(url));
        break;
      case Glide:
        Glide.with(context.getApplicationContext()).load(getUri(url)).preload();
        break;
    }

  }

  public static void clear() {
    switch (LIBTYPE) {
      case ImageLoader:
        if (imageLoader != null) {
          imageLoader.clearDiskCache();
          imageLoader.clearMemoryCache();
          imageLoader.destroy();
        }
        imageLoader = null;
        break;
      case Glide:
        Glide.get(App.getInstance()).clearMemory();
        Glide.get(App.getInstance()).clearDiskCache();
        break;
    }
  }

  private static String getRealUrl(String url) {
    String realurl = host + url;
    if (url != null) {
      if (url.startsWith("http") || url.startsWith("file") || url.startsWith("drawable")) {
        realurl = url;
      } else {
        if (url.startsWith("/")) {
          realurl = host + url;
        } else {
          realurl = host + "/" + url;
        }
      }
    }

    return realurl;
  }

  private static Uri getUri(String url){
    String realurl = getRealUrl(url);
    if(!TextUtils.isEmpty(realurl)){
      if (realurl.startsWith("http") || realurl.startsWith("file")){
        return Uri.parse(realurl);
      }
      else if(realurl.startsWith("drawable")){
        int res = Integer.parseInt(realurl.replace("drawable://", ""));
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
            + App.getInstance().getResources().getResourcePackageName(res) + "/"
            + App.getInstance().getResources().getResourceTypeName(res) + "/"
            + App.getInstance().getResources().getResourceEntryName(res));
      }
    }

    return Uri.parse("");
  }
}
