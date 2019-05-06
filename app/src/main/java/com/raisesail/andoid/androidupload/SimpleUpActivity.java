package com.raisesail.andoid.androidupload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.raisesail.andoid.androidupload.bean.Data;
import com.raisesail.andoid.androidupload.bean.LzyResponse;
import com.raisesail.andoid.androidupload.bean.ServerModel;
import com.raisesail.andoid.androidupload.callback.DialogCallback;
import com.raisesail.andoid.androidupload.upload.GlideImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

public class SimpleUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_up);
    }

    public void simpleClick(View view){
        switch (view.getId()){
            case R.id.upJson:
                upJson();
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
        //sample config use HashMap
//        HashMap<String, String> params = new HashMap<>();
//        params.put("key1", "value1");
        //data bean
        String json = getCurrentJsonData();
        Log.d("request_code","response-------getCurrentJsonData-------->"+json);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<LzyResponse<ServerModel>>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)
                .headers("header1", "headerValue1")//
                //.params("param1", "paramValue1")//  upJson 与 params 是互斥的
                .upJson(jsonObject)
                .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        //handleResponse(response);
                        Log.d("request_code","response--------onSuccess------->"+response.code());
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        //handleError(response);
                        Log.d("request_code","response-------onError-------->"+response.code());
                    }
                });
    }

    private String getCurrentJsonData() {
        Data data = new Data();
        String pathBase64String = Utils.imageToBase64("/storage/emulated/0/DCIM/Camera/201501070001/20150107224939.jpg");
        Log.d("request_code","response-------pathBase64String.length()-------->"+pathBase64String.length());
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
