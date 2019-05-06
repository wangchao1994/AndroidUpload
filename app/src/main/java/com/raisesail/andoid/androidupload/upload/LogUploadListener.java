package com.raisesail.andoid.androidupload.upload;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.upload.UploadListener;

public class LogUploadListener<T> extends UploadListener<T> {

    public LogUploadListener() {
        super("LogUploadListener");
    }

    @Override
    public void onStart(Progress progress) {
        System.out.println("onStart: " + progress);
    }

    @Override
    public void onProgress(Progress progress) {
        System.out.println("onProgress: " + progress);
    }

    @Override
    public void onError(Progress progress) {
        System.out.println("onError: " + progress);
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(T t, Progress progress) {
        System.out.println("onFinish: " + progress);
    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);
    }
}
