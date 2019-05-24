package com.raisesail.andoid.androidupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;

import com.alibaba.fastjson.JSON;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.raisesail.andoid.androidupload.bean.Data;
import com.raisesail.andoid.androidupload.upload.GlideImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class SimpleUpActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_up);
        checkPermission();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
    public void simpleClick(View view) {
        switch (view.getId()) {
            case R.id.upJson:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upJson();
                    }
                }).start();
                break;
            case R.id.upString:
                break;
            case R.id.upBytes:
                break;
            case R.id.upFile:
                break;
            case R.id.selectImage:
                selectImage();
                break;
        }
    }

    private void upJson() {
        //data bean
        final String json = getCurrentJsonDataForm();
        Log.d("request_code", "response-------upJson------------------------->" + json);
        /*OkGo.<LzyResponse<ServerModel>>post(Urls.SERVER)//
                .tag(this)
                .upJson(json)
                .execute(new JsonCallback<LzyResponse<ServerModel>>() {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        //handleResponse(response);
                        Log.d("request_code", "response--------onSuccess------->" + response.code());
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        //handleError(response);
                        Log.d("request_code", "response-------onError-------->" + response.code());
                    }
                });*/
           /* OkGo.<SimpleResponse>post(Urls.SERVER_IP)
                    .tag(this)
                    .upJson(json)
                    .execute(new JsonCallback<SimpleResponse>() {
                        @Override
                        public void onSuccess(Response<SimpleResponse> response) {
                            Log.d("request_code","response-------onSuccess-------->"+response.message());
                            Log.d("request_code","response-------onSuccess-------->"+response.body().code);

                        }
                        @Override
                        public void onError(Response<SimpleResponse> response) {
                            Log.d("request_code","response-------onError-------->"+response.body());
                        }
                    });*/

          OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5*1000, TimeUnit.MILLISECONDS) //链接超时
                .readTimeout(10*1000,TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(10*1000,TimeUnit.MILLISECONDS) //写入超时
                .addInterceptor(new UserAgentInterceptor())
                .build();
            final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            final Request request = new Request.Builder()
                    .url(Urls.SERVER)
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .addHeader("X-API-KEY","E6Y4GLtGdIBsMHIwlh7S2eOUKhJrTsr5A8x8UHH0")
                    .post(requestBody)
                    .build();
            Call call1 = okHttpClient.newCall(request);
            call1.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("request_code", "response-------onError-------->" + e.getMessage());
                }
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Log.d("request_code", "response-------onResponse-------->" + response.message());
                    Log.d("request_code", "response-------onResponse-body------->" + response.code());
                    Log.d("request_code", "response-------onResponse-body------->" + response.body());
                    Log.d("request_code", "response-------onResponse-body------->" + response.isSuccessful());
                    ResponseBody responseBody = response.body();
                    if (responseBody != null){
                        parseJSON(responseBody.string());
                    }
                }

                private void parseJSON(String json) {
                    ResponseData resposeData = JSON.parseObject(json, ResponseData.class);
                    Log.d("request_code", "response-------getStatus-------->" + resposeData.getStatus());
                    Log.d("request_code", "response-------getId------->" + resposeData.getId());
                    Log.d("request_code", "response-------getLink------->" + resposeData.getLink());
                }
            });
        //图片
        //String base64Pic = getBase64Pic();
        //boolean base64ToFile = Utils.base64ToFile(base64Pic, "/storage/emulated/0/124.jpg");
        //Log.d("request_code", "response-------base64ToFile------->" + base64ToFile);
    }

    class UserAgentInterceptor  implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(getApplicationContext()))
                    .build();
            return chain.proceed(request);
        }
    }

    private String getCurrentJsonDataForm() {
        RaiseData raiseData = new RaiseData();
        raiseData.setData(getBase64Pic());
        raiseData.setDataFormat("jpg");
        raiseData.setName("123344444");
        RaiseData.MetaBean metaBean = new RaiseData.MetaBean();
        metaBean.set_$AdditionalInformation149("some data");
        raiseData.setMeta(metaBean);
        String toJSONString = JSON.toJSONString(raiseData);
        return toJSONString;
    }

    public String getBase64Pic() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = this.getResources().openRawResource(R.mipmap.img);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
        String bitmapToBase64 = Utils.bitmapToBase64(bitmap);
        return bitmapToBase64;
    }

    private String getCurrentJsonData() {
        Data data = new Data();
        String pathBase64String = Utils.imageToBase64("/storage/emulated/0/DCIM/Camera/201501070001/20150107224939.jpg");
        Log.d("request_code", "response-------pathBase64String.length()-------->" + pathBase64String.length());
        data.setData(pathBase64String);
        data.setCreated(String.valueOf(SystemClock.elapsedRealtime()));
        data.setDataFormat("jpg");
        data.setName("name");
        Data.MetaBean metaBean = new Data.MetaBean();
        metaBean.setExtra("hospital");
        metaBean.setThreshold(10);
        data.setMeta(metaBean);
        String toJSONString = JSON.toJSONString(data);
        return toJSONString;
    }

    public void selectImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setMultiMode(false);   //单选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(9);    //最多选择9张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }


}
