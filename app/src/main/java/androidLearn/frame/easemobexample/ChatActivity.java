package androidLearn.frame.easemobExample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.conversation.ImConversation;
import androidLearn.frame.easemobExample.im.message.ImMessageListener;
import androidLearn.frame.easemobExample.im.message.entity.ImMessage;
import androidLearn.frame.easemobExample.utils.UiUtils;
import androidLearn.frame.easemobExample.widget.MessageAdapter;
import androidLearn.frame.easemobExample.widget.PlayButton;
import androidLearn.frame.easemobExample.widget.ProgressDialogFragment;
import androidLearn.frame.easemobExample.widget.RecordButton;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.VoiceRecorder;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class ChatActivity extends Activity  implements ImMessageListener, View.OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
  private static final String EXTRA_CONVERSATION_ID = "conversation_id";
  //  private static final String EXTRA_BUDDY_ID = "buddy_id";
  private static final String TAG = ChatActivity.class.getSimpleName();
  static final int PAGE_SIZE = 10;

  private static final int ACTIVITY_RESULT_PICK_IMAGE = 1;
  private static final int ACTIVITY_RESULT_PICK_PHRASE = 2;

  @InjectView(R.id.title_tv) TextView title;
  @InjectView(R.id.listview) ListView listview;
  @InjectView(R.id.btn_set_mode_voice) View btnSetModeVoice;
  @InjectView(R.id.btn_set_mode_keyboard) View btnSetModeKeyboard;
  @InjectView(R.id.btn_press_to_speak) RecordButton btnPressToSpeak;
  @InjectView(R.id.et_sendmessage) EditText etSendmessage;
  //  @InjectView(R.id.iv_emoticons_normal) ImageView ivEmoticonsNormal;
//  @InjectView(R.id.iv_emoticons_checked) ImageView ivEmoticonsChecked;
  @InjectView(R.id.edittext_layout) RelativeLayout edittextLayout;
  @InjectView(R.id.btn_more) ImageView btnMore;
  @InjectView(R.id.btn_send) View btnSend;
  @InjectView(R.id.rl_bottom) LinearLayout rlBottom;
  @InjectView(R.id.vPager) ViewPager vPager;
  @InjectView(R.id.ll_face_container) LinearLayout llFaceContainer;
  @InjectView(R.id.btn_take_picture) View btnTakePicture;
  @InjectView(R.id.btn_picture) View btnPicture;
  @InjectView(R.id.btn_location) View btnLocation;
  @InjectView(R.id.btn_close) View btnClose;
  @InjectView(R.id.ll_btn_container) LinearLayout llBtnContainer;
  @InjectView(R.id.more) LinearLayout more;
  @InjectView(R.id.bar_bottom) LinearLayout barBottom;
  @InjectView(R.id.back) View back;
  @InjectView(R.id.chat_swipe_layout) SwipeRefreshLayout chatSwipeLayout;
  @InjectView(R.id.btn_word) View btnWord;

  private VoiceRecorder voiceRecorder;

  private Drawable[] micImages;

  private ImConversation conversation;
  MessageAdapter mMessageAdapter;

  private AtomicBoolean isLoadingMessages = new AtomicBoolean(false);

  private ImClient mClient;

  private PowerManager.WakeLock mWakeLock;
  private boolean mIsShowing = false;

  private ProgressDialogFragment mProgressDialog;


  public static boolean startActivity(Context context, String conversationId) {
    //目前没有群聊，会话id和buddyid是同一个东西
    ImConversation conversation = App.getInstance().getImClient().getConversation(conversationId);
    return startActivityWithBuddy(context, conversation);
  }

  private static AtomicBoolean isActvitiyStarting = new AtomicBoolean(false);

  private static boolean startActivityWithBuddy(final Context context, ImConversation conversation) {
    if (isActvitiyStarting.get()) {
      return false;
    }
    isActvitiyStarting.set(true);
    if (conversation != null) {

      UiUtils.cancelNewMessageNotification(context);
      conversation.setUnReadCount(0);

      final Intent intent = new Intent(context, ChatActivity.class);
      intent.putExtra(EXTRA_CONVERSATION_ID, conversation.getConversationId());
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

      context.startActivity(intent);
      isActvitiyStarting.set(false);
      return true;
    }
    isActvitiyStarting.set(false);
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    ButterKnife.inject(this);

    //android4.4 theme中使用了<item name="android:windowTranslucentStatus">true</item>后会导致edittext被输入法覆盖，这是个系统的bug，
    // 为了绕过这个bug，先在activity根视图中加上android:fitsSystemWindows="true"，再使用以下代码
    SystemBarTintManager tintManager = new SystemBarTintManager(this);
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setTintColor(getResources().getColor(R.color.title_bar_color));

    mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
        .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

    intiView();
    updateOrder();
  }

  private void intiView() {

    mClient = App.getInstance().getImClient();

    final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
    conversation = mClient.getConversation(conversationId);

    mMessageAdapter = new MessageAdapter(ChatActivity.this, ImClient.getUserId(), conversation);
    listview.setAdapter(mMessageAdapter);

    title.setText(conversation.getName());

    etSendmessage.requestFocus();
    etSendmessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      @Override
      public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        scrollToLast();
      }
    });
    etSendmessage.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        more.setVisibility(View.GONE);
