package com.zxing;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import com.google.zxing.Result;
import com.zxing.camera.CameraManager;


public interface IZxingActivity {
    Handler getHandler();

    void startActivity(Intent intent);

    void setResult(int code, Intent intent);

    void finish();

    void drawViewfinder();

    CameraManager getCameraManager();

    Rect getCropRect();

    void handleDecode(Result obj, Bundle bundle);
}
