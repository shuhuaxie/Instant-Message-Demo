package androidLearn.frame.easemobexample.widget;

import android.content.Context;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.conversation.ImUi.BaseMessageUIHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.ClinicMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.EndMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.ImageMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.ProblemMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.RecordMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.TextMessageHandler;
import androidLearn.frame.easemobexample.im.conversation.ImUi.VoiceMessageHandler;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.utils.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class MessageAdapter extends BaseAdapter{
  private Context context;
  private List<ImMessage> messageList = new LinkedList<ImMessage>();
  private ImConversation conversation;

  public MessageAdapter(Context context, String selfId, final ImConversation conversation) {
    this.context = context;
    this.conversation = conversation;
  }

  public void setMessageList(List<ImMessage> messageList) {
    this.messageList = messageList;
  }

  public List<ImMessage> getMessageList() {
    return messageList;
  }

  @Override
  public int getCount() {
    return messageList.size();
  }

  @Override
  public Object getItem(int position) {
    return messageList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ImMessage message = messageList.get(position);
    BaseMessageUIHandler uiHandler = null;

    View cacheView = null;
    if(message.getTag() != null){
      cacheView = ((WeakReference<View>) message.getTag()).get();
    }

    if (convertView == null || cacheView == null) {
      switch(message.getType()){
        case notice_text:
          uiHandler = new TextMessageHandler(context, this ,conversation);
          break;
        case notice_image:
          uiHandler = new ImageMessageHandler(context, this ,conversation);
          break;
        case notice_voice:
          uiHandler = new VoiceMessageHandler(context, this ,conversation);
          break;
        case notice_clinic:
          uiHandler = new ClinicMessageHandler(context, this ,conversation);
          break;
        case notice_record:
          uiHandler = new RecordMessageHandler(context, this ,conversation);
          break;
        case notice_end:
          uiHandler = new EndMessageHandler(context, this, conversation);
          break;
        case notice_problem:
          uiHandler = new ProblemMessageHandler(context, this, conversation);
          break;
      }
      convertView = uiHandler.createView(message);
      uiHandler.createViewHolder(message, convertView);
      convertView.setTag(uiHandler);
      message.setTag(new WeakReference<>(convertView));
    } else {
      convertView = cacheView;
      uiHandler = (BaseMessageUIHandler) convertView.getTag();
    }

    uiHandler.handleMessage(message);

    // 第一条消息或者两条消息时间离得如果稍长，显示时间
    if (position > 0 && DateUtils.isCloseEnough(message.getTime(), messageList.get(position - 1).getTime())) {
      uiHandler.showTime(false, message);
    } else {
      uiHandler.showTime(true, message);
    }

    return convertView;
  }

  public void addMessage(ImMessage message) {
    messageList.add(message);
    notifyDataSetChanged();
  }

  public void removeMessage(ImMessage message){
    conversation.removeMessage(message);
    messageList.remove(message);
    notifyDataSetChanged();
  }
}
