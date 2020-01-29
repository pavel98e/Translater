package com.elshin.translater.network;

import com.elshin.translater.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    private Retrofit mRetrofit;

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()//для работы с API сайта
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();
    }

    private OkHttpClient getHttpClient() {//для http-запросов
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            Interceptor interceptor = new HttpLoggingInterceptor();
            ((HttpLoggingInterceptor) interceptor).setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        return okHttpClientBuilder.build();
    }

    public ServerApi getJSONApi() {
        return mRetrofit.create(ServerApi.class);
    }
}
