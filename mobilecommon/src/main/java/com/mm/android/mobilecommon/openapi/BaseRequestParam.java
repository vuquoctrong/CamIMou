package com.mm.android.mobilecommon.openapi;

import com.mm.android.mobilecommon.openapi.data.SystemData;

import java.io.Serializable;
import java.util.UUID;

public class BaseRequestParam implements Serializable {
    public String id = UUID.randomUUID().toString();
    public SystemData system = new SystemData();
}
