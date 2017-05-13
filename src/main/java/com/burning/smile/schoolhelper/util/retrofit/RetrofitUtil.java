package com.burning.smile.schoolhelper.util.retrofit;

import com.burning.smile.schoolhelper.AppConfig;
import com.burning.smile.schoolhelper.util.fastjson_convert.FastJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static Retrofit retrofit;

    public static ServiceAPI getRetrofitApiInstance() {
        return getRetrofit().create(ServiceAPI.class);
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (Retrofit.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .client(OkHttpUtils.getInstance().getOkHttpClient())
                            .baseUrl(AppConfig.BASE_URL)
                            .addConverterFactory(FastJsonConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();

                }
            }
        }
        return retrofit;
    }

    public static OkHttpClient defaultOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .build();
        return client;
    }
}
