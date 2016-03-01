package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.easemob.EMImageMsg;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.utils.BitmapUtils;
import androidLearn.frame.easemobExample.utils.FileUtils;
import androidLearn.frame.easemobExample.utils.PathUtils;
import androidLearn.frame.easemobExample.utils.ThumbnailUtil;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

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
    public String localPath;
    public double width;
    public double height;
  }

  private ImagePath mData;

  protected ImImageMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);


    if (internalMessage instanceof EMImageMsg) {
      //兼容旧环信消息
      mData = new ImagePath();
      try {
        ImImageMessageInternalInterface imageMessage = (ImImageMessageInternalInterface) internalMessage;
        mData.localPath = imageMessage.getThumbnailUri();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    String content = getContentString();
    if (!TextUtils.isEmpty(content)) {
      mData = new Gson().fromJson(content, ImagePath.class);
    }
    if (mData != null && !TextUtils.isEmpty(mData.url)) {
      final String localFileUri = getDownloadLocalFilePath();
      File downFile = new File(localFileUri);
      if (downFile == null || !downFile.exists()) {
        downloadFile();
      }
    }
    if (mData == null) {  //content为空或者mPath为空可能是之前上传图片失败
      mData = new ImagePath();
      if (isSendMessage()) {  //只有发送消息才可能会出现这种问题，如果接收消息也出现了那么就是严重的异常，也没法处理了。。。
        mData.localPath = getUploadLocalFilePath();
      }
    }
  }

  public ImImageMessage(ImMessageInternalInterface internalMessage, String orderId, String srcPath) {
    super(internalMessage, orderId, srcPath);
    mData = new ImagePath();
    mData.localPath = srcPath;
    String cachePath = PathUtils.getPicturePathByMessageId(internalMessage.getConversationId(), internalMessage.getMessageId(), false);
    try {
      compressBitmapFile(srcPath, cachePath);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
//      File file = new File(cachePath);
//      String tempPath = cachePath;
//      if (!file.exists()) {
//        if (androidLearn.frame.easemobExample.utils.FileUtils.copyFile(srcPath, cachePath)) {
//          tempPath = srcPath;
//        }
//      }
//      mData.localPath = tempPath;
      saveTempSendData(srcPath, null);
    }
  }

  @Override
  public String getUploadFileExtensionName() {
    return ".jpeg";
  }

  private boolean compressBitmapFile(String originPath, String cachePath) {
    Log.e("xie", "ImImageMessage  originPath:" + originPath + ", cachePath:" + cachePath);
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

//  public String getOriginUri() {
//    //尝试app自己的缓存文件是否存在
//    if (PathUtils.isImageFileExist(getConversationId(), getMessageId(), false)) {
//      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
//      return "file://" + filePath;
//    }
//
//    if(!mData.url.startsWith("http") && !mData.url.startsWith("file://")){
//      return "file://" + mData.url;
//    }
//    //返回server路径
//    return mData.url;
//  }

  public String getThumbnailUri() {
//    //首先尝试app自己的缓存文件是否存在
    if (PathUtils.isImageFileExist(getConversationId(), getMessageId(), true)) {
      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), true);
      return "file://" + filePath;
    }
    if (!TextUtils.isEmpty(mData.localPath) && !mData.localPath.startsWith("http") && !mData.localPath.startsWith("file://")) {
      return "file://" + mData.localPath;
    }
    return mData.localPath;
  }

  @Override
  public String getTitle() {
    return App.getInstance().getString(R.string.picture);
  }

  @Override
  protected String onUploadSuccess(String fileAddress) {
    String err = "upload file error. response:" + fileAddress;
    try {
      if (!TextUtils.isEmpty(fileAddress)) {
//        ImageUploadResponse r = new Gson().fromJson(response, ImageUploadResponse.class);
//        if (r.status == 0 && r.data != null && r.data.attachment != null) {
        ImagePath data = new ImagePath();
        BitmapUtils.Size size = BitmapUtils.getImageSize(getUploadLocalFilePath());
        data.height = size.height;
        data.width = size.width;
        data.url = fileAddress;
        data.localPath = getThumbnailUri();
        deleteTempSendData();
        setAttribute(ImMessage.ATTR_MSG_CONTENT, new GsonBuilder().disableHtmlEscaping().create().toJson(data));
        super.onUploadSuccess(fileAddress);
        return null;
//        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    onUploadFail(err);
    return err;
  }

  @Override
  protected String getDownloadUrl() {
//    String content = getContentString();
//    if (!TextUtils.isEmpty(content)) {
//      try {
//        ImagePath data = new Gson().fromJson(content, ImagePath.class);
//        if (data != null) {
    return mData.url;
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//    return null;
  }

  @Override
  protected String getUploadLocalFilePath() {
    String cachePath = PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
    Log.e("xie", "cachePath:" + cachePath);
    if ((TextUtils.isEmpty(cachePath) || !new File(cachePath).exists()) && isSendMessage()) {
//      MessageData data = getMessageData();
//      if (data != null) {
//        cachePath = data.local;
//      }
    }

    return cachePath;
  }

  @Override
  protected String getDownloadLocalFilePath() {
    return PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), false);
  }

  @Override
  public void onMessageRemoved() {
    super.onMessageRemoved();
    //尝试删除缩略图缓存，这个文件目前是UI侧生成的，不一定存在
    FileUtils.deleteFile(PathUtils.getPicturePathByMessageId(getConversationId(), getMessageId(), true));
  }
}
