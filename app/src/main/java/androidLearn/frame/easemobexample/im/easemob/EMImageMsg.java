package androidLearn.frame.easemobexample.im.easemob;

import androidLearn.frame.easemobexample.im.message.entity.ImImageMessage;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.utils.PathUtils;
import android.text.TextUtils;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.PathUtil;
import com.google.gson.Gson;

import java.io.File;

public class EMImageMsg extends EMMsg implements ImImageMessage.ImImageMessageInternalInterface {

  private static class ImagePath{
    public String origin;
    public String thumbnail;
  }

  private ImagePath mPath;

  public static EMImageMsg createMessage(EMMessage message){
    return new EMImageMsg(message);
  }

  private EMImageMsg(EMMessage message) {
    super(message);

    String content = message.getStringAttribute(ImMessage.ATTR_MSG_CONTENT, null);
    if(!TextUtils.isEmpty(content)){
      mPath = new Gson().fromJson(content, ImagePath.class);
      if (mPath == null) {
        mPath = new ImagePath();
      }
    }
    else{
      //兼容老版本协议
      mPath = new ImagePath();
      mPath.origin = getImageUri();
      mPath.thumbnail = getImageThumbnailUri();
    }
  }

  @Override
  public String getOriginUri() {
    //尝试app自己的缓存文件是否存在
    if (PathUtils.isImageFileExist(getConversationId(), message.getMsgId(), false)) {
      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), message.getMsgId(), false);
      return "file://" + filePath;
    }

    //返回server路径
    return mPath.origin;
  }

  @Override
  public String getThumbnailUri() {
    //首先尝试app自己的缓存文件是否存在
    if (PathUtils.isImageFileExist(getConversationId(), message.getMsgId(), true)) {
      String filePath = PathUtils.getPicturePathByMessageId(getConversationId(), message.getMsgId(), true);
      return "file://" + filePath;
    }
    return mPath.thumbnail;
  }

  //获取环信图片缓存路径
  private static String getImagePath(String remoteUrl) {
    String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
    String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
    return path;
  }

  //获取环信缩略图缓存路径
  private static String getThumbnailImagePath(String thumbRemoteUrl) {
    String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
    String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
    return path;
  }

  public String getImageUri() {
    if (message.getType() == EMMessage.Type.IMAGE) {
      ImageMessageBody body = (ImageMessageBody) message.getBody();

      //如果本地源文件存在，返回本地文件路径
      String filePath = body.getLocalUrl();
      File file = new File(filePath);
      if (file.exists()) {
        return "file://" + filePath;
      }

      //尝试app自己的缓存文件是否存在
      if (PathUtils.isImageFileExist(getConversationId(), message.getMsgId(), false)) {
        filePath = PathUtils.getPicturePathByMessageId(getConversationId(), message.getMsgId(), false);
        return "file://" + filePath;
      }

      //如果本地缓存文件存在，返回缓存文件路径，否则返回远端url
      String url = body.getRemoteUrl();
      filePath = getImagePath(url);
      file = new File(filePath);
      if (file.exists()) {
        return "file://" + filePath;
      } else {
        return url;
      }
    }
    return null;
  }

  public String getImageThumbnailUri() {
    if (message.getType() == EMMessage.Type.IMAGE) {
      ImageMessageBody body = (ImageMessageBody) message.getBody();
      String url = "";
      String filePath = "";

      //首先尝试app自己的缓存文件是否存在
      if (PathUtils.isImageFileExist(getConversationId(), message.getMsgId(), true)) {
        filePath = PathUtils.getPicturePathByMessageId(getConversationId(), message.getMsgId(), true);
        return "file://" + filePath;
      }

      //尝试本地文件路径
      filePath = body.getLocalUrl();
      File file = new File(filePath);
      if (file.exists()) {
        return "file://" + filePath;
      }

      //尝试环信缓存文件
      url = body.getThumbnailUrl();  //远端缩略图
      if (TextUtils.isEmpty(url) || "null".equals(url)) {  //如果远端缩略图不存在，尝试远端大图
        url = body.getRemoteUrl();
        filePath = getImagePath(url);
      } else {
        filePath = getThumbnailImagePath(url);
      }
      file = new File(filePath);
      if (file.exists()) {
        return "file://" + filePath;
      } else {
        if (!isSendMessage()) {
          //下面的这个方法可以从换新服务器上获取图片缩略图或语音文件
//          downloadFileInMessage();
          //由于傻X环信SDK用上面的方法只能获取图片的缩略图不能获取大图，而图片在他服务器上只保存14天，你不及时获取14天后就什么都没了，所以再调用下面这个方法来获取大图。。。
//          downloadBigImage();
        }
      }

      //url仍有可能为空，先不管了
      return url;
    }
    return null;
  }

//  private void downloadBigImage() {
//    Map<String, String> maps = new HashMap<>();
//    ImageMessageBody body = (ImageMessageBody) message.getBody();
//    String secret = body.getSecret();
//    if (!TextUtils.isEmpty(secret)) {
//      maps.put("share-secret", secret);
//    }
//
//    EMChatManager.getInstance().downloadFile(body.getRemoteUrl(), getImagePath(body.getRemoteUrl()), maps, new EMCallBack() {
//      @Override
//      public void onSuccess() {
//        EMClient.getInstance().getMessageListenerManager().onMessageUpdated(EMImageMsg.this, EMClient.getInstance().getConversation(getConversationId()));
//      }
//
//      @Override
//      public void onError(int i, String s) {
//
//      }
//
//      @Override
//      public void onProgress(int i, String s) {
//
//      }
//    });
//  }
//
//  private void downloadFileInMessage() {
//    final FileMessageBody body = (FileMessageBody) message.getBody();
//    if (!body.downloaded) {
//      new Thread(new Runnable() {
//
//        @Override
//        public void run() {
//          body.setDownloadCallback(new EMCallBack() {
//            @Override
//            public void onSuccess() {
//              EMClient.getInstance().getMessageListenerManager().onMessageUpdated(EMImageMsg.this, EMClient.getInstance().getConversation(getConversationId()));
//            }
//
//            @Override
//            public void onError(int i, String s) {
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//              setProgress(i);
//            }
//          });
//          EMChatManager.getInstance().asyncFetchMessage(message);
//        }
//      }).start();
//    }
//  }
}
