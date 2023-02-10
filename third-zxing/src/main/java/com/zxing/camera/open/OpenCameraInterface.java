package com.zxing.camera.open;

import android.hardware.Camera;
import android.util.Log;

public class OpenCameraInterface {

    private static final String TAG = OpenCameraInterface.class.getName();

    /**
     * Opens the requested camera with {@link Camera#open(int)}, if one exists.
     *
     * @param cameraId camera ID of the camera to use. A negative value means
     *                 "no preference"
     * @return handle to {@link Camera} that was opened
     *
     * 使用{@link Camera#open(int)},打开请求的相机(如果存在的话)。
     *
     * @param cameraId camera要使用的摄像头ID。负值表示“无偏好”
     * @return 已打开的 {@link Camera}句柄
     */
    public static Camera open(int cameraId) {

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.w(TAG, "No cameras!");
            return null;
        }

        boolean explicitRequest = cameraId >= 0;

        if (!explicitRequest) {
            // Select a camera if no explicit camera requested
            int index = 0;
            while (index < numCameras) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(index, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                index++;
            }

            cameraId = index;
        }

        Camera camera;
        if (cameraId < numCameras) {
            Log.i(TAG, "Opening camera #" + cameraId);
            camera = Camera.open(cameraId);
        } else {
            if (explicitRequest) {
                Log.w(TAG, "Requested camera does not exist: " + cameraId);
                camera = null;
            } else {
                Log.i(TAG, "No camera facing back; returning camera #0");
                camera = Camera.open(0);
            }
        }

        return camera;
    }

    /**
     * Opens a rear-facing camera with {@link Camera#open(int)}, if one exists,
     * or opens camera 0.
     * @return handle to {@link Camera} that was opened
     *
     * 使用  {@link Camera#open(int)} 打开后置相机(如果有的话)，或者打开相机0。
     * @return 已打开的 {@link Camera}句柄
     */
    public static Camera open() {
        return open(-1);
    }

}
