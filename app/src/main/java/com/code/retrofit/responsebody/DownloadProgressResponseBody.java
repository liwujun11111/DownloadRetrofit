package com.code.retrofit.responsebody;

import com.code.retrofit.eventbus.DownloadProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by LiWuJun on 2016/8/10.
 */
public class DownloadProgressResponseBody extends ResponseBody{
    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                DownloadProgressEvent downloadProgressEvent = new DownloadProgressEvent();
                downloadProgressEvent.setBytesRead(totalBytesRead);
                downloadProgressEvent.setContentLength(responseBody.contentLength());
                downloadProgressEvent.setDone(bytesRead == -1);
                EventBus.getDefault().post(downloadProgressEvent);
                return bytesRead;
            }
        };
    }
}
