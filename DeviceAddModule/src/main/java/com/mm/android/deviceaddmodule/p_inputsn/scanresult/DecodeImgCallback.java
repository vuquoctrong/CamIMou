package com.mm.android.deviceaddmodule.p_inputsn.scanresult;

import com.google.zxing.Result;

/**
 * Callback to parse the image
 *
 * 解析图片的回调
 */

public interface DecodeImgCallback {
    void onImageDecodeSuccess(Result result);

    void onImageDecodeFailed();
}
