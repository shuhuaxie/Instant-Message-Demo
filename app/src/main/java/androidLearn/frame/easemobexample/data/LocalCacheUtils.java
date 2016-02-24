package androidLearn.frame.easemobExample.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

public class LocalCacheUtils {

	private static LocalCacheUtils localCache;
	private SharedPreferences sp;
	private final String TAG = "LocalCache";

	private LocalCacheUtils(Context context) {
		sp = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
	}

	public static LocalCacheUtils getInstance(Context context) {
		if (localCache == null) {
			localCache = new LocalCacheUtils(context);
		}
		return localCache;
	}
	
	public synchronized boolean getBoolean(String key){
		return sp.getBoolean(key, false);
	}
	
	public synchronized boolean getBoolean(String key, boolean defValue){
		return sp.getBoolean(key, defValue);
	}
	
	public synchronized void setBoolean(String key, boolean value){
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public synchronized float getFloat(String name){
		return sp.getFloat(name, 0);
	}
	
	public synchronized float getFloat(String name, float defValue){
		return sp.getFloat(name, defValue);
	}
	
	public synchronized void setFloat(String key, float value){
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public synchronized int getInt(String name){
		return sp.getInt(name, 0);
	}
	
	public synchronized int getInt(String name, int defValue){
		return sp.getInt(name, defValue);
	}
	
	public synchronized void setInt(String key, int value){
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public synchronized long getLong(String name){
		return sp.getLong(name, 0);
	}
	
	public synchronized long getLong(String name, long defValue){
		return sp.getLong(name, defValue);
	}
	
	public synchronized void setLong(String key, long value){
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public synchronized String getString(String name){
		return sp.getString(name, "");
	}
	
	public synchronized String getString(String name, String defValue){
		return sp.getString(name, defValue);
	}
	
	public synchronized void setString(String key, String value){
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public synchronized <T> T getObject(String key, Class<T> classOfT){
		String json = sp.getString(key, "");
		if(json.length() > 0){
			return new Gson().fromJson(json, classOfT);
		}
		
		return null;
	}
	
	public synchronized void setObject(String key, Object value){
		Editor editor = sp.edit();
		String json = "";
		if(value != null){
			json = new Gson().toJson(value);
		}
		editor.putString(key, json);
		editor.commit();
	}
}
