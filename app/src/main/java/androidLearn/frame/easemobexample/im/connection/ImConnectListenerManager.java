package androidLearn.frame.easemobExample.im.connection;

public interface ImConnectListenerManager {
  void addConnectHandler(ImConnectListener handler);
  void removeConnectHandler(ImConnectListener handler);
  void clearConnectHandler();

  ImConnectStatus getConnectStatus();
  void onConnectStatusChanged(ImConnectStatus status);
}
