package androidLearn.frame.easemobexample.data.entity;


public class UserData {

  //数据的类型名，一定不要重复
  public final static String TYPE_PATIENT_LIST_NEED_UPDATE = "patient.list.update";
  public final static String TYPE_NEW_ORDER_COUNT_BASE = "order.count.";
  public final static String TYPE_PATIENT_COUNT = "patient.count";
  public final static String TYPE_FANS_COUNT = "fans.count";

  //数据库字段名
  private final static String USER_ID_FIELD_NAME = "user_id";
  private final static String TYPE_FIELD_NAME = "type";
  private final static String DATA_FIELD_NAME = "data";

  private int id;

  private String userid;
  private String type;
  private String data;

  public UserData(){}


}
