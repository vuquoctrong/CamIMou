package com.lechange.demo.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.FileInputStream;

public class MediaPlayHelper {

    public static BitmapDrawable picDrawable(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            return bd;
        } catch (Throwable e) {
            return null;
        }
    }
}
