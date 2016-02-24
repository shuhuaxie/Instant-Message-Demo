package androidLearn.frame.easemobExample.main;


import android.app.Activity;
import android.content.Intent;

import androidLearn.frame.easemobExample.R;
import androidLearn.frame.easemobExample.service.AccountManager;
import androidLearn.frame.easemobExample.utils.UiUtils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity implements View.OnClickListener {

  public static void openActivityAsRoot(Activity activity, String mobile) {
    Intent intent = new Intent(activity, LoginActivity.class);
    if (!TextUtils.isEmpty(mobile)) {
      intent.putExtra("mobile", mobile);
    }
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    activity.startActivity(intent);
  }

  private EditText et_userid;
  private EditText et_password;
  private Button bt_login;
//  private Button bt_getcode;
//  private View bt_clear;
//  private View bt_register;
//  private View bt_reset;
  private View bt_setpwd;
  private long sec = 60;
  private static long back_pressed = 0;
  private Toast mToast;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    bt_login = (Button) findViewById(R.id.login);


    et_userid.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        bt_login.setEnabled(checkInputOK());
//        bt_getcode.setEnabled(checkCanGetCode());
//        if (s.length() > 0) {
//          bt_clear.setVisibility(View.VISIBLE);
//        } else {
//          bt_clear.setVisibility(View.GONE);
//        }
      }
    });
    et_password.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        bt_login.setEnabled(checkInputOK());
      }
    });
    et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,
                                    KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
          if (bt_login.isEnabled()) {
            bt_login.performClick();
          }
        }

        return false;
      }
    });
//    et_password.setRawInputType(InputType.TYPE_CLASS_NUMBER);

    UiUtils.cancelAllNotification(this);
    AccountManager.getInstance().clearUser();

    String mobile = getIntent().getStringExtra("mobile");
    if(!TextUtils.isEmpty(mobile)){
      et_userid.setText(mobile);
    }
  }

  /**
   * 检查输入合法性
   */
  private boolean checkInputOK() {
    boolean result = false;

    result = !TextUtils.isEmpty(et_userid.getEditableText().toString())
        && !TextUtils.isEmpty(et_password.getEditableText().toString())
    ;

    return result;
  }

//  private boolean checkCanGetCode() {
//    boolean result = false;
//
//    result = !TextUtils.isEmpty(et_userid.getEditableText().toString())
//        && bt_getcode.getText().toString().equals(getString(R.string.btn_captcha));
//
//    return result;
//  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.login:
        // loginByMobile(et_userid.getEditableText().toString(), et_password.getEditableText().toString());
        break;
    }
  }

  @Override
  public void onBackPressed() {
    if (mToast != null) {
      mToast.cancel();
    }
    if (back_pressed + 2000 > System.currentTimeMillis()) {
      super.onBackPressed();
    } else {
      mToast = Toast.makeText(this, "再按一次退出" + getString(R.string.app_name), Toast.LENGTH_SHORT);
      mToast.show();
    }
    back_pressed = System.currentTimeMillis();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
//    handle.removeCallbacks(r);
  }

}

