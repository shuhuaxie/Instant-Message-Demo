package androidLearn.frame.easemobExample.im.message.entity;

import androidLearn.frame.easemobExample.common.Constants;
import androidLearn.frame.easemobExample.data.HmDataService;
import androidLearn.frame.easemobExample.data.entity.MessageData;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageStatus;
import androidLearn.frame.easemobExample.utils.FileUtils;

import android.text.TextUtils;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

public abstract class ImFileMessage extends ImMessage {

  private String tempCachePath;
  private UploadManager uploadManager;
  protected static MessageTransferManager transferManager = MessageTransferManager.getInstance();

  public interface uploadFileCallback {
    void onUploadFinish(boolean success, String msg);
  }

  protected ImFileMessage(ImMessageInternalInterface internalMessage) {
    super(internalMessage);
  }

  protected ImFileMessage(ImMessageInternalInterface internalMessage, String orderId) {
    super(internalMessage, orderId);
  }

  public ImFileMessage(ImMessageInternalInterface internalMessage, String orderId, String path) {
    super(internalMessage, orderId);
//    setAttribute(ImMessage.ATTR_MSG_LOCAL_FILE, path);
  }

  public abstract String getUploadFileExtensionName();

  public void uploadFile(final uploadFileCallback callback) {
    Log.e("xie", "uploadFile...");
    if (transferManager.isUploading(getMessageId())) {
      return;
    }

    transferManager.uploadStart(getMessageId());

    setStatus(ImMessageStatus.Progressing);

    String path = getUploadLocalFilePath();
    Log.e("xie", "ImFileMessage getUploadLocalFilePath  path:"+ path);
    if (TextUtils.isEmpty(path)) {
      onUploadFail("upload file path is empty");
      return;
    }
    File file = new File(path);
    if (!file.exists()) {
      onUploadFail("upload file not exist");
      return;
    }
    if (uploadManager == null) {
      uploadManager = new UploadManager();
    }
    uploadManager.put(file, null, Constants.uploadToken,
        new UpCompletionHandler() {
          @Override
          public void complete(String key, ResponseInfo respInfo,
                               JSONObject jsonData) {
            try {
              if (respInfo.isOK()) {
                // file address
                onUploadSuccess(jsonData.getString("hash"));
                callback.onUploadFinish(true, "upload file success...");
              } else {
                callback.onUploadFinish(false, "upload file err:" + respInfo.error);
              }
            } catch (Exception e) {

            }
          }

        }, null);


//    Request request = buildMultipartFormRequest(HmDataService.getInstance().getUploadEndPoint(), new File[]{file}, new String[]{"attachment"}, null);
//    httpClient.newCall(request).enqueue(new Callback() {
//      @Override
//      public void onFailure(Request request, IOException e) {
//        Log.e("xie", "onFailure..." + e.getMessage());
//        transferManager.uploadStop(getMessageId());
//        onUploadFail(e.getMessage());
//        if (callback != null) {
//          callback.onUploadFinish(false, "upload file err:" + e.getMessage());
//        }
//      }
//
//      @Override
//      public void onResponse(Response response) throws IOException {
//        transferManager.uploadStop(getMessageId());
//        String result = onUploadSuccess(response.body().string());
//        if (callback != null) {
//          Log.e("xie", "onResponse..." + response);
//          callback.onUploadFinish(TextUtils.isEmpty(result), result);
//        }
//      }
//    });
  }

  protected String onUploadSuccess(String response) {
    setStatus(ImMessageStatus.Create);
    return null;
  }

  protected void onUploadFail(String err) {

    setStatus(ImMessageStatus.Fail);
  }

  protected abstract String getDownloadUrl();

  //待上传的本地文件路径
  protected abstract String getUploadLocalFilePath();

  //本地文件缓存路径，路径应该是固定的:cacheFileDir/[conversationid]/[messsagetype]_[messageid], 这个文件并不一定实际存在
  protected abstract String getDownloadLocalFilePath();

