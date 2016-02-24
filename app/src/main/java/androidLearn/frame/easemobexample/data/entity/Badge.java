package androidLearn.frame.easemobExample.data.entity;

import androidLearn.frame.easemobExample.App;

public class Badge {

  public enum BadgeType{
    BADGE_TYPE_IM_MESSAGE,
    BADGE_TYPE_CLINIC_ORDER,
    BADGE_TYPE_PHONE_ORDER,
    BADGE_TYPE_CONSULT_ORDER,

    BADGE_TYPE_COUNT, //类型总量，一定要放在最后
  }

  public BadgeType type;
  public int count;

  public Badge(BadgeType type, int count){
    this.type = type;
    this.count = count;
  }

  public static int getBadgeCount(Badge.BadgeType type) {
    int count = 0;
    switch (type) {
      case BADGE_TYPE_IM_MESSAGE:
        count = App.getInstance().getImClient().getAllUnreadMsgCount();
        break;
      case BADGE_TYPE_CLINIC_ORDER:
      case BADGE_TYPE_PHONE_ORDER:
      case BADGE_TYPE_CONSULT_ORDER:
      {
//        Integer c = UserData.getData(UserData.TYPE_NEW_ORDER_COUNT_BASE + type.toString(), Integer.class);
//        if (c != null) {
//          count = c;
//        }
      }
      break;
    }

    return count;
  }

  public static int getAllBadgeCount(){
    int count = 0;
    for (int i = 0; i < Badge.BadgeType.BADGE_TYPE_COUNT.ordinal(); i++) {
      count += Badge.getBadgeCount(Badge.BadgeType.values()[i]);
    }

    return count;
  }

  public static void updateBadgeCount(Badge.BadgeType type, int count){
    switch (type) {
      case BADGE_TYPE_CLINIC_ORDER:
      case BADGE_TYPE_PHONE_ORDER:
      case BADGE_TYPE_CONSULT_ORDER:
//        UserData.update(UserData.TYPE_NEW_ORDER_COUNT_BASE + type.toString(), count);
        break;
    }
    Badge badge = new Badge(type, count);
//    EventBus.getDefault().post(badge);
  }

  public static void addBadgeCount(Badge.BadgeType type, int count){
    Integer c = 0;
    switch (type) {
      case BADGE_TYPE_CLINIC_ORDER:
      case BADGE_TYPE_PHONE_ORDER:
      case BADGE_TYPE_CONSULT_ORDER:
//        c = UserData.getData(UserData.TYPE_NEW_ORDER_COUNT_BASE + type.toString(), Integer.class);
//        updateBadgeCount(type, c + count);
        break;
    }
  }

  public static void minusBadgeCount(Badge.BadgeType type, int count){
    Integer c = 0;
    switch (type) {
      case BADGE_TYPE_CLINIC_ORDER:
      case BADGE_TYPE_PHONE_ORDER:
      case BADGE_TYPE_CONSULT_ORDER:
//        c = UserData.getData(UserData.TYPE_NEW_ORDER_COUNT_BASE + type.toString(), Integer.class);
//        c -= count;
//        updateBadgeCount(type, c >= 0 ? c : 0);
        break;
    }
  }
}
