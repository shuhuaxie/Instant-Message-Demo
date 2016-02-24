package androidLearn.frame.easemobExample.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.conversation.ConversationListAdapter;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.conversation.ImConversationListener;
import androidLearn.frame.easemobExample.im.message.ImMessageListener;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChatListActivity extends Activity implements ImConversationListener, ImMessageListener, AdapterView.OnItemClickListener {

  private ConversationListAdapter mAdapter = new ConversationListAdapter();
  private ImClient mClient = App.getInstance().getImClient();
  private AtomicBoolean isLoadingData = new AtomicBoolean(false);

  @InjectView(R.id.searchtext) EditText mSearchText;
  @InjectView(R.id.clearSearch) ImageView mClearSearch;
  @InjectView(R.id.listview) ListView mListView;
  @InjectView(R.id.text_null) TextView mTextNull;
  @InjectView(R.id.listview_null) LinearLayout mListViewNull;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversation);
    ButterKnife.inject(this);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(this);
    mListView.setEmptyView(mListViewNull);
    mTextNull.setText(getString(R.string.im_conversation_null));
    mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,
                                    KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          search();
        }
        return false;
      }
    });

    mSearchText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          mClearSearch.setVisibility(View.VISIBLE);
          mTextNull.setText(getString(R.string.im_conversation_search_no_result));
        } else {
          mClearSearch.setVisibility(View.GONE);
          mTextNull.setText(getString(R.string.im_conversation_null));
        }
        search();
      }
    });

    mClient.addMessageListener(this);
    mClient.addConversationListener(this);
  }

  private synchronized void initConversationData() {

    if (isLoadingData.get()) {
      return;
    }

    isLoadingData.set(true);

    mClient.getAllConversations(new ImClient.ImClientConversationListCallBack() {
      @Override
      public void onCallBack(boolean success, List<ImConversation> list) {
        if (list != null && list.size() != mAdapter.getRealCount()) {
          mAdapter.clearAll();
          for (ImConversation conversation :
              list) {
            mAdapter.addConversation(conversation, false);
          }
          if (mSearchText != null) {
            String keyword = mSearchText.getEditableText().toString();
            mAdapter.filter(keyword);
          } else {
            //必须调用filter
            mAdapter.filter("");
          }
        }
        mAdapter.notifyDataSetChanged();
        isLoadingData.set(false);
      }
    });
  }

  @Override
  public int getConversationListenerPriority() {
    return 0;
  }

  @Override
  public boolean onConversationCreate(ImConversation conversation) {
    return false;
  }

  @Override
  public boolean onConversationListChanged() {
    initConversationData();
    return false;
  }

  @Override
  public int getMessageListenerPriority() {
    return 2;
  }

  @Override
  public boolean onMessageSent(ImMessage message, ImConversation conversation) {
    return false;
  }

  @Override
  public boolean onMessageReceived(ImMessage message, ImConversation conversation) {
    mAdapter.onMessageReceived(message, conversation);
    return false;
  }

  @Override
  public void onMessageReceipt(ImMessage message, ImConversation conversation) {

  }

  @Override
  public boolean onMessageUpdated(ImMessage message, ImConversation conversation) {
    return false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mClient.removeMessageListener(this);
    mClient.removeConversationListener(this);
  }

  public void search() {
    String keyword = mSearchText.getEditableText().toString();
    mAdapter.filter(keyword);
    mAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.clearSearch)
  public void clearSearch() {
    mSearchText.setText("");
  }

  @OnClick(R.id.back)
  public void back() {
    finish();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    String item = (String) parent.getItemAtPosition(position);
    if (item != null) {
      ChatActivity.startActivity(ChatListActivity.this, item);
    }
  }
}
