package androidLearn.frame.easemobexample.im.conversation;

import androidLearn.frame.easemobexample.App;
import android.os.Handler;


import java.util.ArrayList;
import java.util.List;

public class ImConversationManager implements ImConversationListenerManager {

  private static final List<ImConversationListener> mListenerList = new ArrayList<>();

  @Override
  public void addConversationHandler(final ImConversationListener handler) {
    if(!mListenerList.contains(handler)){
      mListenerList.add(handler);
      if(App.getInstance().getImClient().isLoggedIn()){
        new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            handler.onConversationListChanged();
          }
        });
      }
    }
  }

  @Override
  public void removeConversationHandler(ImConversationListener handler) {
    mListenerList.remove(handler);
  }

  @Override
  public void clearConversationHandler() {
    mListenerList.clear();
  }

  @Override
  public void onConversationCreate(final ImConversation conversation) {
    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        for (ImConversationListener handler:
            mListenerList) {
          handler.onConversationCreate(conversation);
        }
      }
    });
  }

  @Override
  public void onConversationListChanged() {
    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        for (ImConversationListener handler:
            mListenerList) {
          handler.onConversationListChanged();
        }
      }
    });
  }
}
