package androidLearn.frame.easemobexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
  public static final String ACTION_NEW_MESSAGE = "com.huimei.app.msg";
  public static final String EXTRA_MESSAGE_BUDDYID = "msg.buddyid";
  public static final String ACTION_NEW_ORDER = "com.huimei.app.order";
  public static final String EXTRA_ORDER_TYPE = "order.type";
  public static final String EXTRA_ORDER_ID = "order.id";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //init easemob

    setClickListener();
  }

  private void setClickListener() {
    findViewById(R.id.chat_list_tv).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
        startActivity(intent);
      }
    });
    findViewById(R.id.chat_easemob_tv).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
      }
    });
    findViewById(R.id.chat_obama_tv).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
      }
    });
  }
}
