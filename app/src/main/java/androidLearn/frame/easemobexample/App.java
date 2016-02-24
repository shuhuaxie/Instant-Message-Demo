package androidLearn.frame.easemobExample;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import androidLearn.frame.easemobExample.im.ImClient;
import androidLearn.frame.easemobExample.im.easemob.EMClient;

import java.util.Iterator;
import java.util.List;


public class App extends Application {

  private static App mInstance;
  private ImClient mImClient = null;
  private boolean mUpdateChecked = false;

  @Override
  public void onCreate() {
    super.onCreate();
    mInstance = this;
    initImClient();
  }

  public static App getInstance() {
    return mInstance;
  }

  public ImClient getImClient() {
    return mImClient;
  }

  public void initImClient() {

    int pid = android.os.Process.myPid();
    String processAppName = getAppName(pid);
    if (processAppName == null || !processAppName.equalsIgnoreCase(App.getInstance().getPackageName())) {
      return;
    }

//    mImClient = LeanCloudClient.getInstance();  //使用Leancloud IM SDK
    mImClient = EMClient.getInstance();   //使用环信IM SDK
    mImClient.init(this);
  }

  private String getAppName(int pID) {
    String processName = null;
    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
    List l = am.getRunningAppProcesses();
    Iterator i = l.iterator();
    PackageManager pm = this.getPackageManager();
    while (i.hasNext()) {
      ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
      try {
        if (info.pid == pID) {
          CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
          // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
          // info.processName +"  Label: "+c.toString());
          // processName = c.toString();
          processName = info.processName;
          return processName;
        }
      } catch (Exception e) {
        // Log.d("Process", "Error>> :"+ e.toString());
      }
    }
    return processName;
  }

}
