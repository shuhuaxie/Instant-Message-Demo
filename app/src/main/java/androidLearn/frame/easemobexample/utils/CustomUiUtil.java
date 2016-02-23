package androidLearn.frame.easemobexample.utils;


import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

public class CustomUiUtil {
  private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

  public static void setCustomId(View view) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      view.setId(generateSelfViewId());
    } else {
      view.setId(view.generateViewId());
    }
  }

  public static int generateSelfViewId() {
    for (; ; ) {
      final int result = sNextGeneratedId.get();
      // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
      int newValue = result + 1;
      if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
      if (sNextGeneratedId.compareAndSet(result, newValue)) {
        return result;
      }
    }
  }
}
