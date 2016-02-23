package androidLearn.frame.easemobexample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobexample.App;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.ImMessageStatus;
import androidLearn.frame.easemobexample.im.message.entity.ImImageMessage;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.utils.BitmapLoader;
import androidLearn.frame.easemobexample.utils.BitmapUtils;
import androidLearn.frame.easemobexample.utils.PathUtils;
import androidLearn.frame.easemobexample.widget.MessageAdapter;
import androidLearn.frame.easemobexample.widget.PortraitView;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ImageMessageHandler extends BaseMessageUIHandler implements View.OnClickListener, View.OnLongClickListener {

  private static int imageViewHeight = App.getInstance().getResources().getDimensionPixelSize(R.dimen.chat_image_message_height);
  private static int maxImageViewWidth = App.getInstance().getResources().getDimensionPixelSize(R.dimen.chat_image_message_max_width);

  private class ImageViewHolder extends BaseMessageViewHolder {
    ImageView iv_image;
    TextView tv_percentage;
  }

  public ImageMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new ImageViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return message.isSendMessage() ?
        inflater.inflate(R.layout.row_sent_picture, null) :
        inflater.inflate(R.layout.row_received_picture, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    ImageViewHolder h = (ImageViewHolder) mViewHolder;
    try {
      h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
      h.iv_image = ((ImageView) convertView.findViewById(R.id.iv_picture));
      h.iv_portrait = (PortraitView) convertView.findViewById(R.id.iv_userhead);
      h.tv_percentage = (TextView) convertView.findViewById(R.id.percentage);
      h.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
      h.iv_status = (ImageView) convertView.findViewById(R.id.msg_status);
    } catch (Exception e) {
    }
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    super.handleMessage(message);
    final ImageViewHolder holder = (ImageViewHolder) mViewHolder;

    if (message.getStatus() == ImMessageStatus.Progressing) {
      holder.pb.setVisibility(View.VISIBLE);
    } else {
      holder.pb.setVisibility(View.GONE);
      holder.tv_percentage.setVisibility(View.GONE);
      ImImageMessage msg = (ImImageMessage) message;

      boolean hasResized = false;
      if(msg.getThumbnailUri().startsWith("file://")){  //如果是本地文件
        BitmapUtils.Size size = BitmapUtils.getImageSize(msg.getThumbnailUri().substring(7));
        if(size.width > 0 && size.width > 0){
          setSize(holder.iv_image, (int)size.width, (int)size.height);
          hasResized = true;
        }
      }

      final boolean finalHasResized = hasResized;
      BitmapLoader.displayImage(context, msg.getThumbnailUri(), holder.iv_image, new BitmapLoader.BitmapLoadingListener() {
        @Override
        public void onLoadingStarted(String s, View view) {

        }

        @Override
        public void onLoadingFailed(String s, View view, String failReason) {
          holder.iv_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
          holder.iv_image.setImageResource(R.drawable.icon);
        }

        @Override
        public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
          if (!s.equals("drawable://" + R.drawable.icon)) {
            if (!finalHasResized)
              setSize(holder.iv_image, bitmap.getWidth(), bitmap.getHeight());
            if (!(message.isSendMessage() && message.getStatus() != ImMessageStatus.Success)) {
              final String filename = PathUtils.getPicturePathByMessageId(conversation.getConversationId(), message.getMessageId(), true);
              new Thread(new Runnable() {
                @Override
                public void run() {
                  BitmapUtils.saveBitmapToCacheFile(filename, bitmap, true, imageViewHeight);
                }
              }).start();
            }
          }
        }
      });

    }
    holder.iv_image.setTag(R.id.message_tag, message);
    holder.iv_image.setOnClickListener(this);
    holder.iv_image.setOnLongClickListener(this);

    switch (message.getStatus()) {
      case Success: // 成功
        holder.pb.setVisibility(View.GONE);
        if (holder.iv_status != null) {
          holder.iv_status.setVisibility(View.GONE);
        }
        break;
      case Fail: // 失败
        holder.pb.setVisibility(View.GONE);
        if (message.isSendMessage() && holder.iv_status != null) {
          holder.iv_status.setVisibility(View.VISIBLE);
        }
        break;
      case Progressing: // 传输中
        holder.pb.setVisibility(View.VISIBLE);
        if (holder.iv_status != null) {
          holder.iv_status.setVisibility(View.GONE);
        }
        break;
      default:
        if (message.isSendMessage()) {
          holder.pb.setVisibility(View.VISIBLE);
          holder.iv_status.setVisibility(View.GONE);
          resendTextMessage(adapter, conversation, message);
        }
        break;
    }

    if (holder.iv_status != null) {
      holder.iv_status.setTag(message);
      holder.iv_status.setOnClickListener(this);
    }
  }

  @Override
  public void showMenu(ImMessage message) {
    mMenuDialog
        .clearMenu()
        .addMenuItem(MENU_DELETE, context.getString(R.string.im_menu_text_delete), message)
        .show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.msg_status:   //发送消息失败重试
      {
        ImMessage message = (ImMessage) v.getTag();
        if (message != null) {
          if (message.isSendMessage()) {
            resendTextMessage(adapter, conversation, message);
          } else {

          }
        }
      }
      break;
      case R.id.iv_picture:    //点击图片
      {
        ImMessage message = (ImMessage) v.getTag(R.id.message_tag);
        if (message != null) {
          String filename = PathUtils.getPicturePathByMessageId(conversation.getConversationId(), message.getMessageId(), false);
//          ShowAllImageActivity.openActivity(context, new String[]{((ImImageMessage) message).getOriginUri()}, new String[]{filename}, 0);
        }
      }
      break;
    }
  }

  @Override
  public boolean onLongClick(View v) {
    switch (v.getId()) {
      case R.id.iv_picture: {
        ImMessage message = (ImMessage) v.getTag(R.id.message_tag);
        showMenu(message);
      }
      break;
    }
    return true;
  }

  private static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  private void setSize(ImageView view, int bmpWidth, int bmpHeight){
    int h = bmpHeight;
    int w = bmpWidth;
    int vh = imageViewHeight;
    int vw = w * vh / h;// + dip2px(context, (float) 7.5);
    if (vw > maxImageViewWidth * 3 / 2) { //如果图片属于长条形的，就只显示部分内容
      vw = maxImageViewWidth;
      vh = vw * 2 / 5;
      view.setScaleType(ImageView.ScaleType.CENTER_CROP);
    } else {
      view.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
    view.setLayoutParams(new RelativeLayout.LayoutParams(vw, vh));
  }
}
