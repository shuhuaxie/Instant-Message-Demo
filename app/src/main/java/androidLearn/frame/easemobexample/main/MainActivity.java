package androidLearn.frame.easemobExample.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidLearn.frame.easemobExample.App;
import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.data.HmDataService;
import androidLearn.frame.easemobExample.data.entity.User;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.service.AccountManager;
import androidLearn.frame.easemobExample.utils.UiUtils;
import androidLearn.frame.easemobExample.widget.ProgressDialogFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements View.OnClickListener {
  public static final String ACTION_NEW_MESSAGE = "com.huimei.app.msg";
  public static final String EXTRA_MESSAGE_BUDDYID = "msg.buddyid";
  public static final String ACTION_NEW_ORDER = "com.huimei.app.order";
  public static final String EXTRA_ORDER_TYPE = "order.type";
  public static final String EXTRA_ORDER_ID = "order.id";
  private ProgressDialogFragment mProgressDialog;
  private ActivityBack mBackListener;
  private static long mLastBackPressedTime = 0;
  private Toast mToast;
  public User[] users;

  @InjectView(R.id.login_1_tv) TextView mLoginTv;
  @InjectView(R.id.chat_list_tv) TextView mChatListTv;
  @InjectView(R.id.chat_easemob_tv) TextView mChatEasemobTv;
  @InjectView(R.id.chat_obama_tv) TextView mChatObamaTv;
  @InjectView(R.id.logout_tv) TextView mLogoutTv;
  @InjectView(R.id.normal_ll) LinearLayout normalLl;
  @InjectView(R.id.login_2_tv) TextView mLogin2Tv;
  @InjectView(R.id.login_3_tv) TextView mLogin3Tv;
  @InjectView(R.id.login_ll) LinearLayout mLoginLl;
  @InjectView(R.id.chat_shuahua_tv) TextView mChatShuahuaTv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initUserData();
    ButterKnife.inject(this);
    setClickListener();
  }


  private void login(final int index) {
    final User user = users[index];
    user.avatar = HmDataService.getInstance().getEndPoint() + "/easemobExample15d3fe1c32e058c10b91bd8a7fd9ab48.png";
    mProgressDialog = UiUtils.showProgressDialog(MainActivity.this, "loading...");
    App.getInstance().getImClient().open(user.imId, user.imPwd, new ImClient.ImClientCallBack() {
      @Override
      public void onCallBack(boolean success, String errMsg) {
        if (success) {
          normalLl.setVisibility(View.VISIBLE);
          mLoginLl.setVisibility(View.GONE);
          UiUtils.dismiss(mProgressDialog);
          Toast.makeText(MainActivity.this, "login success.", Toast.LENGTH_SHORT).show();
          AccountManager.getInstance().login(MainActivity.this, user);
          mChatShuahuaTv.setVisibility(View.VISIBLE);
          mChatEasemobTv.setVisibility(View.VISIBLE);
          mChatObamaTv.setVisibility(View.VISIBLE);
          switch (index){
            case(0):
              mChatShuahuaTv.setVisibility(View.GONE);
              break;
            case(1):
              mChatEasemobTv.setVisibility(View.GONE);
              break;
            case(2):
              mChatObamaTv.setVisibility(View.GONE);
              break;
          }
        } else {
          UiUtils.dismiss(mProgressDialog);
          UiUtils.showToast(MainActivity.this, errMsg);
        }
      }
    });
  }

  private void setClickListener() {
    mLoginTv.setOnClickListener(this);
    mLogin2Tv.setOnClickListener(this);
    mLogin3Tv.setOnClickListener(this);
    mChatListTv.setOnClickListener(this);
    mChatEasemobTv.setOnClickListener(this);
    mChatObamaTv.setOnClickListener(this);
    mChatShuahuaTv.setOnClickListener(this);
    mLogoutTv.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent intent;
    switch (v.getId()) {
      case R.id.login_1_tv:
        login(0);
        break;
      case R.id.login_2_tv:
        login(1);
        break;
      case R.id.login_3_tv:
        login(2);
        break;
      case R.id.chat_list_tv:
        intent = new Intent(MainActivity.this, ChatListActivity.class);
        startActivity(intent);
        break;
      case R.id.chat_shuahua_tv:
        ChatActivity.startActivity(this, "xieshuhua");
        break;
      case R.id.chat_easemob_tv:
        ChatActivity.startActivity(this, "easemob");
        break;
      case R.id.chat_obama_tv:
        ChatActivity.startActivity(this, "Obama");
        break;
      case R.id.logout_tv:
        AccountManager.getInstance().logout(MainActivity.this, true);
        normalLl.setVisibility(View.GONE);
        mLoginLl.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }
  }

  @Override
  public void onBackPressed() {
    if (mBackListener != null && mBackListener.onActivityBack()) {
      return;
    }

    if (mToast != null) {
      mToast.cancel();
    }

    if (mLastBackPressedTime + 2000 > System.currentTimeMillis()) {
      super.onBackPressed();
    } else {
      mToast = Toast.makeText(this, "press back again to exit " + getString(R.string.app_name), Toast.LENGTH_SHORT);
      mToast.show();
    }
    mLastBackPressedTime = System.currentTimeMillis();
  }

  public interface ActivityBack {
    boolean onActivityBack();
  }

  private void initUserData() {
    users = new User[3];
    User user1 = new User();
    user1.name = "shuhua";
    user1.imId = "xieshuhua";
    user1.imPwd = "000000";

    User user2 = new User();
    user2.name = "easemob";
    user2.imId = "easemob";
    user2.imPwd = "000000";

    User user3 = new User();
    user3.name = "Obama";
    user3.imId = "Obama";
    user3.imPwd = "000000";

    users[0] = user1;
    users[1] = user2;
    users[2] = user3;
  }
}
