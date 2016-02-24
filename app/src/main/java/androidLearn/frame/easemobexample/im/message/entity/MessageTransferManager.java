package androidLearn.frame.easemobExample.im.message.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 明帅 on 2016-01-26.
 */
class MessageTransferManager {

  private class Transfer{
    public boolean downloading;
    public boolean uploading;
  }

  private final Map<String, Transfer> mMessageList = Collections.synchronizedMap(new HashMap<String, Transfer>());
  private static MessageTransferManager instance;
  private MessageTransferManager(){}

  public static MessageTransferManager getInstance(){
    if(instance == null){
      instance = new MessageTransferManager();
    }

    return instance;
  }

  private boolean checkAllDone(Transfer transfer){
    if(transfer != null){
      return !(transfer.uploading || transfer.downloading);
    }

    return false;
  }

  public void uploadStart(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer == null){
      transfer = new Transfer();
    }
    transfer.uploading = true;
  }

  public void uploadStop(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer != null){
      if(checkAllDone(transfer)){
        mMessageList.remove(messageId);
        return;
      }

      transfer.uploading = false;
    }
  }

  public void downloadStart(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer == null){
      transfer = new Transfer();
    }
    transfer.downloading = true;
  }

  public void downloadStop(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer != null){
      if(checkAllDone(transfer)){
        mMessageList.remove(messageId);
        return;
      }

      transfer.downloading = false;
    }
  }

  public boolean isUploading(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer != null){
      return transfer.uploading;
    }

    return false;
  }

  public boolean isDownloading(String messageId){
    Transfer transfer = mMessageList.get(messageId);
    if(transfer != null){
      return transfer.downloading;
    }

    return false;
  }
}
