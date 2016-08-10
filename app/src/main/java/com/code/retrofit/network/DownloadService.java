package com.code.retrofit.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by LiWuJun on 2016/8/10.
 */
public interface DownloadService {
    //当下载大文件时需要加上注解@Streaming 主要作用是下载多少字节就立马写入磁盘，而不用把整个文件读入内存
    @Streaming
    @GET("01a3bd5737f2e4fcc0c1939b4798b259b3c31247e/com.supercell.clashroyale.mi.apk")
    Call<ResponseBody> downloadLargeAPK();
}
