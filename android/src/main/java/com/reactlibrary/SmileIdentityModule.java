
package com.reactlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.smileidentity.libsmileid.core.CameraSourcePreview;
import com.smileidentity.libsmileid.core.SelfieCaptureConfig;
import com.smileidentity.libsmileid.core.SmartSelfieManager;
import com.smileidentity.libsmileid.core.captureCallback.FaceState;


import java.io.IOException;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.reactlibrary.Constants.CameraConfig.*;


public class SmileIdentityModule extends ReactContextBaseJavaModule
        implements LifecycleEventListener {

    private CameraSourcePreview mPreview;
    private SmartSelfieManager smartSelfieManager;
    private Activity currentActivity;
    private AlertDialog cameraDialog;

    private EventEmitter eventEmitter;
    private boolean captureSmileManually;

    SmileIdentityModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        eventEmitter = new EventEmitter(reactContext);
    }

    @Override
    public String getName() {
        return "SmileIdentity";
    }

    private void showCameraDialog(View customview) {
        if (cameraDialog == null)  {
            cameraDialog = new AlertDialog.Builder(currentActivity).create();

            cameraDialog.setView(customview);
            cameraDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Capture", ( v, i) -> takePicture());
            cameraDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss", ( v, i) -> cameraDialog.dismiss());
        }
        cameraDialog.show();
        (cameraDialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
    }

    @ReactMethod
    @SuppressWarnings("unused")
    private void captureSelfie(final ReadableMap options,
                               Callback onCompleteCallback) {
        float photoQuality = (float)(options.hasKey(PHOTO_QUALITY_KEY) ? options.getDouble(PHOTO_QUALITY_KEY) : DEFAULT_PHOTO_QUALITY);
        String fileName = options.hasKey(FILE_NAME_KEY) ? options.getString("fileName") : FILE_NAME_KEY;
        boolean smileRequired = !options.hasKey(SMILE_REQUIRED_KEY) || options.getBoolean(SMILE_REQUIRED_KEY);
        boolean cameraFacingFront = !options.hasKey(CAMERA_FRONT_KEY) || options.getBoolean(CAMERA_FRONT_KEY);
        this.captureSmileManually = !options.hasKey(CAPTURE_SMILE_KEY) || options.getBoolean(CAPTURE_SMILE_KEY);

        currentActivity = getCurrentActivity();
        if (currentActivity == null){
            return;
        }

        if (!Utilities.Permissions.checkAllPermissionsGranted(currentActivity)){
            eventEmitter.sendErrorEvent("Permissions not granted");
            return;
        }

        runOnUiThread( () -> {
            // Run this on the UI thread or it will end in tears.
            // React Native uses a bridging and UI thread, it will crash if you don't. Don't say I didn't tell you.

            if (smartSelfieManager != null) {
                smartSelfieManager.resume(); // So you don't reset the camera every time you retake a photo
                cameraDialog.show();
            } else {
                LayoutInflater inflater = (LayoutInflater)currentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // CameraSourcePreview has private a AttributeSet class, only chance of editing is by inflating.
                //If you instantiate, you will have to work with a small source preview size.
                mPreview = (CameraSourcePreview)(inflater.inflate(R.layout.camera_preview, null));
                showCameraDialog(mPreview);

                SelfieCaptureConfig config = getCaptureConfig(smileRequired, cameraFacingFront);
                if (config == null) {
                    eventEmitter.sendErrorEvent("Capture config invalid");
                    return;
                }
                smartSelfieManager = new SmartSelfieManager(config);
            }

            smartSelfieManager.setOnCompleteListener(new SmartSelfieManager.OnCompleteListener() {
                @Override
                public void onComplete(Bitmap bitmap) {
                    try {
                        String tempFilePath = Utilities.Files.saveBitmap(currentActivity, bitmap, photoQuality, fileName);
                        onCompleteCallback.invoke(tempFilePath);
                        smartSelfieManager.pause();
                    } catch (IOException ioexception) {
                        eventEmitter.sendErrorEvent(ioexception.getMessage());
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    eventEmitter.sendErrorEvent(throwable.getMessage());
                }
            });

            smartSelfieManager.setOnFaceStateChangeListener( faceState -> {
                        boolean showCaptureButton = faceState == FaceState.DO_SMILE && captureSmileManually;
                        int buttonVisibility = showCaptureButton ? View.VISIBLE : View.INVISIBLE;
                        if (cameraDialog != null) (cameraDialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(buttonVisibility);
                        eventEmitter.sendFaceChangedEvent(faceState);
                    }
            );

            smartSelfieManager.captureSelfie(PHOTO_TAG);
            smartSelfieManager.resume();
        });
    }

    @ReactMethod
    @SuppressWarnings("unused")
    private void stopCapturing() {
        onHostDestroy();
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean takePicture() {
        if (smartSelfieManager == null) {
            return false;
        }
        return smartSelfieManager.takePicture();
    }

    @Override
    public void onHostDestroy() {
        if (smartSelfieManager != null) smartSelfieManager.stop();
        if (cameraDialog != null && mPreview != null) cameraDialog.dismiss();
    }

    @Override
    public void onHostPause() {
        if (smartSelfieManager != null) smartSelfieManager.pause();
    }

    @Override
    public void onHostResume() {
        if (smartSelfieManager != null) smartSelfieManager.resume();
    }

    private SelfieCaptureConfig getCaptureConfig(boolean smileRequired, boolean cameraFacingFront) {
        if (currentActivity == null || mPreview == null) {
            return null;
        }
        return new SelfieCaptureConfig.Builder(currentActivity)
                .setCameraType(cameraFacingFront ? SelfieCaptureConfig.FRONT_CAMERA : SelfieCaptureConfig.BACK_CAMERA)
                .setPreview(mPreview)
                .setSetSmileRequired(smileRequired)
                .setManualSelfieCapture(captureSmileManually)
                .build();
    }
}
