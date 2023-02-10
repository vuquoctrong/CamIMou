package com.mm.android.mobilecommon.eventbus.event;

/**
 * EventBus Message base class
 *
 * EventBus消息基类
 */

public class BaseEvent {
    private String code;

    public BaseEvent(String code){
        this.code=code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
