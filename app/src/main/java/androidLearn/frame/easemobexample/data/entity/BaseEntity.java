package androidLearn.frame.easemobExample.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class BaseEntity implements Parcelable {

  public String toJson() {
    return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
  }

  public static BaseEntity fromJson(String json, Type typeOfT) {
    return new Gson().fromJson(json, typeOfT);
  }

  public static <T> T fromJson(String json, Class<T> classOfT) {
    return new Gson().fromJson(json, classOfT);
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.getClass().getName());
    dest.writeString(this.toJson());
  }

  public static final Creator<BaseEntity> CREATOR = new Creator<BaseEntity>() {

    @Override
    public BaseEntity createFromParcel(Parcel source) {
      String classname = source.readString();
      String json = source.readString();

      BaseEntity data = null;
      Class<?> forName = null;
      try {
        forName = Class.forName(classname);
        data = (BaseEntity) fromJson(json, forName);
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return data;
    }

    @Override
    public BaseEntity[] newArray(int size) {
      return new BaseEntity[size];
    }

  };
}
