package androidLearn.frame.easemobexample.data;

import androidLearn.frame.easemobexample.service.AccountManager;
import androidLearn.frame.easemobexample.utils.DeviceUtils;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class HmDataService {

  private static final String RELEASE_ENDPOINT = "http://api.huimeibest.com/rpc";
  private static final String TEST_ENDPOINT = "http://api-staging.huimeibest.com/rpc";
  private static final String RELEASE_UPLOAD_ENDPOINT = "http://api-staging.huimeibest.com/storage/object/upload";
  private static final String TEST_UPLOAD_ENDPOINT = "http://api-staging.huimeibest.com/storage/object/upload";
  private static final long CONNECT_TIMEOUT_MILLIS = 20 * 1000;
  private static final long READ_TIMEOUT_MILLIS = 30 * 1000;

  private static final String APPID_TEST = "adb0u2r4a6f7m9t1";
  private static final String APPKEY_TEST = "v2b9q7o0t1t4x3p4e0r7t6";
  private static final String APPID_RELEASE = "adb0y6b9e3v2j9q1";
  private static final String APPKEY_RELEASE = "y8u6x1b7g0c3j2y3y4n9f1";

  private static HmDataService sInstance;

  public static HmDataService getInstance() {
    if (sInstance == null) {
      sInstance = new HmDataService(DataManager.getInstance());
    }
    return sInstance;
  }

  private final DataManager mDataManager;
  private final HmRestService mHmRestService;

  public HmDataService(DataManager dataManager) {
    mDataManager = dataManager;
    Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .disableHtmlEscaping()
        .create();
    GsonConverter gsonConverter = new GsonConverter(gson);
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//    try {
//      client.setSslSocketFactory(SSLCustomSocketFactory.getSocketFactory());
//    } catch (Throwable throwable) {
//      throwable.printStackTrace();
//    }

    final String userAgent = "Android HuiMei/" + mDataManager.get(CacheKeys.VERSION_NAME);
    RequestInterceptor interceptor = new RequestInterceptor() {
      @Override
      public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", userAgent);
        request.addHeader("Content-Type", "application/json");
//        request.addHeader("X-HM-ID", getAppId());
//        request.addHeader("X-HM-Sign", getRequestSign());
        String token = AccountManager.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
          request.addHeader("X-HM-Session-Token", token);
        }
        request.addHeader("X-HM-Endpoint-Agent", String.format("%s,%s,%s", DeviceUtils.getManufacturer(), DeviceUtils.getDeviceModel(), DeviceUtils.getOSVersion()));
        request.addHeader("X-HM-App-Version", String.valueOf(mDataManager.get(CacheKeys.VERSION_NAME)));
//        request.addHeader("X-AVOSCloud-Application-Id", "id-2n9x-3i7r");
//        request.addHeader("X-AVOSCloud-Request-Sign", getRequestSign());
      }
    };

    RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(getEndPoint())
        .setConverter(gsonConverter)
        .setRequestInterceptor(interceptor)
        .setClient(new OkClient(client))
        .build();
    mHmRestService = restAdapter.create(HmRestService.class);
  }

  public String getEndPoint() {
    return RELEASE_ENDPOINT;
  }

  public String getUploadEndPoint(){
        return RELEASE_ENDPOINT;
  }
}
