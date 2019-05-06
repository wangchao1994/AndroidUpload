package com.raisesail.andoid.androidupload.upload;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //默认配置
        //OkGo.getInstance().init(this);
        setDefaultParams();
    }

    /**
     * 设置默认参数
     */
    public void setDefaultParams(){
        //set request header-------------------------
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");
        params.put("commonParamsKey2", "这里支持中文参数");
        //set request header-------------------------

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //证书验证
        //HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();//默认ssl认证
        HttpsUtils.SSLParams currentSSLParams = getCurrentSSLParams();
        if (currentSSLParams != null){
            builder.sslSocketFactory(currentSSLParams.sSLSocketFactory, currentSSLParams.trustManager);
        }
        //主机认证/自定义认证
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3)
                .addCommonHeaders(headers)
                .addCommonParams(params);
    }

    private HttpsUtils.SSLParams getCurrentSSLParams() {
        try {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
            return sslParams;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
