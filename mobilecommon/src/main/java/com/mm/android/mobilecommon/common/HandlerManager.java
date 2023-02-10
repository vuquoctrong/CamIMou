package com.mm.android.mobilecommon.common;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.mm.android.mobilecommon.base.BaseHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler Management classes that facilitate the creation of anonymous handlers and prevent memory leaks
 *
 * Handler管理类，方便界面创建匿名Handler，防止内存泄露
 */
public class HandlerManager {
    public  static final  String TAG = "HandlerManager";
    private List<WrapHandler>  mHandlers;

    public HandlerManager(){
        mHandlers = new ArrayList<>();
    }

    public static class WrapHandler extends  Handler{
        //真正外部使用的Handler，强引用防止回收，等处理之后解除引用
        private Handler mTarget;
        private WeakReference<List<WrapHandler>>  mHandlers;
        public WrapHandler(Handler target,WeakReference<List<WrapHandler>> handlers){
            mTarget = target;
            mHandlers = handlers;
        }

        public  Handler getTartgetHandler(){
            return mTarget;
        }

        @Override
        public void handleMessage(Message msg) {
            if(mTarget != null){
                mTarget.handleMessage(msg);
            }
            List<WrapHandler> handlers = mHandlers.get();
            if(handlers != null){
                //执行结束删除对应Handler
                handlers.remove(this);
            }
        }
    }

    private WrapHandler wrapHandler(@NonNull Handler handler){
        return new WrapHandler(handler, new WeakReference<>(mHandlers));
    }

    public Handler addHandler(@NonNull final Handler handler){
        WrapHandler wrapHandler = wrapHandler(handler);
        mHandlers.add(wrapHandler);
        return wrapHandler;
    }

    public void clearHandlers(){
        //取消数据回调
        for(Handler h : mHandlers){
            if(h instanceof BaseHandler){
                ((BaseHandler) h).cancle();
            }
        }
        mHandlers.clear();
    }
}
