package com.code.retrofit.interceptor;

import com.code.retrofit.responsebody.DownloadProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by LiWuJun on 2016/8/10.
 */
public class DownloadProgressInterceptor implements Interceptor {

    public DownloadProgressInterceptor()
    {
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new DownloadProgressResponseBody(originalResponse.body())).build();
    }
}
