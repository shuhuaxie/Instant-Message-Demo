package androidLearn.frame.easemobexample.im.message;

import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.data.entity.Badge;
import androidLearn.frame.easemobexample.data.entity.Event;
import androidLearn.frame.easemobexample.data.entity.Order;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.utils.UiUtils;
import android.os.Handler;
import android.os.Looper;


import java.util.ArrayList;
import java.util.List;

public class ImMessageManager implements ImMessageListenerManager {

  private static final List<ImMessageListener> mListenerList = new ArrayList<>();

  @Override
  public void addMessageHandler(ImMessageListener handler) {
    if (!mListenerList.contains(handler)) {
      for (int i = 0; i < mListenerList.size(); i++) {
        ImMessageListener h = mListenerList.get(i);
        if (h.getMessageListenerPriority() >= handler.getMessageListenerPriority()) {
          mListenerList.add(i, handler);
          return;
        }
      }
      mListenerList.add(handler);
    }
  }

  @Override
  public void removeMessageHandler(ImMessageListener handler) {
    if (mListenerList.contains(handler)) {
      mListenerList.remove(handler);
    }
  }

  @Override
  public void clearMessageHandler() {
    mListenerList.clear();
  }

  @Override
  public void onMessageReceived(final ImMessage message, final ImConversation conversation) {

    //现在已经没有ImMessageType.Notice_Clinic这个类型的消息了，下面的写法只是为了兼容，实际通过IM发送的订单目前只有ImMessageType.Notice_Consult一种
    if (message.getType() == ImMessageType.notice_clinic || message.getType() == ImMessageType.notice_record) {   //如果有新订单
//      if (PatientManager.getInstance().getPatient(message.getSenderId()) == null) {   //如果下订单的患者不在患者列表中，认为是个新患者
//        //要求更新患者列表
//        PatientListManager.getInstance().setNeedUpdate(true);
//      }
      //通知有新订单
      new Event.NewOrder(message.getType() == ImMessageType.notice_record ? Order.TYPE_CONSULT : Order.TYPE_CLINIC, message.getOrderId()).post();
    }

    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListenerList != null) {
          try {
            for (ImMessageListener handler : mListenerList) {
              if (handler != null) {
                boolean result = handler.onMessageReceived(message, conversation);
                if (result) {
                  return;
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
          //谁都没处理，弹出新消息通知
          UiUtils.showNewMessageNotification(App.getInstance(), message);
        }
      }
    });

    Badge.updateBadgeCount(Badge.BadgeType.BADGE_TYPE_IM_MESSAGE, App.getInstance().getImClient().getAllUnreadMsgCount());
  }

  @Override
  public void onMessageSent(final ImMessage message, final ImConversation conversation) {

    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListenerList != null) {
          for (ImMessageListener handler : mListenerList) {
            if (handler != null) {
              boolean result = handler.onMessageSent(message, conversation);
              if (result) {
                return;
              }
            }
          }
        }
      }
    });


  }

  @Override
  public void onMessageUpdated(final ImMessage message, final ImConversation conversation) {

    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListenerList != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              for (ImMessageListener handler : mListenerList) {
                if (handler != null) {
                  boolean result = handler.onMessageUpdated(message, conversation);
                  if (result) {
                    break;
                  }
                }
              }
            }
          });
        }
      }
    });
  }

  private void onMessageRecipt(final ImMessage message, final ImConversation conversation) {
    new Handler(App.getInstance().getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListenerList != null) {
          for (ImMessageListener handler : mListenerList) {
            if (handler != null) {
              handler.onMessageReceipt(message, conversation);
            }
          }
        }
      }
    });
  }
}
