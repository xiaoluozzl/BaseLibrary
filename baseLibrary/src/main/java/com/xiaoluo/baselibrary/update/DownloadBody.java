package com.xiaoluo.baselibrary.update;


import com.xiaoluo.baselibrary.rxbus.RxBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

import static com.xiaoluo.baselibrary.rxbus.RxBus.RXBUS_DOWNLOAD_APK;

/**
 * 下载responseBody
 *
 * author: xiaoluo
 * date: 2017/7/5 17:08
 */
public class DownloadBody extends ResponseBody {

    Response originalResponse;

    public DownloadBody(Response originalResponse) {
        this.originalResponse = originalResponse;
    }

    @Override
    public MediaType contentType() {
        return originalResponse.body().contentType();
    }

    @Override
    public long contentLength() {
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(new ForwardingSource(originalResponse.body().source()) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                RxBus.getInstance().post(RXBUS_DOWNLOAD_APK, new ApkBean(contentLength(), bytesReaded));
                return bytesRead;
            }
        });
    }
}
