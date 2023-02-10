package com.example.usermodule.net;

public interface IUserDataCallBack {
    void onCallBackOpenId(String str);
    /**
     * Error correction
     * @param throwable
     *
     * 错误回调
     * @param throwable
     */
    void onError(Throwable throwable);
}
