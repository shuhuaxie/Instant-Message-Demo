package androidLearn.frame.easemobexample.im.message.entity;

import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.data.response.BaseResponse;
import androidLearn.frame.easemobexample.im.easemob.EMImageMsg;
import androidLearn.frame.easemobexample.im.message.ImMessageType;
import androidLearn.frame.easemobexample.utils.BitmapUtils;
import androidLearn.frame.easemobexample.utils.FileUtils;
import androidLearn.frame.easemobexample.utils.PathUtils;
import androidLearn.frame.easemobexample.utils.ThumbnailUtil;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class ImImageMessage extends ImFileMessage {

  public interface ImImageMessageInternalInterface {
    /**
     * 获取显示图片消息用的URI地址
     */
    String getOriginUri();

    /**
     * 获取显示图片消息缩略图用的URI地址
     */
    String getThumbnailUri();
  }

  @Override
  public ImMessageType getType() {
    return ImMessageType.notice_image;
  }

  private static class ImagePath {
    public String url;
    public String url_thumb;
    public double width;
    public double height;
  }

  private ImagePath mPath;

  protected ImImageMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);


    if(internalMessage instanceof EMImageMsg){
      //兼容旧环信消息
      mPath = new ImagePath();
      try {
        ImImageMessageInternalInterface imageMessage = (ImImageMessageInternalInterface) internalMessage;
        mPath.url = imageMessage.getOriginUri();
        mPath.url_thumb = imageMessage.getThumbnailUri();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    String content = getContentString();
    if (!TextUtils.isEmpty(content)) {
      mPath = new Gson().fromJson(content, ImagePath.class);
    }
    if (mPath == null) {  //content为空或者mPath为空可能是之前上传图片失败
      mPath = new ImagePath();
      if(isSendMessage()){  //只有发送消息才可能会出现这种问题，如果接收消息也出现了那么就是严重的异常，也没法处理了。。。
        mPath.url = getLocalFilePath();
        mPath.url_thumb = getLocalFilePath();
      }
    }
  }

  public ImImageMessage(ImMessageInternalInterface internalMessage, String orderId, String srcPath) {
    super(internalMessage, orderId, srcPath);
    mPath = new ImagePath();
    mPath.url = srcPath;
    mPath.url_thumb = srcPath;
    String cachePath = PathUtils.getPicturePathByMessageId(internalMessage.getConversationId(), internalMessage.getMessageId(), false);
    try {
      compressBitmapFile(srcPath, cachePath);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
//      File file = new File(cachePath);
//      String tempPath = cachePath;
//      if (!file.exists()) {
//        if (com.huimei.doctor.utils.FileUtils.copyFile(srcPath, cachePath)) {
//          tempPath = srcPath;
//        }
//      }
//      mPath.url = tempPath;
//      mPath.url_thumb = tempPath;
      saveTempSendData(srcPath, null);
    }
  }

  @Override
  public String getUploadFileExtensionName() {
    return ".jpeg";
  }

  private boolean compressBitmapFile(String originPath, String cachePath) {
    if (TextUtils.isEmpty(originPath)) {
      return false;
    }
    File originfile = new File(originPath);
    if (!originfile.exists()) {
      return false;
    }
    if (originfile.length() < 102400) {
      return FileUtils.copyFile(originPath, cachePath);
    }
    Bitmap bitmap = ThumbnailUtil.createImageThumbnail(originPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
    if (bitmap == null) {
      return false;
    }

    BitmapUtils.saveBitmapToCacheFile(cachePath, bitmap, false, 0);
    bitmap.recycle();
    bitmap = null;
    return true;
  }

  public String getOriginUri() {
    //尝试app自己的缓存文件是否存在
    if (PathUtils.isImageFileExist(getConversationId(), getMessageId(), false)) {
      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
      return "file://" + filePath;
    }

    if(!mPath.url.startsWith("http") && !mPath.url.startsWith("file://")){
      return "file://" + mPath.url;
    }
    //返回server路径
    return mPath.url;
  }

  public String getThumbnailUri() {
    //首先尝试app自己的缓存文件是否存在
    if (PathUtils.isImageFileExist(getConversationId(), getMessageId(), true)) {
      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), true);
      return "file://" + filePath;
    }

    if(!mPath.url_thumb.startsWith("http") && !mPath.url_thumb.startsWith("file://")){
      return "file://" + mPath.url_thumb;
    }
    return mPath.url_thumb;
  }

  @Override
  public String getTitle() {
    return App.getInstance().getString(R.string.picture);
  }

  @Override
  protected String onUploadSuccess(String response) {
    String err = "upload file error. response:" + response;
    try {
      if (!TextUtils.isEmpty(response)) {
        ImageUploadResponse r = new Gson().fromJson(response, ImageUploadResponse.class);
        if (r.status == 0 && r.data != null && r.data.attachment != null) {
          ImagePath data = new ImagePath();
          BitmapUtils.Size size = BitmapUtils.getImageSize(getLocalFilePath());
          data.height = size.height;
          data.width = size.width;
          data.url = r.data.attachment.url;
          data.url_thumb = r.data.attachment.url_thumb;
          deleteTempSendData();
          setAttribute(ImMessage.ATTR_MSG_CONTENT, new GsonBuilder().disableHtmlEscaping().create().toJson(data));
          super.onUploadSuccess(response);
          return null;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    onUploadFail(err);
    return err;
  }

  @Override
  protected String getDownloadUrl() {
    String content = getContentString();
    if (!TextUtils.isEmpty(content)) {
      try {
        ImagePath data = new Gson().fromJson(content, ImagePath.class);
        if (data != null) {
          return data.url;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  protected String getLocalFilePath() {
    String cachePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
    if ((TextUtils.isEmpty(cachePath) || !new File(cachePath).exists()) && isSendMessage()) {
//      MessageData data = getMessageData();
//      if (data != null) {
//        cachePath = data.local;
//      }
    }

    return cachePath;
  }

  @Override
  protected String getLocalCacheFilePath() {
    return PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
  }

  private static class ImageUploadResponseData {
    public Attachment attachment;
  }

  private static class Attachment {
    public String url;
    public String url_thumb;
  }

  private static class ImageUploadResponse extends BaseResponse {
    public ImageUploadResponseData data;
  }

  @Override
  public void onMessageRemoved(){
    super.onMessageRemoved();
    //尝试删除缩略图缓存，这个文件目前是UI侧生成的，不一定存在
    FileUtils.deleteFile(PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), true));
  }
}
