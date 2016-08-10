package com.code.retrofit;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.code.retrofit.eventbus.DownloadProgressEvent;
import com.code.retrofit.network.DownloadService;
import com.code.retrofit.network.RetrofitMananger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by LiWuJun on 2016/8/10.
 */
public class DownloadActivity extends AppCompatActivity{
    private File downloadFile;
    ProgressBar progressBar;
    private Button btnDownload;
    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/retrofit/b1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        final File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        downloadFile = new File(file, "retrofit_largeFile_test.apk");
        final DownloadService downLoadService = RetrofitMananger.createService();
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DownloadActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                Call<ResponseBody> call = downLoadService.downloadLargeAPK();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            BufferedSink sink = null;
                            //下载文件到本地
                            try {
                                sink = Okio.buffer(Okio.sink(downloadFile));
                                sink.writeAll(response.body().source());
                            } catch (Exception e) {
                                if (e != null) {
                                    e.printStackTrace();
                                }
                            } finally {
                                try {
                                    if (sink != null) sink.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d("DownloadActivity", "==responseCode=="+response.code() + "");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("下载失败", t.getMessage());
                    }
                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //注册EventBus
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnDownload = (Button) findViewById(R.id.download);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadProgressUpdate(DownloadProgressEvent messageEvent) {
        if(messageEvent.isDone()){
            btnDownload.setText("下载完成");
            Toast.makeText(this, "下载成功", Toast.LENGTH_SHORT).show();
        }else{
            progressBar.setProgress((int) ((messageEvent.getBytesRead() * 100) / messageEvent.getContentLength()));
        }
    }
}
