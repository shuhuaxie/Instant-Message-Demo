package androidLearn.frame.easemobExample.im.connection;

/**
 * Created by 明帅 on 2015/12/22.
 */
public enum ImConnectStatus {
  CONNECTED,
  NETWORK_DISCONNECT,   //网络连接中断
  ACCOUNT_KICKED,       //被踢下线，要求重登录,
  ACCOUNT_ERROR,        //账户错误，要求重登录,
}
