package androidLearn.frame.easemobExample.im.conversation;

/**
 * Created by lzw on 15/5/14.
 */
public enum ImConversationType {
  OneToOne(0),Group(1);
  int value;
  public static final String KEY_ATTRIBUTE_TYPE = "type";

  ImConversationType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
