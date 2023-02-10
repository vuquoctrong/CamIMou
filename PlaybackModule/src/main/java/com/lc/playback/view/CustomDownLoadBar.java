package com.lc.playback.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lc.playback.R;

public class CustomDownLoadBar extends FrameLayout {

    private TextView downloadNormalTv, downloadTotalIv;
    private ProgressBar downloadProgressBar;
    private ViewGroup downloadingLayout;
    private ImageView closeIv;

    public CustomDownLoadBar(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomDownLoadBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDownLoadBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomDownLoadBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_download_bar, this);
        downloadNormalTv = findViewById(R.id.download_normal_tv);
        downloadTotalIv = findViewById(R.id.download_total_tv);
        downloadProgressBar = findViewById(R.id.download_progress_bar);
        downloadingLayout = findViewById(R.id.downloading_layout);
        downloadProgressBar.setMax(100);
        closeIv = findViewById(R.id.close_iv);
    }

    public void startDownload() {
        Log.e("554433", "startDownload");
//        downloadNormalTv.setText(R.string.lc_demo_device_record_download);
        downloadNormalTv.setVisibility(View.GONE);
        downloadingLayout.setVisibility(View.VISIBLE);
    }

    public void stopDownload() {
        Log.e("554433", "stopDownload");
        downloadNormalTv.setText(R.string.lc_demo_device_record_download);
        downloadNormalTv.setVisibility(View.VISIBLE);
        downloadingLayout.setVisibility(View.GONE);
    }

    public void downloadFail() {
        Log.e("554433", "downloadFail");
        downloadNormalTv.setText(R.string.lc_demo_device_record_download_error);
        downloadNormalTv.setVisibility(View.VISIBLE);
        downloadingLayout.setVisibility(View.GONE);
    }

    public void downloadFinish() {
        Log.e("554433", "downloadFinish");
        downloadNormalTv.setText(R.string.lc_demo_device_record_download_finish);
        downloadNormalTv.setVisibility(View.VISIBLE);
        downloadingLayout.setVisibility(View.GONE);
    }

    public void setProgress(int progress) {
        Log.e("554433", "progress:" + progress);
        downloadProgressBar.setProgress(progress);
    }

    public void setSecondaryProgress(int secondProgress) {
        Log.e("554433", "secondProgress:" + secondProgress);
        downloadProgressBar.setSecondaryProgress(secondProgress);
    }

    public void setProgressTv(String progressTv) {
        downloadTotalIv.setText(progressTv);
    }

    public void setStopDownloadListener(View.OnClickListener stopDownloadListener) {
        closeIv.setOnClickListener(stopDownloadListener);
    }
}
