package androidLearn.frame.easemobexample.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by 明帅 on 2015/9/6.
 */
public class DeviceUtils {
  public static String getDeviceId(Context context){
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    String deviceId = UUID.randomUUID().toString();

    final String tmDevice, tmSerial, androidId;
    try{
      tmDevice = "" + tm.getDeviceId();
      tmSerial = "" + tm.getSimSerialNumber();
      androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

      UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
      deviceId = deviceUuid.toString();
    }
    catch(Exception e){

    }

    return deviceId;
  }

  public static String getDeviceModel(){
    return Build.MODEL;
  }

  public static String getOSVersion(){
    return Build.VERSION.RELEASE;
  }

  public static String getManufacturer(){
    return Build.MANUFACTURER;
  }
}
