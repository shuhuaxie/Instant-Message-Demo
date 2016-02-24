package androidLearn.frame.easemobExample.im.conversation.ImUi;

import android.content.Context;

import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageType;
import androidLearn.frame.easemobExample.im.message.entity.ImConsultMessage;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.widget.MessageAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RecordMessageHandler extends BaseMessageUIHandler implements View.OnClickListener {

  ConsultImageAdapter adapter;
  String name;
  String age;
  int sex;
  String detail;
  boolean hasImage = false;

  private class RecordViewHolder extends BaseMessageViewHolder {
    TextView tv_name;
    TextView tv_age;
    ImageView image_sex;
    TextView tv_content;
    ImageView[] iv_image;
    View layout_img;
    View layout_message;
    RecyclerView recyclerView;
  }

  public RecordMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new RecordViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return inflater.inflate(R.layout.row_medical_record_message, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    RecordViewHolder h = (RecordViewHolder)mViewHolder;
    try {
      h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
      h.tv_name = (TextView) convertView.findViewById(R.id.name);
      h.tv_age = (TextView) convertView.findViewById(R.id.age);
      h.image_sex = (ImageView) convertView.findViewById(R.id.sex);
      h.tv_content = (TextView) convertView.findViewById(R.id.content);
      h.iv_image = new ImageView[5];
      h.iv_image[0] = (ImageView) convertView.findViewById(R.id.img1);
      h.iv_image[1] = (ImageView) convertView.findViewById(R.id.img2);
      h.iv_image[2] = (ImageView) convertView.findViewById(R.id.img3);
      h.iv_image[3] = (ImageView) convertView.findViewById(R.id.img4);
      h.iv_image[4] = (ImageView) convertView.findViewById(R.id.img5);
      h.layout_img = convertView.findViewById(R.id.layout_img);
      h.layout_message = convertView.findViewById(R.id.layout_message);

      ImConsultMessage.ConsultData data = ((ImConsultMessage)message).getConsultData();
      if(data != null){
        name = data.name;
        age = String.valueOf(data.age);
        if("male".equals(data.gender)){
          sex = R.drawable.male;
        }
        else{
          sex = R.drawable.female;
        }
        detail = data.detail;
      }

//      Map<String, Object> data = (Map<String, Object>) message.getContent();
//      if(data != null) {
//        //姓名
//        name = (String) data.get("name");
//        //年龄
//        Object age_o = data.get("age");
//        if(age_o instanceof Double){
//          age = String.valueOf((int)((double) age_o));
//        }
//        else if(age_o instanceof String){
//          age = (String)age_o;
//        }
//        //性别
//        if("male".equals((String)data.get("gender"))){
//          sex = R.drawable.male;
//        }
//        else{
//          sex = R.drawable.female;
//        }
//        //详情
//        detail = (String) data.get("detail");
//      }

      //以下都是图片处理
      h.recyclerView = (RecyclerView) convertView.findViewById(R.id.recyclerview);
      //设置recyclerView布局管理器
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(convertView.getContext());
      linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
      h.recyclerView.setLayoutManager(linearLayoutManager);
      List<String> thumbnails = new ArrayList<>();
      List<String> bigImages = new ArrayList<>();
      if(data != null){
        for (ImConsultMessage.ConsultData.Attachments attachment:
             data.attachments) {
          thumbnails.add(attachment.thumbnail);
          bigImages.add(attachment.origin);
        }
        hasImage = data.attachments.length > 0;
//        List<Map<String,String>> pics = (List<Map<String,String>>) data.get("attachments");
//        hasImage = pics.size() > 0;
//        for (Map<String,String> map:
//             pics) {
//          thumbnails.add(map.get("thumbnail"));
//          bigImages.add(map.get("origin"));
//        }
      }
      adapter = new ConsultImageAdapter(convertView.getContext(), thumbnails, bigImages);
      h.recyclerView.setAdapter(adapter);

    } catch (Exception e) {
    }
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    RecordViewHolder holder = (RecordViewHolder)mViewHolder;
    // 设置内容
    if (message.getType() == ImMessageType.notice_record) {
      if(((ImConsultMessage)message).getConsultData() != null){
        //姓名
        holder.tv_name.setText(name);
        //年龄
        holder.tv_age.setText(age);
        //性别
        holder.image_sex.setImageResource(sex);
        //详情
        holder.tv_content.setText(detail);
        //图片
        if(hasImage){
          holder.layout_img.setVisibility(View.VISIBLE);
        }
        else{
          holder.layout_img.setVisibility(View.GONE);
        }
      }

      if(!TextUtils.isEmpty(message.getOrderId())){
        holder.layout_message.setTag(message.getOrderId());
        holder.layout_message.setOnClickListener(this);
      }
    }
  }

  @Override
  public void showMenu(ImMessage message) {

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.img1:
      case R.id.img2:
      case R.id.img3:
      case R.id.img4:
      case R.id.img5: {

        String []urls = (String[])v.getTag();
        int index = (int) v.getTag(0xff000001);
//        ShowAllImageActivity.openActivity(context, urls, null, index);

      }
      break;

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
