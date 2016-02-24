package androidLearn.frame.easemobExample.im.conversation.ImUi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageStatus;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.service.AccountManager;
import androidLearn.frame.easemobExample.utils.BitmapUtils;
import androidLearn.frame.easemobExample.utils.DateUtils;
import androidLearn.frame.easemobExample.utils.UiUtils;
import androidLearn.frame.easemobExample.widget.MessageAdapter;
import androidLearn.frame.easemobExample.widget.PortraitView;
import androidLearn.frame.easemobExample.widget.menu.MenuDialog;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

public abstract class BaseMessageUIHandler implements IMessageUIHandler, MenuDialog.OnMenuClickListener {

  protected Context context;
  protected MessageAdapter adapter;
  protected ImConversation conversation;
  protected BaseMessageViewHolder mViewHolder;

  protected static final int MENU_DELETE = 0;
  protected static final int MENU_COPY = 1;
  protected MenuDialog mMenuDialog;

  protected static OkHttpClient httpClient = new OkHttpClient();

  public BaseMessageUIHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    this.context = context;
    this.adapter = adapter;
    this.conversation = conversation;
    mMenuDialog = new MenuDialog(context);
    mMenuDialog.setOnClickListener(this);
  }

  public void handleMessage(final ImMessage message) {
    if(mViewHolder != null && mViewHolder.iv_portrait !=null){
      showAvatar(message, mViewHolder.iv_portrait);
//      mViewHolder.iv_portrait.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          if (message != null && !message.isSendMessage()) {
//            PatientDetailActivity.openActivity(context, conversation.getBuddyId(), true);
//          }
//        }
//      });
    }
  }

  public void showTime(boolean show, ImMessage message) {
    if (mViewHolder.timestamp != null) {
      if (show) {
        mViewHolder.timestamp.setText(DateUtils.getTimestampString(new Date(message.getTime())));
        mViewHolder.timestamp.setVisibility(View.VISIBLE);
      } else {
        mViewHolder.timestamp.setVisibility(View.GONE);
      }
    }
  }

  protected void resendTextMessage(final MessageAdapter adapter, final ImConversation conversation, final ImMessage message) {
    if (message.getStatus() == ImMessageStatus.Fail || message.getStatus() == ImMessageStatus.Create) {
      conversation.sendMessage(message, new ImConversation.ImConversationSendMessagesCallBack() {
        @Override
        public void onFinish(boolean success, ImMessage msg) {
//          if (success) {
//            sendPush(message);
//          }
        }
      });
    }
  }

  protected void showAvatar(final ImMessage message, PortraitView view) {
    if (message.isSendMessage()) {
      BitmapUtils.showAvatar(ImClient.getUserId(), AccountManager.getInstance().getUser().avatar, view);
    } else {
      BitmapUtils.showAvatar(message.getSenderId(), message.getSenderAvatar(), view);
    }
  }

  @Override
  public void OnMenuItemClick(int id, Object obj) {
    ImMessage msg = (ImMessage) obj;
    switch (id) {
      case MENU_COPY:
        ClipboardManager clipboard = (ClipboardManager)
            context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", msg.getText());
        clipboard.setPrimaryClip(clip);
        UiUtils.showToast(context, context.getString(R.string.im_tip_copy));
        break;
      case MENU_DELETE:
        adapter.removeMessage(msg);
        break;
    }
  }
}
