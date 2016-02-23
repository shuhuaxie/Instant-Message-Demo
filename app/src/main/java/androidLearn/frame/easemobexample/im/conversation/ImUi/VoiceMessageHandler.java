package androidLearn.frame.easemobexample.im.conversation.ImUi;

import android.content.Context;
import androidLearn.frame.easemobexample.R;
import androidLearn.frame.easemobexample.im.conversation.ImConversation;
import androidLearn.frame.easemobexample.im.message.entity.ImAudioMessage;
import androidLearn.frame.easemobexample.im.message.entity.ImMessage;
import androidLearn.frame.easemobexample.widget.MessageAdapter;
import androidLearn.frame.easemobexample.widget.PlayButton;
import androidLearn.frame.easemobexample.widget.PortraitView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VoiceMessageHandler extends BaseMessageUIHandler implements View.OnClickListener, View.OnLongClickListener {

  private class VoiceViewHolder extends BaseMessageViewHolder {
    PlayButton btn_play;
    LinearLayout layout_voice;
    TextView tv_duration;
  }

  public VoiceMessageHandler(Context context, MessageAdapter adapter, ImConversation conversation) {
    super(context, adapter, conversation);
    mViewHolder = new VoiceViewHolder();
  }

  @Override
  public View createView(ImMessage message) {
    LayoutInflater inflater = LayoutInflater.from(context);
    return message.isSendMessage() ?
        inflater.inflate(R.layout.row_sent_voice, null) :
        inflater.inflate(R.layout.row_received_voice, null);
  }

  @Override
  public BaseMessageViewHolder createViewHolder(ImMessage message, View convertView) {
    VoiceViewHolder h = (VoiceViewHolder) mViewHolder;
    try {
      h.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
      h.btn_play = (PlayButton) convertView.findViewById(R.id.iv_voice);
      h.iv_portrait = (PortraitView) convertView.findViewById(R.id.iv_userhead);
      h.tv_duration = (TextView) convertView.findViewById(R.id.tv_length);
      h.layout_voice = (LinearLayout) convertView.findViewById(R.id.layout_voice);
      h.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
      h.iv_status = (ImageView) convertView.findViewById(R.id.msg_status);
    } catch (Exception e) {
    }
    return h;
  }

  @Override
  public void handleMessage(final ImMessage message) {
    super.handleMessage(message);
    final VoiceViewHolder holder = (VoiceViewHolder) mViewHolder;
    // 设置内容
    double duration = ((ImAudioMessage) message).getDuration();
    holder.btn_play.setPath(((ImAudioMessage) message).getAudioUri());
    holder.tv_duration.setText((int) duration + "\"");
    float MAX_VOICE_LENGTH = (float) 60.0;
    float weight = duration > MAX_VOICE_LENGTH ? 90 : (float) (duration * 90 / MAX_VOICE_LENGTH);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layout_voice.getLayoutParams();
    params.weight = weight;
    holder.layout_voice.setLayoutParams(params);
    holder.layout_voice.setTag(message);
    holder.layout_voice.setOnClickListener(this);
    holder.layout_voice.setOnLongClickListener(this);

    switch (message.getStatus()) {
      case Success: // 成功
        holder.btn_play.setEnabled(true);
        holder.pb.setVisibility(View.GONE);
        holder.iv_status.setVisibility(View.GONE);
        break;
      case Fail: // 失败
        holder.pb.setVisibility(View.GONE);
        holder.iv_status.setVisibility(View.VISIBLE);
        if (!message.isSendMessage()) {
          holder.btn_play.setEnabled(false);
        }
        break;
      case Progressing: // 传输中
        holder.btn_play.setEnabled(false);
        holder.pb.setVisibility(View.VISIBLE);
        holder.iv_status.setVisibility(View.GONE);
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
            ((ImAudioMessage) message).downloadFile();
          }
        }
      }
      break;
      case R.id.layout_voice: //播放语音
      {
        VoiceViewHolder h = (VoiceViewHolder) mViewHolder;
        ImMessage message = (ImMessage) v.getTag();
        h.btn_play.setPath(((ImAudioMessage) message).getAudioUri());
        h.btn_play.performClick();
      }
      break;
    }
  }

  @Override
  public boolean onLongClick(View v) {
    switch (v.getId()) {
      case R.id.layout_voice: {
        VoiceViewHolder h = (VoiceViewHolder) mViewHolder;
        h.btn_play.stopPlay();
        ImMessage message = (ImMessage) v.getTag();
        showMenu(message);
      }
      break;
    }
    return true;
  }
}
