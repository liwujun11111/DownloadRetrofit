package com.code.retrofit.network;

import com.code.retrofit.interceptor.DownloadProgressInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LiWuJun on 2016/8/10.
 */
public class RetrofitMananger {

    public static DownloadService createService()
    {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        //特别注意下载大文件时千万不要打开下面注释代码 否则会导致OOM
        //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()//
                .readTimeout(5, TimeUnit.SECONDS)//
                .connectTimeout(5, TimeUnit.SECONDS)//
                .addInterceptor(new DownloadProgressInterceptor())
                .build();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://f5.market.xiaomi.com/download/AppStore/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executorService) //默认CallBack回调在主线程进行,当设置下载大文件时需设置注解@Stream 不加这句话会报android.os.NetworkOnMainThreadException
                .build();

        DownloadService apiService = retrofit.create(DownloadService.class);

        return apiService;
    }
}
