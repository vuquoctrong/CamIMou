package com.mm.android.deviceaddmodule.openapi.data;

import java.io.Serializable;

public class AddDevicePolicyParam implements Serializable {
    public String openid;
    public String token;
    public PolicyData policy = new PolicyData();
}
