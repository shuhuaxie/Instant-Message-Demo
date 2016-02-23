package androidLearn.frame.easemobexample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.widget.MessageAdapter;
import androidLearn.frame.easemobexample.widget.PortraitView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class TextMessageHandler extends BaseMessageUIHandler implements View.OnClickListener, View.OnLongClickListener {

  private class TextViewHolder extends BaseMessageViewHolder {
    TextView textView;
  }

  public TextMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new TextViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return message.isSendMessage() ?
        inflater.inflate(R.layout.row_sent_message, null) :
        inflater.inflate(R.layout.row_received_message, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    TextViewHolder h = (TextViewHolder)mViewHolder;
    try {
      h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
      h.iv_portrait = (PortraitView) convertView.findViewById(R.id.iv_userhead);
      h.textView = (TextView) convertView.findViewById(R.id.tv_chatcontent);
      h.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
      h.iv_status = (ImageView) convertView.findViewById(R.id.msg_status);
    } catch (Exception e) {
    }
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    super.handleMessage(message);
    TextViewHolder holder = (TextViewHolder)mViewHolder;
    // 设置内容
    String msg = message.getText();
    if (msg != null && msg.length() < 10) {
      holder.textView.setGravity(Gravity.CENTER);
    } else {
      holder.textView.setGravity(Gravity.CENTER_VERTICAL);
    }
    holder.textView.setText(msg);

    if (message.isSendMessage()) {
      switch (message.getStatus()) {
        case Success: // 成功
          holder.pb.setVisibility(View.GONE);
          holder.iv_status.setVisibility(View.GONE);
          break;
        case Fail: // 失败
          holder.pb.setVisibility(View.GONE);
          holder.iv_status.setVisibility(View.VISIBLE);
          break;
        case Progressing: // 传输中
          holder.pb.setVisibility(View.VISIBLE);
          holder.iv_status.setVisibility(View.GONE);
          break;
        default:
          holder.pb.setVisibility(View.VISIBLE);
          holder.iv_status.setVisibility(View.GONE);
          resendTextMessage(adapter,conversation, message);
          break;
      }
    }

    if (holder.iv_status != null) {
      holder.iv_status.setTag(message);
      holder.iv_status.setOnClickListener(this);
    }
    holder.textView.setTag(message);
    holder.textView.setOnLongClickListener(this);
  }

  @Override
  public void showMenu(ImMessage message) {
    mMenuDialog
        .clearMenu()
        .addMenuItem(MENU_COPY, context.getString(R.string.im_menu_text_copy), message)
        .addMenuItem(MENU_DELETE, context.getString(R.string.im_menu_text_delete), message)
        .show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.msg_status:   //发送消息失败重试
      {
        ImMessage message = (ImMessage) v.getTag();
        if (message != null && message.isSendMessage()) {
          resendTextMessage(adapter, conversation, message);
        }
      }
      break;
    }
  }

  @Override
  public boolean onLongClick(View v) {
    switch (v.getId()){
      case R.id.tv_chatcontent:
      {
        ImMessage message = (ImMessage) v.getTag();
        showMenu(message);
      }
      break;
      case R.id.iv_userhead:    //点击头像
      {
        ImMessage message = (ImMessage) v.getTag();
        if (message != null && !message.isSendMessage()) {
//          PatientDetailActivity.openActivity(context, conversation.getBuddyId(), true);
        }
      }
      break;
    }
    return true;
  }
}
