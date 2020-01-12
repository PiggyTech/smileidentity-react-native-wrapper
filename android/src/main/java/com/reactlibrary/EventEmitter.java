package com.reactlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.smileidentity.libsmileid.core.captureCallback.FaceState;

public class EventEmitter {
    private ReactContext reactContext;

    public EventEmitter(ReactContext reactContext) {
        this.reactContext = reactContext;
    }

    private void sendEvent(String eventName,
                           Object data) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, data);
    }

    public void sendFaceChangedEvent(
            @NonNull FaceState faceState) {
        sendEvent("FaceChanged", faceState.ordinal());
    }

    /**
    The method below is to keep things on the safe side.
    I've seen the erring method get invoked more than once
    Ideally these errors can be invoked but when you invoke a callback more than once,
    React native will crash. I consider React Native on it's own as a bug.
     **/
    public void sendErrorEvent(@Nullable String errorMessage) {
        sendEvent("Error", errorMessage == null ? "Unknown smartSelfieManager error" : errorMessage);
    }
}