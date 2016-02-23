package androidLearn.frame.easemobexample.im.connection;

/**
 * Created by 明帅 on 2015/9/9.
 * 用于UI连接状态监听类
 */
public interface ImConnectListener {
  int getConnectListenerPriority();
  boolean onImConnectStatusChanged(boolean connected, ImConnectStatus status);
}
