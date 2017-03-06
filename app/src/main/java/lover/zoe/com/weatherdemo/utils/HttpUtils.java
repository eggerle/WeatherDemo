package lover.zoe.com.weatherdemo.utils;


import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zoe on 2017/3/6.
 */
public class HttpUtils {

    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);

    }
}
