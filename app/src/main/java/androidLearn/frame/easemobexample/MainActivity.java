package androidLearn.frame.easemobExample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidLearn.frame.easemobExample.data.entity.User;
import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.service.AccountManager;
import androidLearn.frame.easemobExample.utils.UiUtils;
import androidLearn.frame.easemobExample.widget.ProgressDialogFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements View.OnClickListener{
  public static final String ACTION_NEW_MESSAGE = "com.huimei.app.msg";
  public static final String EXTRA_MESSAGE_BUDDYID = "msg.buddyid";
  public static final String ACTION_NEW_ORDER = "com.huimei.app.order";
  public static final String EXTRA_ORDER_TYPE = "order.type";
  public static final String EXTRA_ORDER_ID = "order.id";
  private ProgressDialogFragment mProgressDialog;
  private ActivityBack mBackListener;
  private static long mLastBackPressedTime = 0;
  private Toast mToast;

  @InjectView(R.id.login_tv) TextView mLoginTv;
  @InjectView(R.id.chat_list_tv) TextView mChatListTv;
  @InjectView(R.id.chat_easemob_tv) TextView mChatEasemobTv;
  @InjectView(R.id.chat_obama_tv) TextView mChatObamaTv;
  @InjectView(R.id.logout_tv) TextView mLogoutTv;
  @InjectView(R.id.normal_ll) LinearLayout normalLl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    setClickListener();
  }

  private void login() {
    // TODO use server.
    final User user = new User();
    user.token = "nick_token";
    user.name = "shuhua";
//    user.id = "xieshuhua";
    //user.mobile = mobile;
    user.imPwd = "000000";
    user.imId = "xieshuhua";
    user.avatar = "http://7xr6tu.com1.z0.glb.clouddn.com/easemobExample15d3fe1c32e058c10b91bd8a7fd9ab48.png";
    mProgressDialog = UiUtils.showProgressDialog(MainActivity.this, "loading...");
    App.getInstance().getImClient().open(user.imId, user.imPwd, new ImClient.ImClientCallBack() {
      @Override
      public void onCallBack(boolean success, String errMsg) {
        if (success) {
          normalLl.setVisibility(View.VISIBLE);
          mLoginTv.setVisibility(View.GONE);
          UiUtils.dismiss(mProgressDialog);
          Toast.makeText(MainActivity.this, "login success.", Toast.LENGTH_SHORT).show();
          AccountManager.getInstance().login(MainActivity.this, user);
//          finish();
        } else {
          UiUtils.dismiss(mProgressDialog);
          UiUtils.showToast(MainActivity.this, errMsg);
        }
      }
    });
  }

  private void setClickListener() {
    mLoginTv.setOnClickListener(this);
    mChatListTv.setOnClickListener(this);
    mChatEasemobTv.setOnClickListener(this);
    mChatObamaTv.setOnClickListener(this);
    mLogoutTv.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent intent;
    switch (v.getId()){
      case R.id.login_tv:
        login();
        break;
      case R.id.chat_list_tv:
        intent = new Intent(MainActivity.this, ChatListActivity.class);
        startActivity(intent);
        break;
      case R.id.chat_easemob_tv:
        ChatActivity.startActivity(this, "easemob");
        break;
      case R.id.chat_obama_tv:
        ChatActivity.startActivity(this, "Obama");
        break;
      case R.id.logout_tv:

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
      mToast = Toast.makeText(this, "press back again to exit" + getString(R.string.app_name), Toast.LENGTH_SHORT);
      mToast.show();
    }
    mLastBackPressedTime = System.currentTimeMillis();
  }

  public interface ActivityBack {
    boolean onActivityBack();
  }
}
