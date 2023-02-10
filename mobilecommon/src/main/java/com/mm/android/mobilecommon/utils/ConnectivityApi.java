package com.mm.android.mobilecommon.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class ConnectivityApi {
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private static ConnectivityApi sInstance;
    private List<ConnectivityCallback> mConnectivityCallbacks = new ArrayList<>();

    public interface ConnectivityCallback {
        void onSuccess(Network network);
        void onFailed(int error);
    }

    private ConnectivityApi(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static ConnectivityApi instance(Context context) {
        if (sInstance == null) {
            sInstance = new ConnectivityApi(context);
        }
        return sInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void connectToWifiByNetworkRequest(String ssid, String password) {
        if (mNetworkCallback != null) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                mConnectivityManager.bindProcessToNetwork(null);
                mConnectivityManager.bindProcessToNetwork(network);
                for (ConnectivityCallback connectivityCallback:mConnectivityCallbacks) {
                    connectivityCallback.onSuccess(network);
                }
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                mConnectivityManager.bindProcessToNetwork(null);
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
                for (ConnectivityCallback connectivityCallback:mConnectivityCallbacks) {
                    connectivityCallback.onFailed(-1);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                mConnectivityManager.bindProcessToNetwork(null);
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
                for (ConnectivityCallback connectivityCallback:mConnectivityCallbacks) {
                    connectivityCallback.onFailed(-1);
                }
            }
        };

        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder().setSsid(ssid);
        if (!TextUtils.isEmpty(password)) {
            builder.setWpa2Passphrase(password);
        }
        WifiNetworkSpecifier specifier = builder.build();

        NetworkRequest networkRequestBuilder = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();

        mConnectivityManager.requestNetwork(networkRequestBuilder, mNetworkCallback);
    }

    public void disconnectWifiByNetworkRequest() {
        if (mNetworkCallback != null) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    public void addConnectivityCallback(ConnectivityCallback networkCallback) {
        if (networkCallback == null || mConnectivityCallbacks.contains(networkCallback))
            return;
        mConnectivityCallbacks.add(networkCallback);
    }

    public void removeConnectivityCallback(ConnectivityCallback networkCallback) {
        mConnectivityCallbacks.remove(networkCallback);
    }

    public void cancelConnectivityCallbacks() {
        mConnectivityCallbacks.clear();
    }

    public void release() {
        disconnectWifiByNetworkRequest();
        cancelConnectivityCallbacks();
        sInstance = null;
    }
}
