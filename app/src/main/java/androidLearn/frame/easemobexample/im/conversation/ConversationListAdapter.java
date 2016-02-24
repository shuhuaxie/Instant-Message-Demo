package androidLearn.frame.easemobExample.im.conversation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.utils.BitmapUtils;
import androidLearn.frame.easemobExample.utils.DateUtils;
import androidLearn.frame.easemobExample.widget.PortraitView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 明帅 on 2015/8/29.
 */
public class ConversationListAdapter extends BaseAdapter {

  private List<String> list;
  private List<String> allList;
  private String mKeyWord = null;

  public ConversationListAdapter() {
    super();
    list = Collections.synchronizedList(new ArrayList<String>());
    allList = Collections.synchronizedList(new ArrayList<String>());
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return list.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(
          R.layout.list_item_conversation, null);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    final ImConversation item = App.getInstance().getImClient().getConversation(list.get(position));
    if (item != null) {
      holder.name.setText(item.getName());
//      TextPaint tp = holder.name.getPaint();
//      tp.setFakeBoldText(true);

      ImMessage message = item.getLastMessage();
      if (message != null) {
        holder.info.setText(message.getTitle());
        int fontColor = R.color.font_color_light;
        switch (message.getType()) {
          case notice_record:
          case notice_clinic:
          case notice_problem:
            fontColor = R.color.base_color;
            break;
          case notice_end:
            fontColor = R.color.font_color_red;
            break;
        }
        holder.info.setTextColor(parent.getContext().getResources().getColor(fontColor));

        holder.time.setText(DateUtils.getTimestampString(new Date(message.getTime())));
      } else {
        holder.info.setText("没有消息");
        holder.time.setText("");
      }

      int newMsgCount = item.getUnReadCount();
      if (newMsgCount > 0) {
        holder.tip.setText(newMsgCount > 99 ? "99+" : String.valueOf(newMsgCount));
        holder.tip.setVisibility(View.VISIBLE);
      } else {
        holder.tip.setVisibility(View.INVISIBLE);
      }

      BitmapUtils.showAvatar(item.getConversationId(), item.getIcon(), holder.portrait);
    }

    return convertView;
  }

  static class ViewHolder {
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.info)
    TextView info;
    @InjectView(R.id.time)
    TextView time;
    @InjectView(R.id.portrait)
    PortraitView portrait;
    @InjectView(R.id.tip)
    TextView tip;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  public void resortConversation() {
//    ImQSort sort = new ImQSort();
//    convCompare comp = new convCompare();
//    allList = sort.sort(allList, comp);
    Collections.sort(allList, new Comparator<String>() {
      public int compare(String arg0, String arg1) {
        ImMessage m1 = App.getInstance().getImClient().getConversation(arg0).getLastMessage();
        ImMessage m2 = App.getInstance().getImClient().getConversation(arg1).getLastMessage();

        if (m1 != null && m2 != null) {
          return m1.getTime() > m2.getTime() ? -1 : 1;
        } else if (m1 != null && m2 == null) {
          return -1;
        } else if (m1 == null && m2 != null) {
          return 1;
        }

        return 0;
      }
    });
    filter(mKeyWord);
  }

  private void insertConversation(ImConversation conversation) {
    ImMessage newmsg = conversation.getLastMessage();

    if (allList.size() == 0 || newmsg == null) {
      allList.add(conversation.getConversationId());
    } else {
      for (String convid :
          allList) {
        ImConversation conv = App.getInstance().getImClient().getConversation(convid);
        ImMessage oldmsg = conv.getLastMessage();

        if (oldmsg != null && newmsg != null) {
          if (newmsg.getTime() > oldmsg.getTime()) {
            allList.add(allList.indexOf(conv.getConversationId()), conversation.getConversationId());
            return;
          }
        } else if (oldmsg == null && newmsg != null) {
          allList.add(allList.indexOf(conv.getConversationId()), conversation.getConversationId());
          return;
        }
      }
      allList.add(conversation.getConversationId());
    }
  }

  public void addConversation(ImConversation conversation) {
    addConversation(conversation, true);
  }

  public void addConversation(ImConversation conversation, boolean filter) {
    if (conversation != null) {
      if (!isConversationExist(conversation)) {
        insertConversation(conversation);
      }
    }

    if (filter) {
      filter(mKeyWord);
    }
  }

  public void clearAll() {
    allList.clear();
    list.clear();
  }

  public void onMessageReceivedInBackground(ImMessage message, ImConversation conversation) {
    if (isConversationExist(conversation)) {  //如果会话存在
      conversation.setUnReadCount(conversation.getUnReadCount() + 1);
    } else {  //如果会话不存在
      conversation.setUnReadCount(conversation.getUnReadCount() + 1);
      allList.add(0, conversation.getConversationId());
    }
  }

  public void onMessageReceived(ImMessage message, ImConversation conversation) {
    if (isConversationExist(conversation)) {  //如果会话存在
      conversation.setUnReadCount(conversation.getUnReadCount() + 1);
      if (!allList.get(0).equals(conversation.getConversationId())) {
        allList.remove(conversation.getConversationId());
        allList.add(0, conversation.getConversationId());
      }
    } else {  //如果会话不存在
      conversation.setUnReadCount(conversation.getUnReadCount() + 1);
      allList.add(0, conversation.getConversationId());
    }

    filter(mKeyWord);
    notifyDataSetChanged();
  }

  private boolean isConversationExist(ImConversation conversation) {
    return allList.contains(conversation.getConversationId());
  }

  public void filter(String keyword) {
    mKeyWord = keyword;
    boolean isKeywordEmpty = TextUtils.isEmpty(keyword);

    list.clear();
    if (isKeywordEmpty) {
      list.addAll(allList);
    } else {

      for (String convid :
          allList) {
//        String name = conversation.getBuddyName();
        ImConversation conversation = App.getInstance().getImClient().getConversation(convid);
        String searchString = conversation.getSearchString();
        if (searchString != null && searchString.contains(keyword)) {
          list.add(conversation.getConversationId());
        }
      }
    }
  }

  public int getRealCount() {
    return allList.size();
  }

}