//        ivEmoticonsNormal.setVisibility(View.VISIBLE);
//        ivEmoticonsChecked.setVisibility(View.INVISIBLE);
        llFaceContainer.setVisibility(View.GONE);
        llBtnContainer.setVisibility(View.GONE);
      }
    });
    etSendmessage.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before,
                                int count) {
        if (!TextUtils.isEmpty(s)) {
          btnMore.setVisibility(View.GONE);
          btnSend.setVisibility(View.VISIBLE);
        } else {
          btnMore.setVisibility(View.VISIBLE);
          btnSend.setVisibility(View.GONE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    btnSend.setOnClickListener(this);
    btnSetModeVoice.setOnClickListener(this);
    btnSetModeKeyboard.setOnClickListener(this);
    btnMore.setOnClickListener(this);
    btnPicture.setOnClickListener(this);
    btnClose.setOnClickListener(this);
    back.setOnClickListener(this);
    btnWord.setOnClickListener(this);

    chatSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
        android.R.color.holo_orange_light, android.R.color.holo_red_light);
    chatSwipeLayout.setOnRefreshListener(this);

    initRecordBtn();

    loadMessagesWhenInit();

    mClient.addMessageListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mIsShowing = true;
    conversation.setUnReadCount(0);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mIsShowing = false;
    PlayButton.stopAllPlay();
  }

  private void loadMessagesWhenInit() {
    if (isLoadingMessages.get()) {
      return;
    }
    isLoadingMessages.set(true);
    conversation.getMessages(PAGE_SIZE, new ImConversation.ImConversationGetMessagesCallBack() {

      @Override
      public void onFinish(boolean success, List<ImMessage> list) {
        if (success) {
          //保证orderid的正确性，现在还不知道为什么有时候orderid会是上一个order的
          if (list != null && list.size() > 0) {
            conversation.setOrderId(list.get(list.size() - 1).getOrderId());
          }
          mMessageAdapter.setMessageList(list);
          mMessageAdapter.notifyDataSetChanged();
          scrollToLast();
        }
        isLoadingMessages.set(false);
      }
    });

  }

  private void loadOldMessages() {
    if (isLoadingMessages.get()) {
      return;
    } else {
      isLoadingMessages.set(true);
      ImMessage firstMsg = mMessageAdapter.getMessageList().get(0);
      conversation.getMessages(firstMsg.getToken(), PAGE_SIZE, new ImConversation.ImConversationGetMessagesCallBack() {

        @Override
        public void onFinish(boolean success, List<ImMessage> list) {
          if (success) {
            if (list.size() == 0) {
              UiUtils.showToast(ChatActivity.this, "无更早的消息了");
            } else {
              List<ImMessage> newMessages = new ArrayList<>();
              newMessages.addAll(list);
              newMessages.addAll(mMessageAdapter.getMessageList());
              mMessageAdapter.setMessageList(newMessages);
              mMessageAdapter.notifyDataSetChanged();
              listview.setSelection(list.size() - 1);
            }
          }
          isLoadingMessages.set(false);
        }
      });
    }
  }

  private void scrollToLast() {
    // 为了版本兼容性，再加一层Handler处理，确保在UI线程完成此操作（尤其是接收到新消息后）。
    // 实在难以理解某些系统版本中bug。。。
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        listview.setSelection(listview.getBottom());
      }
    });
  }

  @Override
  protected void onDestroy() {
    mClient.removeMessageListener(this);
    super.onDestroy();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.back:
        finish();
        break;
      case R.id.btn_send:
        sendText();
        break;
      case R.id.btn_set_mode_voice:
        setModeVoice();
        break;
      case R.id.btn_set_mode_keyboard:
        setModeKeyboard(true);
        break;
      case R.id.btn_more:
        more();
        edittextLayout.setVisibility(View.VISIBLE);
        btnSetModeKeyboard.setVisibility(View.GONE);
        btnSetModeVoice.setVisibility(View.VISIBLE);
        btnPressToSpeak.setVisibility(View.GONE);
        etSendmessage.requestFocus();
        break;
      case R.id.btn_picture:
        pickImage();
        more();
        break;
      case R.id.btn_close: {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
////        builder.setTitle(R.string.attach_clsoe); //设置标题
//        builder.setMessage(R.string.im_conversation_end_tip); //设置内容
////        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { //设置确定按钮
//          @Override
//          public void onClick(DialogInterface dialog, int which) {
//            dialog.dismiss(); //关闭dialog
//            endConversation();
//          }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { //设置取消按钮
//          @Override
//          public void onClick(DialogInterface dialog, int which) {
//            dialog.dismiss();
//          }
//        });
//        builder.create().show();
      }
      break;
      case R.id.btn_word: {
//        Intent intent = new Intent(this, CommonPhraseActivity.class);
//        intent.putExtra(CommonPhraseActivity.EXTRA_IS_NEED_RETURN, true);
//        startActivityForResult(intent, ACTIVITY_RESULT_PICK_PHRASE);
      }
      break;
      default:
        break;
    }
  }

  private void endConversation() {
    more();

    final String orderId = conversation.getOrderId();
    //mProgressDialog = UiUtils.showProgressDialog(this, getString(R.string.waiting));
    //
  }

  private void sendText() {
    String txt = etSendmessage.getText().toString();
    if (!TextUtils.isEmpty(txt)) {
      ImMessage message = conversation.createTextMessage(txt, new ImConversation.ImConversationSendMessagesCallBack() {

        @Override
        public void onFinish(boolean success, ImMessage data) {
          mMessageAdapter.notifyDataSetChanged();
        }
      });
      if (message != null) {
        mMessageAdapter.addMessage(message);
        finishSend();
      }
    }
  }

  private void sendImage(String path) {
    if (!TextUtils.isEmpty(path)) {
      ImMessage message = conversation.createImageMessage(path, new ImConversation.ImConversationSendMessagesCallBack() {
        @Override
        public void onFinish(boolean success, ImMessage msg) {
          mMessageAdapter.notifyDataSetChanged();
        }
      });
      if (message != null) {
        mMessageAdapter.addMessage(message);
        scrollToLast();
      }
    }
  }

  private void finishSend() {
    etSendmessage.setText(null);
    scrollToLast();
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    if (scrollState == SCROLL_STATE_IDLE) {
      if (view.getChildCount() > 0) {
        View first = view.getChildAt(0);
        if (first != null && view.getFirstVisiblePosition() == 0 && first.getTop() == 0) {
          loadOldMessages();
        }
      }
    }
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

  }

  @Override
  public void onRefresh() {

    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        if (listview.getChildCount() > 0) {
          View first = listview.getChildAt(0);
          if (first != null && listview.getFirstVisiblePosition() == 0 && first.getTop() == 0) {
            loadOldMessages();
          }
        }
        chatSwipeLayout.setRefreshing(false);
      }
    }, 500);

  }

  @Override
  public int getMessageListenerPriority() {
    return 0;
  }

  @Override
  public boolean onMessageSent(ImMessage message, ImConversation conversation) {
    return false;
  }

  @Override
  public boolean onMessageReceived(ImMessage message, ImConversation conversation) {
    if (conversation.getConversationId().equals(ChatActivity.this.conversation.getConversationId())) {
      conversation.setUnReadCount(0);
      mMessageAdapter.addMessage(message);
      scrollToLast();
      return mIsShowing;
    }

    return false;
  }

  @Override
  public void onMessageReceipt(ImMessage message, ImConversation conversation) {

  }

  @Override
  public boolean onMessageUpdated(ImMessage message, ImConversation conversation) {
    if (conversation.getConversationId().equals(ChatActivity.this.conversation.getConversationId())) {
//      mMessageAdapter.notifyDataSetChanged();
      //只更新一行
      updateSingleRow(listview, message);
    }
    return false;
  }

  /**
   * 隐藏软键盘
   */
  private void hideKeyboard() {
    if (getCurrentFocus() != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(getCurrentFocus()
          .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  /**
   * 显示语音图标按钮
   */
  public void setModeVoice() {
    hideKeyboard();
    edittextLayout.setVisibility(View.GONE);
    more.setVisibility(View.GONE);
    btnSetModeVoice.setVisibility(View.GONE);
    btnSetModeKeyboard.setVisibility(View.VISIBLE);
    btnSend.setVisibility(View.GONE);
    btnMore.setVisibility(View.VISIBLE);
    btnPressToSpeak.setVisibility(View.VISIBLE);
//    ivEmoticonsNormal.setVisibility(View.VISIBLE);
//    ivEmoticonsChecked.setVisibility(View.INVISIBLE);
    llBtnContainer.setVisibility(View.VISIBLE);
    llFaceContainer.setVisibility(View.GONE);
  }

  /**
   * 显示键盘图标
   */
  public void setModeKeyboard(boolean showSoftInput) {
    edittextLayout.setVisibility(View.VISIBLE);
    more.setVisibility(View.GONE);
    btnSetModeKeyboard.setVisibility(View.GONE);
    btnSetModeVoice.setVisibility(View.VISIBLE);
    etSendmessage.requestFocus();
    btnPressToSpeak.setVisibility(View.GONE);
    if (TextUtils.isEmpty(etSendmessage.getText())) {
      btnMore.setVisibility(View.VISIBLE);
      btnSend.setVisibility(View.GONE);
    } else {
      btnMore.setVisibility(View.GONE);
      btnSend.setVisibility(View.VISIBLE);
    }
    if(showSoftInput){
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
  }

  /**
   * 显示或隐藏图标按钮页
   */
  public void more() {
    if (more.getVisibility() == View.GONE) {
      hideKeyboard();
      more.setVisibility(View.VISIBLE);
      llBtnContainer.setVisibility(View.VISIBLE);
      llFaceContainer.setVisibility(View.GONE);
    } else {
      if (llFaceContainer.getVisibility() == View.VISIBLE) {
        llFaceContainer.setVisibility(View.GONE);
        llBtnContainer.setVisibility(View.VISIBLE);
//        ivEmoticonsNormal.setVisibility(View.VISIBLE);
//        ivEmoticonsChecked.setVisibility(View.INVISIBLE);
      } else {
        more.setVisibility(View.GONE);
      }
    }

  }

  public void initRecordBtn() {
//    btnPressToSpeak.setSavePath(PathUtils.getRecordPathByCurrentTime(conversation.getConversationId()));
    btnPressToSpeak.setConversationId(conversation.getConversationId());
    btnPressToSpeak.setRecordEventListener(new RecordButton.RecordEventListener() {
      @Override
      public void onFinishedRecord(final String audioPath, int secs) {
        ImMessage message = conversation.createAudioMessage(audioPath, secs, new ImConversation.ImConversationSendMessagesCallBack() {
          @Override
          public void onFinish(boolean success, ImMessage msg) {
            mMessageAdapter.notifyDataSetChanged();
            scrollToLast();
          }
        });
        if (message != null) {
          mMessageAdapter.addMessage(message);
        }
      }

      @Override
      public void onStartRecord() {

      }
    });
  }

  private void pickImage() {
    PhotoPickerIntent intent = new PhotoPickerIntent(this);
    intent.setPhotoCount(1);
    intent.setShowCamera(true);
    intent.setShowGif(false);
    startActivityForResult(intent, ACTIVITY_RESULT_PICK_IMAGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
//        case ACTIVITY_RESULT_PICK_PHRASE: {
//          if(data != null){
//            String phrase = data.getStringExtra(CommonPhraseActivity.EXTRA_LOCATION);
//            etSendmessage.setText(phrase);
//            etSendmessage.setSelection(etSendmessage.getEditableText().toString().length());
//          }
//        }
//        break;
        case ACTIVITY_RESULT_PICK_IMAGE: {
          if (data != null) {
            ArrayList<String> photos =
                data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            String path = photos.get(0);
            if (!TextUtils.isEmpty(path)) {
              sendImage(path);
            }
          }
        }
        break;
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (more.getVisibility() == View.VISIBLE) {
      more();
    } else {
      super.onBackPressed();
    }
  }

  private void updateOrder(){
    if(conversation != null){
//      HmDataService.getInstance().getOrderById(conversation.getOrderId()).
//          observeOn(AndroidSchedulers.mainThread())
//          .subscribe(
//              new Action1<OrderInfoResponse>() {
//                @Override
//                public void call(OrderInfoResponse orderInfo) {
//                  if (orderInfo != null && orderInfo.data != null && orderInfo.data.order != null) {
//                    orderInfo.data.order.setReaded();
//                  }
//                }
//              },
//              new Action1<Throwable>() {
//                @Override
//                public void call(Throwable throwable) {
//                }
//              }
//          );
    }
  }

  private void updateSingleRow(ListView listView, ImMessage message) {
    if (listView != null) {
      int start = listView.getFirstVisiblePosition();
      for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
        if (message.getMessageId() == ((ImMessage) listView.getItemAtPosition(i)).getMessageId()) {
          View view = listView.getChildAt(i - start);
          listView.getAdapter().getView(i, view, listView);
          break;
        }
    }
  }
}
