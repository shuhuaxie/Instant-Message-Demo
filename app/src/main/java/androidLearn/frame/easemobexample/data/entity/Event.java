package androidLearn.frame.easemobExample.data.entity;

import de.greenrobot.event.EventBus;


public class Event {

  private static abstract class EventBase{
    public void post(){
      EventBus.getDefault().post(this);
    }
  }

  //新订单通知
  public static class NewOrder extends EventBase{
    public String service_type;
    public String id;
    public NewOrder(String type, String id){
      this.service_type = type;
      this.id = id;
    }
  }

  //订单未读状态变化通知
  public static class OrderRead extends EventBase{
    public String id;
    public boolean unread;
    public OrderRead(String id, boolean unread){
      this.id = id;
      this.unread = unread;
    }
  }

  //订单结束
  public static class OrderClosed extends EventBase{
    public String service_type;
    public String id;
    public OrderClosed(String type, String id){
      this.service_type = type;
      this.id = id;
    }
  }
}
