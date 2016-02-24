package androidLearn.frame.easemobExample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImProblemMessage;
import androidLearn.frame.easemobExample.widget.MessageAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ProblemMessageHandler extends BaseMessageUIHandler{

  private final static int COUNT = 5;

  class childView{
    View view;
    TextView text;
    View divider;
  }

  private class RecordViewHolder extends BaseMessageViewHolder {
    childView childViews[];
  }

  public ProblemMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new RecordViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return inflater.inflate(R.layout.row_problem_message, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    RecordViewHolder h = (RecordViewHolder)mViewHolder;
    try {
      h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
      int res[] = {R.id.problem1,R.id.problem2,R.id.problem3,R.id.problem4,R.id.problem5,};
      h.childViews = new childView[COUNT];
      for(int i = 0; i < h.childViews.length && i < COUNT; i++){
        h.childViews[i] = new childView();
        h.childViews[i].view = convertView.findViewById(res[i]);
        h.childViews[i].text = (TextView) h.childViews[i].view.findViewById(R.id.text);
        h.childViews[i].divider = h.childViews[i].view.findViewById(R.id.divider);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    RecordViewHolder holder = (RecordViewHolder)mViewHolder;
    // 设置内容
    if (message.getType() == ImMessageType.notice_problem) {
      List<String> data = ((ImProblemMessage)message).getProblems();
      holder.childViews[0].divider.setVisibility(View.GONE);
      if(data != null){
        for(int i = 0; i < COUNT; i ++){
          if(i < data.size()){
            holder.childViews[i].view.setVisibility(View.VISIBLE);
            holder.childViews[i].text.setText((i+1) + "、" + data.get(i));
          }
          else{
            holder.childViews[i].view.setVisibility(View.GONE);
          }
        }
      }
    }
  }

  @Override
  public void showMenu(ImMessage message) {

  }
}
