package androidLearn.frame.easemobexample.im.easemob;

import androidLearn.frame.easemobexample.im.connection.ImConnectListenerManager;
import androidLearn.frame.easemobexample.im.connection.ImConnectStatus;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;

public class EMConnectManager implements EMConnectionListener {

  private ImConnectListenerManager ICM;

  public EMConnectManager(ImConnectListenerManager manager){
    ICM = manager;
  }

  @Override
  public void onConnected() {
    ICM.onConnectStatusChanged(ImConnectStatus.CONNECTED);
  }

  @Override
  public void onDisconnected(int error) {
    ImConnectStatus status = ImConnectStatus.NETWORK_DISCONNECT;
    if(error == EMError.USER_REMOVED){
      // 显示帐号已经被移除
      status = ImConnectStatus.ACCOUNT_ERROR;
    }else if (error == EMError.CONNECTION_CONFLICT) {
      // 显示帐号在其他设备登陆
      status = ImConnectStatus.ACCOUNT_KICKED;
    } else {
      //连接不到聊天服务器
      status = ImConnectStatus.NETWORK_DISCONNECT;
    }
    ICM.onConnectStatusChanged(status);
  }
}