  public void downloadFile() {
    if (transferManager.isDownloading(getMessageId())) {
      return;
    }

    transferManager.downloadStart(getMessageId());

    String remoteUri = HmDataService.getInstance().getEndPoint() + "/" + getDownloadUrl();
    final String localFileUri = getDownloadLocalFilePath();
    if (TextUtils.isEmpty(remoteUri) || TextUtils.isEmpty(localFileUri)) {
      onDownloadFail("download err.Url or file path is empty");
      return;
    }

    File file = new File(localFileUri);
    if (file != null && file.exists()) {
      return;
    }

    setStatus(ImMessageStatus.Progressing);
    final Request request = new Request.Builder()
        .url(remoteUri)
        .build();
    final Call call = httpClient.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(final Request request, final IOException e) {
        transferManager.downloadStop(getMessageId());
        onDownloadFail(e.getMessage());
      }

      @Override
      public void onResponse(Response response) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
          is = response.body().byteStream();
          File file = new File(localFileUri);
          fos = new FileOutputStream(file);
          while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
          }
          fos.flush();
          //如果下载文件成功，第一个参数为文件的绝对路径
          onDownloadSuccess();
        } catch (IOException e) {
          onDownloadFail(e.getMessage());
        } finally {
          try {
            if (is != null) is.close();
          } catch (IOException e) {
          }
          try {
            if (fos != null) fos.close();
          } catch (IOException e) {
          }
          transferManager.downloadStop(getMessageId());
        }

      }
    });
  }

  protected void onDownloadSuccess() {
    setStatus(ImMessageStatus.Success);
  }

  protected void onDownloadFail(String err) {
    setStatus(ImMessageStatus.Fail);
  }

  private Request buildMultipartFormRequest(String url, File[] files,
                                            String[] fileKeys, Map<String, String> params) {
    MultipartBuilder builder = new MultipartBuilder()
        .type(MultipartBuilder.FORM);

    if (params != null) {
      for (Map.Entry<String, String> param : params.entrySet()) {
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\""),
            RequestBody.create(null, param.getValue()));
      }
    }

    if (files != null) {
      RequestBody fileBody = null;
      for (int i = 0; i < files.length; i++) {
        File file = files[i];
        String fileName = file.getName() + getUploadFileExtensionName();
        fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
        //TODO 根据文件名设置contentType
        builder.addPart(Headers.of("Content-Disposition",
                "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
            fileBody);
      }
    }

    RequestBody requestBody = builder.build();
    return new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();
  }

  private String guessMimeType(String path) {
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String contentTypeFor = fileNameMap.getContentTypeFor(path);
    if (contentTypeFor == null) {
      contentTypeFor = "application/octet-stream";
    }
    return contentTypeFor;
  }

  protected void moveCacheFile(String srcPath) {
    if (!TextUtils.isEmpty(srcPath) && !srcPath.equals(getUploadLocalFilePath())) {
      File temp = new File(srcPath);
      if (temp.exists()) {
        File cache = new File(getUploadLocalFilePath());
        if (cache.exists()) {
          cache.delete();
        }
        if (temp.renameTo(cache)) {
//          setAttribute(ImMessage.ATTR_MSG_LOCAL_FILE, cache.getAbsolutePath());
        }
      }
    }
  }

  @Override
  public void prepareSend(final ImConversation.ImConversationSendMessagesCallBack callBack) {

    tempCachePath = getUploadLocalFilePath();
    Log.e("xie", "ImFileMessage prepareSend tempCachePath:"+tempCachePath);
    if (TextUtils.isEmpty(getContentString())) {
      uploadFile(new ImFileMessage.uploadFileCallback() {
        @Override
        public void onUploadFinish(final boolean success, String err) {
          if (callBack != null) {
            callBack.onFinish(success, ImFileMessage.this);
          }
        }
      });
    } else { //上传完成了，可以直接发送
      if (callBack != null) {
        callBack.onFinish(true, ImFileMessage.this);
      }
    }
  }

  @Override
  public void onSendFinish(boolean success) {
    //发送成功后，修改缓存文件文件名，保证文件名是最新的消息id
    if (success) {
      moveCacheFile(tempCachePath);
    }
    super.onSendFinish(success);
  }

  protected void saveTempSendData(String originLocalFilePath, String ext) {
    MessageData data = new MessageData();
    String cachePath = getDownloadLocalFilePath();
    if (TextUtils.isEmpty(cachePath) || !new File(cachePath).exists()) {
      cachePath = originLocalFilePath;
    }
    data.local = cachePath;
    data.id = getMessageId();
    data.content = getContentString();
    data.time = getTime();
    data.from = getSenderId();
    data.to = getSendToId();
    data.type = getType().name();
    data.status = getStatus().name();
    data.ext = ext;
//    data.insert();
  }

  protected void deleteTempSendData() {
//    MessageData.delete(getMessageId());
  }

//  protected MessageData getMessageData(){
//    return MessageData.getMessageData(getMessageId());
//  }

  @Override
  public void onMessageRemoved() {
    FileUtils.deleteFile(getDownloadLocalFilePath());  //删除本地缓存，不能用getLocalFilePath，那样有可能删除源文件
    deleteTempSendData();
  }
}
