package com.mm.android.mobilecommon.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.mm.android.mobilecommon.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AsyncTask for reverse geocoding coordinates into a physical address.
 * include country/city/street etc.
 *
 * AsyncTask用于反向地理编码坐标到物理地址。
 * 包括国家/城市/街道等。
 */
public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private Context mContext;
    private OnGeoDecodeCompleted mListener;
    private final String TAG = "FetchAddressTask";

    public FetchAddressTask(Context applicationContext, OnGeoDecodeCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Location... params) {
        // Set up the geocoder
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        // Get the passed in location
        Location location = params[0];
        List<Address> addresses = null;
        String resultMessage = "";

        if (location == null) {
            LogUtil.debugLog(TAG, "location has't got");
            return "";
        }


        try {
            // In this sample, get just a single address
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            LogUtil.errorLog(TAG, "Catch network or other I/O problems -> service not available", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            LogUtil.errorLog(TAG, "invalid latitude or longitude used" + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // If no addresses found, print an error message.
        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                LogUtil.debugLog(TAG, "no address found");
            }
            return "";
        }
        // If an address is found, read it into resultMessage
        Address address = addresses.get(0);
        ArrayList<String> addressParts = new ArrayList<>();

        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressParts.add(address.getAddressLine(i));
        }
        resultMessage = address.getCountryCode();
        LogUtil.debugLog(TAG, "address info -> " + resultMessage + " || " + TextUtils.join("\n", addressParts));

        return resultMessage;
    }

    /**
     * Called once the background thread is finished and updates the
     * UI with the result.
     *
     * @param address The resulting reverse geocoded address, or error
     *                message if the task failed.
     */
    @Override
    protected void onPostExecute(String address) {
        mListener.onGeoDecodeCompleted(address);
        super.onPostExecute(address);
    }

    //反编码回调接口
    public interface OnGeoDecodeCompleted {
        void onGeoDecodeCompleted(String result);
    }
}
