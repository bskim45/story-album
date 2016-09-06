package im.bsk.storyalbum;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("deprecation")
public class CameraUtil {
    public static int getBackCameraId() {
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }

        return -1;
    }

    public static int getOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        return info.orientation;
    }

    public static int getRelativeOrientation(int cameraId, int displayRotation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int degrees = 0;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }

        int relativeOrientation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            relativeOrientation = (info.orientation + degrees) % 360;
            relativeOrientation = (360 - relativeOrientation) % 360; // compensate the mirror
        } else { // back-facing
            relativeOrientation = (info.orientation - degrees + 360) % 360;
        }

        Log.d(CameraUtil.class.getSimpleName(), "rotation cam / phone = relativeOrientation: "
                + info.orientation + " / " + degrees + " = " + relativeOrientation);

        return relativeOrientation;
    }

    @Nullable
    public static File getCameraInternalFile(Context context){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssS").format(new Date());
        String fileName = "IMG_"+ timeStamp + ".jpg";

        return FileUtil.createInternalFile(context, fileName);
    }
}
