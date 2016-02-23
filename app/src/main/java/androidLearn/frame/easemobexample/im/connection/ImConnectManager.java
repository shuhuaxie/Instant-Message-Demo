package androidLearn.frame.easemobexample.im.connection;

import androidLearn.frame.easemobexample.App;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;

public class ImConnectManager implements ImConnectListenerManager {
  private static final List<ImConnectListener> mListenerList = new ArrayList<>();
  private ImConnectStatus status = ImConnectStatus.NETWORK_DISCONNECT;

  @Override
  public void addConnectHandler(ImConnectListener handler) {
    if (!mListenerList.contains(handler)) {
      for (int i = 0; i < mListenerList.size(); i++) {
        ImConnectListener h = mListenerList.get(i);
        if (h.getConnectListenerPriority() >= handler.getConnectListenerPriority()) {
          mListenerList.add(i, handler);
          return;
        }
      }
      mListenerList.add(handler);
    }
  }

  @Override
  public void removeConnectHandler(ImConnectListener handler) {
    if(mListenerList.contains(handler)){
      mListenerList.remove(handler);
    }
  }

  @Override
  public void clearConnectHandler() {
    mListenerList.clear();
  }

  @Override
  public ImConnectStatus getConnectStatus() {
    return status;
  }

  @Override
  public void onConnectStatusChanged(final ImConnectStatus status) {
    this.status = status;
    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListenerList != null) {
          for (ImConnectListener handler : mListenerList) {
            if (handler != null) {
              boolean result = handler.onImConnectStatusChanged(status == ImConnectStatus.CONNECTED, status);
              if (result) {
                return;
              }
            }
          }
        }
      }
    });
  }
}
