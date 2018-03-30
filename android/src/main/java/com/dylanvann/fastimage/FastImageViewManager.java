package com.dylanvann.fastimage;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

class FastImageViewManager extends SimpleViewManager<ImageViewWithUrl> {
    private static final String REACT_CLASS = "FastImageView";
    private static RequestManager requestManager = null;
    private RequestOptions circleCrop = RequestOptions.circleCropTransform();
    private RequestOptions fitCenter = RequestOptions.fitCenterTransform();
    private MultiTransformation multiTransformation = new MultiTransformation(new FitCenter(), new CircleCrop());
    
    @Override
    public String getName() {
        return REACT_CLASS;
    }
    
    @Override
    protected ImageViewWithUrl createViewInstance(ThemedReactContext reactContext) {
        if (requestManager == null) {
            requestManager = Glide.with(reactContext.getApplicationContext());
        }
        return new ImageViewWithUrl(reactContext);
    }
    
    @ReactProp(name = "source")
    public void setSrc(ImageViewWithUrl view, @Nullable ReadableMap source) {
        if (source == null) {
            return;
        }
        // Get the GlideUrl which contains header info.
        GlideUrl glideUrl = FastImageViewConverter.glideUrl(source);
        view.glideUrl = glideUrl;
        // Get priority.
        final Priority priority = FastImageViewConverter.priority(source);
        view.priority = priority;
    }
    
    @ReactProp(name = ViewProps.RESIZE_MODE)
    public void setResizeMode(ImageViewWithUrl view, String resizeMode) {
        view.resizeMode = resizeMode;
    }
    
    
    @Override
    protected void onAfterUpdateTransaction(ImageViewWithUrl view) {
        RequestOptions requestOptions = new RequestOptions();
        if (view.circle) {
            requestOptions = requestOptions.apply(circleCrop);
        }
        requestOptions.priority(view.priority);
        
        if (view.circle && "cover".equals(view.resizeMode)) {
            requestOptions = requestOptions.apply(bitmapTransform(multiTransformation));
        } else {
            if (view.circle) {
                requestOptions = requestOptions.apply(circleCrop);
            } else if ("cover".equals(view.resizeMode)) {
                requestOptions = requestOptions.apply(fitCenter);
            }
            ImageViewWithUrl.ScaleType scaleType = FastImageViewConverter.scaleType(view.resizeMode);
            view.setScaleType(scaleType);
        }
        
        if (TextUtils.isEmpty(view.defaultSource)) {
            requestManager
            .load(view.glideUrl.toStringUrl())
            .apply(requestOptions)
            .into(view);
            
        } else
            requestManager
            .load(view.glideUrl.toStringUrl())
            .thumbnail(requestManager.load(view.defaultSource))
            .apply(requestOptions)
            .into(view);
        super.onAfterUpdateTransaction(view);
    }
    
    
    @ReactProp(name = "circle")
    public void setCircle(ImageViewWithUrl view, Boolean circle) {
        try {
            view.circle = circle;
        } catch (Exception e) {
        }
    }
    
    @ReactProp(name = "defaultSource")
    public void setDefaultSource(ImageViewWithUrl view, @Nullable ReadableMap defaultSource) {
        try {
            view.defaultSource = defaultSource.getString("uri");
        } catch (Exception e) {
        }
    }
    
    @Override
    public void onDropViewInstance(ImageViewWithUrl view) {
        // This will cancel existing requests.
        requestManager.clear(view);
        super.onDropViewInstance(view);
    }
    
    
}


