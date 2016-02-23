package androidLearn.frame.easemobexample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.widget.MessageAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class EndMessageHandler extends BaseMessageUIHandler {

  private class EndViewHolder extends BaseMessageViewHolder {
    TextView textView;
  }

  public EndMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new EndViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return inflater.inflate(R.layout.row_conversation_end_message, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    EndViewHolder h = (EndViewHolder)mViewHolder;
    h.textView = (TextView) convertView.findViewById(R.id.text);
    h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    EndViewHolder holder = (EndViewHolder)mViewHolder;
    holder.textView.setText(message.getContentString());
    // 设置内容
    if (message.isSendMessage()) {
      switch (message.getStatus()) {
        case Success: // 成功
          break;
        case Progressing: // 传输中
          break;
        case Fail: // 失败
        default:
          resendTextMessage(adapter,conversation, message);
          break;
      }
    }
  }

  @Override
  public void showMenu(ImMessage message) {

  }
}
