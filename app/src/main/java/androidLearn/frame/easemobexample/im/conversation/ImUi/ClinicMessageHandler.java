package androidLearn.frame.easemobExample.im.conversation.ImUi;

import android.content.Context;

import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.im.message.entity.ImClinicMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.widget.EllipsizingTextView;
import androidLearn.frame.easemobExample.widget.MessageAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class ClinicMessageHandler extends BaseMessageUIHandler implements View.OnClickListener {

  private class AppointmentViewHolder extends BaseMessageViewHolder {
    TextView tv_title;
    TextView tv_content;
    EllipsizingTextView tv_detail;
    TextView tv_link;
    View layout_message;
  }

  public ClinicMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new AppointmentViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return inflater.inflate(R.layout.row_appointment_message, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    AppointmentViewHolder h = (AppointmentViewHolder)mViewHolder;
    h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
    h.tv_title = (TextView) convertView.findViewById(R.id.title_tv);
    h.tv_content = (TextView) convertView.findViewById(R.id.content);
    h.tv_detail = (EllipsizingTextView) convertView.findViewById(R.id.detail);
    h.tv_link = (TextView) convertView.findViewById(R.id.link);
    h.layout_message = convertView.findViewById(R.id.layout_message);
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    AppointmentViewHolder holder = (AppointmentViewHolder)mViewHolder;
    // 设置内容
    if (message.getType() == ImMessageType.notice_clinic) {
      ImClinicMessage.ClinicData data = ((ImClinicMessage)message).getClinicData();
      if(data != null){
        holder.tv_title.setText(data.title);

        final String content = (data.content).replace("<br>", "\n");
        holder.tv_content.setText(content);
        holder.tv_detail.setText(data.detail);
        final String link_text = data.link_text;
        if (!TextUtils.isEmpty(link_text)) {
          holder.tv_link.setText(link_text);
        }
        if(!TextUtils.isEmpty(data.order_id)){
          holder.layout_message.setTag(data.order_id);
          holder.layout_message.setOnClickListener(this);
        }
      }
//      Map<String, Object> data = (Map<String, Object>) message.getContent();
//      if (data != null) {
//        holder.tv_title.setText((String) data.get("title"));
//
//        final String content = ((String) data.get("content")).replace("<br>", "\n");
//        holder.tv_content.setText(content);
//        holder.tv_detail.setText((String) data.get("detail"));
//        final String link_text = (String) data.get("link_text");
//        if (!TextUtils.isEmpty(link_text)) {
//          holder.tv_link.setText(link_text);
//        }
//        Object order = data.get("order_id");
//        String order_id = "";
//        if(order instanceof String){
//          order_id = (String) data.get("order_id");
//        }
//        if(!TextUtils.isEmpty(order_id)){
//          holder.layout_message.setTag(order_id);
//          holder.layout_message.setOnClickListener(this);
//        }
//
//      }
    }
  }

  @Override
  public void showMenu(ImMessage message) {

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){

      case R.id.layout_message:
        String order_id = (String)v.getTag();
        if(!TextUtils.isEmpty(order_id)){
//          Intent intent = new Intent(context, OrderDetailActivity.class);
//          intent.putExtra(OrderDetailActivity.EXTRA_ID, order_id);
//          context.startActivity(intent);
        }
        break;

    }
  }
}
