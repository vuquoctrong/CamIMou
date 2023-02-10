package com.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.zxing.camera.open.OpenCameraInterface;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 *
 * 该对象包装Camera服务对象，并期望成为唯一与之通信的对象。该实现封装了获取预览大小的图像所需的步骤，这些图像用于预览和解码。
 */
public class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private final Context context;
    private final CameraConfigurationManager configManager;
    /**
     * Preview frames are delivered here, which we pass on to the registered
     * handler. Make sure to clear the handler so it will only receive one
     * message.
     *
     * 预览帧在这里被交付，我们将其传递给注册的处理程序。确保清除处理程序，以便它只接收一条消息。
     */
    private final PreviewCallback previewCallback;
    private Camera camera;
    private AutoFocusManager autoFocusManager;
    private boolean initialized;
    private boolean previewing;
    private int requestedCameraId = -1;

    public CameraManager(Context context) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager);
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     *
     * 打开摄像机驱动程序，初始化硬件参数。
     * @param holder 摄像机将绘制预览帧的表面对象。
     * @throws IOException 表示打开摄像头驱动程序失败。
     */
    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        Camera theCamera = camera;
        if (theCamera == null) {

            if (requestedCameraId >= 0) {
                theCamera = OpenCameraInterface.open(requestedCameraId);
            } else {
                theCamera = OpenCameraInterface.open();
            }

            if (theCamera == null) {
                throw new IOException();
            }
            camera = theCamera;
        }
        theCamera.setPreviewDisplay(holder);

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(theCamera);
        }

        Camera.Parameters parameters = theCamera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save
        // these,
        // temporarily
        try {
            configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException re) {
            // Driver failed
            Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            // Reset:
            if (parametersFlattened != null) {
                parameters = theCamera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    theCamera.setParameters(parameters);
                    configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException re2) {
                    // Well, darn. Give up
                    Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }

    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    /**
     * Closes the camera driver if still in use.
     *
     * 如果仍在使用，则关闭相机驱动程序。
     */
    public synchronized void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
            // Make sure to clear these each time we close the camera, so that
            // any scanning rect
            // requested by intent is forgotten.
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     *
     * 要求相机硬件开始在屏幕上绘制预览帧。
     */
    public synchronized void startPreview() {
        Camera theCamera = camera;
        if (theCamera != null && !previewing) {
            theCamera.startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(context, camera);
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     *
     * 告诉相机停止绘制预览帧。
     */
    public synchronized void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data
     * will arrive as byte[] in the message.obj field, with width and height
     * encoded as message.arg1 and message.arg2, respectively.
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     *
     * 单个预览帧将返回给提供的处理程序。数据将作为字节[]在消息中到达。Obj字段，宽度和高度编码为消息。arg1和消息arg2。分别最长。
     * @param handler 要将消息发送到的处理程序。
     * @param message 要发送的消息的什么字段
     */
    public synchronized void requestPreviewFrame(Handler handler, int message) {
        Camera theCamera = camera;
        if (theCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            theCamera.setOneShotPreviewCallback(previewCallback);
        }
    }

    /**
     * Allows third party apps to specify the camera ID, rather than determine
     * it automatically based on available cameras and their orientation.
     * @param cameraId camera ID of the camera to use. A negative value means "no preference".
     *
     * 允许第三方应用程序指定相机ID，而不是根据可用的相机和它们的方向自动确定。
     * @param cameraId camera要使用的摄像头ID。负值表示“无偏好”。
     */
    public synchronized void setManualCameraId(int cameraId) {
        requestedCameraId = cameraId;
    }

    /**
     * Obtaining Camera Resolution
     *
     * 获取相机分辨率
     */
    public Point getCameraResolution() {
        return configManager.getCameraResolution();
    }

    public Camera.Size getPreviewSize() {
        if (null != camera) {
            return camera.getParameters().getPreviewSize();
        }
        return null;
    }
}
