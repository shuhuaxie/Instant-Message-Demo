package androidLearn.frame.easemobexample.im.conversation.ImUi;

import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import android.view.View;


public interface IMessageUIHandler {
  public View createView(ImMessage message);
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView);
  public void handleMessage(final ImMessage message);
  public void showMenu(ImMessage message);
}
