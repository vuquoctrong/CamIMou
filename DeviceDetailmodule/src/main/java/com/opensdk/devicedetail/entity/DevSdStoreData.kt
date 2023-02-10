package com.opensdk.devicedetail.entity

import com.google.gson.Gson
import com.google.gson.JsonObject

class DevSdStoreData {
    class Response {
        var data: DevSdStoreData? = null
        fun parseData(json: JsonObject) {
            val gson = Gson()
            data = gson.fromJson(json.toString(), DevSdStoreData::class.java)
        }
    }

    var totalBytes: String? = null
    var usedBytes: String? = null


    override fun toString(): String {
        return "DevStoreInfo{" +
                "totalBytes='" + totalBytes + '\'' +
                ", usedBytes='" + usedBytes + '\'' +
                '}'
    }
}