package com.mm.android.deviceaddmodule.openapi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PolicyData implements Serializable {
    public List<StateMent> statement = new ArrayList<>();

    public static class StateMent implements Serializable{
        public String permission;
        public List<String> resource = new ArrayList<>();
    }

}
