package com.raisesail.andoid.androidupload.upload;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.db.UploadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.raisesail.andoid.androidupload.R;
import com.raisesail.andoid.androidupload.Urls;
import com.raisesail.andoid.androidupload.ui.NumberProgressBar;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {
    //upload status
    public static final int TYPE_ALL = 0;
    public static final int TYPE_FINISH = 1;
    public static final int TYPE_ING = 2;
    private List<UploadTask<?>> values;
    private List<ImageItem> images;
    private NumberFormat numberFormat;
    private LayoutInflater inflater;
    private Context context;
    private int type = -1;

    public UploadAdapter(Context context) {
        this.context = context;
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void updateData(int type) {
        //数据库数据恢复
        this.type = type;
        if (type == TYPE_ALL) {
            values = OkUpload.restore(UploadManager.getInstance().getAll());
        }
        if (type == TYPE_FINISH){
            values = OkUpload.restore(UploadManager.getInstance().getFinished());
        }
        if (type == TYPE_ING){
            values = OkUpload.restore(UploadManager.getInstance().getUploading());
        }
        //由于Converter是无法保存下来的，所以这里恢复任务的时候，需要额外传入Converter，否则就没法解析数据
        //至于数据类型，统一就行，不一定非要是String
        for (UploadTask<?> task : values) {
            //noinspection unchecked
            Request<String, ? extends Request> request = (Request<String, ? extends Request>) task.progress.request;
            request.converter(new StringConvert());
        }
        notifyDataSetChanged();
    }

    public List<UploadTask<?>> updateData(List<ImageItem> images) {
        this.type = -1;
        this.images = images;
        values = new ArrayList<>();
        if (images != null) {
            Random random = new Random();
            for (int i = 0; i < images.size(); i++) {
                ImageItem imageItem = images.get(i);
                PostRequest<String> postRequest = OkGo.<String>post(Urls.URL_FORM_UPLOAD)
                        .headers("aaa", "111")
                        .params("bbb", "222")
                        .params("fileKey"+i, new File(imageItem.path))
                        //.upJson()//传递Json数据
                        .converter(new StringConvert());

                UploadTask<String> task = OkUpload.request(imageItem.path, postRequest)
                        .priority(random.nextInt(100))
                        .extra1(imageItem)//
                        .save();
                values.add(task);
            }
        }
        notifyDataSetChanged();
        return values;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_upload_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UploadTask<String> task = (UploadTask<String>) values.get(position);
        String tag = createTag(task);
        task.register(new ListUploadListener(tag, holder)).register(new LogUploadListener<String>());
        holder.setTag(tag);
        holder.setTask(task);
        holder.bind();
        holder.refresh(task.progress);
    }

    public void unRegister() {
        Map<String, UploadTask<?>> taskMap = OkUpload.getInstance().getTaskMap();
        for (UploadTask<?> task : taskMap.values()) {
            task.unRegister(createTag(task));
        }
    }

    private String createTag(UploadTask task) {
        return type + "_" + task.progress.tag;
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 : values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView name;
        TextView priority;
        TextView downloadSize;
        TextView tvProgress;
        TextView netSpeed;
        NumberProgressBar pbProgress;
        Button upload;
        Button remove;
        Button restart;
        private UploadTask<?> task;
        private String tag;

        public void setTask(UploadTask<?> task) {
            this.task = task;
        }

        public void bind() {
            Progress progress = task.progress;
            ImageItem item = (ImageItem) progress.extra1;
            Glide.with(context).load(item.path).error(R.mipmap.ic_launcher).into(icon);
            name.setText(item.name);
            priority.setText(String.format("优先级：%s", progress.priority));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            priority = itemView.findViewById(R.id.priority);
            downloadSize = itemView.findViewById(R.id.downloadSize);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            netSpeed = itemView.findViewById(R.id.netSpeed);
            pbProgress = itemView.findViewById(R.id.pbProgress);
            upload = itemView.findViewById(R.id.upload);
            remove = itemView.findViewById(R.id.remove);
            restart = itemView.findViewById(R.id.restart);
            upload.setOnClickListener(this);
            remove.setOnClickListener(this);
            restart.setOnClickListener(this);
        }

        public void refresh(Progress progress) {
            String currentSize = Formatter.formatFileSize(context, progress.currentSize);
            String totalSize = Formatter.formatFileSize(context, progress.totalSize);
            downloadSize.setText(currentSize + "/" + totalSize);
            priority.setText(String.format("优先级：%s", progress.priority));
            switch (progress.status) {
                case Progress.NONE:
                    netSpeed.setText("停止");
                    upload.setText("上传");
                    break;
                case Progress.PAUSE:
                    netSpeed.setText("暂停中");
                    upload.setText("继续");
                    break;
                case Progress.ERROR:
                    netSpeed.setText("上传出错");
                    upload.setText("出错");
                    break;
                case Progress.WAITING:
                    netSpeed.setText("等待中");
                    upload.setText("等待");
                    break;
                case Progress.FINISH:
                    upload.setText("完成");
                    netSpeed.setText("上传成功");
                    break;
                case Progress.LOADING:
                    String speed = Formatter.formatFileSize(context, progress.speed);
                    netSpeed.setText(String.format("%s/s", speed));
                    upload.setText("停止");
                    break;
            }
            tvProgress.setText(numberFormat.format(progress.fraction));
            pbProgress.setMax(10000);
            pbProgress.setProgress((int) (progress.fraction * 10000));
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.upload:
                    Log.d("onclick", "upload------------------------------>");
                    upload();
                    break;
                case R.id.remove:
                    Log.d("onclick", "remove------------------------------>");
                    remove();
                    break;
                case R.id.restart:
                    Log.d("onclick", "restart------------------------------>");
                    restart();
                    break;
            }
        }

        /**
         * start upload
         */
        public void upload() {
            Progress progress = task.progress;
            switch (progress.status) {
                case Progress.PAUSE:
                case Progress.NONE:
                case Progress.ERROR:
                    task.start();
                    break;
                case Progress.LOADING:
                    task.pause();
                    break;
                case Progress.FINISH:
                    break;
            }
            refresh(progress);
        }

        /**
         * remove current item
         */
        public void remove() {
            task.remove();
            if (type == -1) {
                int removeIndex = -1;
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).path.equals(task.progress.tag)) {
                        removeIndex = i;
                        break;
                    }
                }
                if (removeIndex != -1) {
                    images.remove(removeIndex);
                }
                updateData(images);
            } else {
                updateData(type);
            }
        }

        /**
         * restart current task
         */
        public void restart() {
            task.restart();
        }
    }

    /**
     * upload listener
     */
    private class ListUploadListener extends UploadListener<String> {

        private ViewHolder holder;

        ListUploadListener(Object tag, ViewHolder holder) {
            super(tag);
            this.holder = holder;
        }

        @Override
        public void onStart(Progress progress) {
        }

        @Override
        public void onProgress(Progress progress) {
            if (tag == holder.getTag()) {
                holder.refresh(progress);
            }
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
        }

        @Override
        public void onFinish(String s, Progress progress) {
            Toast.makeText(context, "上传完成", Toast.LENGTH_SHORT).show();
            if (type != -1) updateData(type);
        }

        @Override
        public void onRemove(Progress progress) {
        }
    }
}

