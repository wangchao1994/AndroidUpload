package com.raisesail.andoid.androidupload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.XExecutor;
import com.lzy.okserver.upload.UploadTask;
import com.raisesail.andoid.androidupload.upload.GlideImageLoader;
import com.raisesail.andoid.androidupload.upload.UploadAdapter;

import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity implements XExecutor.OnAllTaskEndListener {

    private OkUpload mCurrentUpload;
    private List<UploadTask<?>> mCurrentTask;
    private UploadAdapter mUploadAdapter;
    private RecyclerView mRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerview = findViewById(R.id.recyclerView);
        initParams();
    }

    private void initParams() {
        mCurrentUpload = OkUpload.getInstance();
        mCurrentUpload.getThreadPool().setCorePoolSize(1);

        mUploadAdapter = new UploadAdapter(this);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(mUploadAdapter);
        mCurrentUpload.addOnAllTaskEndListener(this);
    }

    public void mainClick(View view) {
        switch (view.getId()) {
            case R.id.main_select_picture:
                selectPicture();
                break;
            case R.id.main_start_upload:
                startTask();
                break;
        }
    }

    public void startTask() {
        if (mCurrentTask == null) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        for (UploadTask<?> task : mCurrentTask) {
            task.start();
        }
    }

    private void selectPicture() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(false);
        imagePicker.setSelectLimit(9);
        imagePicker.setCrop(false);
        Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onAllTaskEnd() {
        Log.d("upload", "onAllTaskEnd-------------------->finish()-------------------->");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                mCurrentTask = mUploadAdapter.updateData(images);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentUpload.removeOnAllTaskEndListener(this);
        mUploadAdapter.unRegister();
    }
}
