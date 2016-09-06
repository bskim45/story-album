package im.bsk.storyalbum;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.common.collect.Collections2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.bsk.storyalbum.widget.CameraPreview;

@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera_frame)
    FrameLayout mPreviewFrame;
    @BindView(R.id.camera_done)
    Button mBtnDone;

    private Camera mCamera;
    private int mMaxZoomLevel;
    private List<File> mTakenFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        initCamera();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void initCamera() {
        if (safeCameraOpen()) {
            mPreviewFrame.addView(new CameraPreview(this, mCamera));
        } else {
            Toast.makeText(this, "카메라를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Camera.Parameters params = mCamera.getParameters();

        if (params.isZoomSupported()) {
            mMaxZoomLevel = params.getMaxZoom();
            Log.i("max ZOOM ", "is " + mMaxZoomLevel);
        }

        // try to set auto focus
        if (params.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
        } else if (params.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
        }

        mCamera.enableShutterSound(true);
    }

    private boolean safeCameraOpen() {
        boolean result = false;

        try {
            releaseCameraAndPreview();
            int id = CameraUtil.getBackCameraId();

            if (id < 0) {
                Toast.makeText(this, "후면 카메라가 없습니다", Toast.LENGTH_SHORT).show();
                return false;
            }
            mCamera = Camera.open(id);
            mCamera.setDisplayOrientation(CameraUtil.getRelativeOrientation(id,
                    getWindowManager().getDefaultDisplay().getRotation()));
            result = (mCamera != null);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Open camera error", e);
        }

        return result;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera.PictureCallback mPictureCallback = (data, camera) -> {

        File file = CameraUtil.getCameraInternalFile(this);

        if (file == null) {
            Log.d(getClass().getSimpleName(), "Error creating media file, check storage permissions");
            return;
        }

        FileOutputStream fo = null;
        try {
            fo = (FileOutputStream) getContentResolver()
                    .openOutputStream(Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        OutputStream os = new BufferedOutputStream(fo, 8192);

        try {
            os.write(data);
        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), "Error writing file: " + e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(getClass().getSimpleName(), "Photo saved: " + file.getPath());
        mTakenFiles.add(file);

        // update button label
        mBtnDone.setText(mTakenFiles.size() == 0 ?
                "완료" : String.format("완료(%d)", mTakenFiles.size()));

        // restart preview
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            mCamera.startPreview();
        }
    };

    @OnClick(R.id.camera_shutter)
    void takeShot() {
        if (mCamera == null) return;

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.takePicture(null, null, mPictureCallback);
                } else {
                    Toast.makeText(CameraActivity.this, "초점이 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.camera_done)
    void savePhoto() {
        ArrayList<String> files = new ArrayList<>(Collections2.transform(mTakenFiles, File::getPath));

        Intent i = new Intent(this, NewStoryActivity.class);
        i.putExtra(NewStoryActivity.EXTRA_PHOTOS, files);

        startActivity(i);
    }

    @OnClick(R.id.camera_exit)
    void discardPhoto() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    cleanTempFiles();
                    CameraActivity.super.onBackPressed();
                }).show();
    }

    private void cleanTempFiles() {
        for (File takenFile : mTakenFiles) {
            takenFile.delete();
        }
    }
}