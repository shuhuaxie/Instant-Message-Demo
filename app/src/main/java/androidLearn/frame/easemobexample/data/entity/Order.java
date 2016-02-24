package androidLearn.frame.easemobExample.data.entity;

public class Order extends BaseEntity {

  public final static String TYPE_CONSULT = "consult";    //图文咨询
  public final static String TYPE_CLINIC = "clinic";      //预约挂号
  public final static String TYPE_PHONE = "phonecall";    //电话

  public int age;
  public String gender;
  public String id;
  public String idcard;
  public String location;
  public String message;
  public String mobile;
  public String name;
  public float price;
  public long schedule;
  public String serviceName;
  public String serviceType;
  public String status;
//  public PatientInfo patient;
  public String[] attachments;
//  public DoctorInfoResponse.Doctor doctor;
  public boolean unread;
  public long created_at;
//  public Voip voip;         //只有电话问诊的订单有这个字段
  public int call_minutes;  //电话问诊通话时间
  public String tips;
  public int talk_duration; //通话用掉的分钟数

  public void setReaded(){

    if(!unread){
      return;
    }

//    HmDataService.getInstance().setOrderRead(id).
//        observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//            new Action1<BaseResponse>() {
//              @Override
//              public void call(BaseResponse response) {
//                unread = false;
//                //通知订单未读状态更新了
//                new Event.OrderRead(id, unread).post();
//                //通知Badge更新
//                switch(serviceType){
//                  case TYPE_CONSULT:
//                    Badge.minusBadgeCount(Badge.BadgeType.BADGE_TYPE_CONSULT_ORDER, 1);
//                    break;
//                  case TYPE_CLINIC:
//                    Badge.minusBadgeCount(Badge.BadgeType.BADGE_TYPE_CLINIC_ORDER, 1);
//                    break;
//                  case TYPE_PHONE:
//                    Badge.minusBadgeCount(Badge.BadgeType.BADGE_TYPE_PHONE_ORDER, 1);
//                    break;
//                }
//              }
//            },
//            new Action1<Throwable>() {
//              @Override
//              public void call(Throwable throwable) {
//
//              }
//            }
//        );
  }
}
