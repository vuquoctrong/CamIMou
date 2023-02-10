package com.mm.android.mobilecommon.AppConsume;

public interface Callback<T> {

    public void onSuccess(T t);

    public void onFail(String code, String message);
}
