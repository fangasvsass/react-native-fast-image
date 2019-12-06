package com.dylanvann.fastimage;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.GlideUrl;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by okct on 21/03/2018.
 */

class PhotoViewWithUrl extends PhotoView {
    public String defaultSource = "";
    public boolean circle;
    public GlideUrl glideUrl;
    public Priority priority;
    public String resizeMode;

    public PhotoViewWithUrl(Context context) {
        super(context);
        this.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                PhotoViewWithUrl.this.onReceiveNativeEvent();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }



    public void onReceiveNativeEvent() {
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onPhotoTapListener",
                null);
    }
}

