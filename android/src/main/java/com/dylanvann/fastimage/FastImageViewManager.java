package com.dylanvann.fastimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

class ImageViewWithUrl extends ImageView {
    public String defaultSource = "";
    public boolean circle;
    public GlideUrl glideUrl;
    public Priority priority;
    public String resizeMode;
    
    public ImageViewWithUrl(Context context) {
        super(context);
    }
}

class FastImageViewManager extends SimpleViewManager<ImageViewWithUrl>  {
    private static final String REACT_CLASS = "FastImageView";
    private static final String REACT_ON_LOAD_START_EVENT = "onFastImageLoadStart";
    private static final String REACT_ON_PROGRESS_EVENT = "onFastImageProgress";
    //    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
    private static RequestManager requestManager = null;
    private final Map<String, List<ImageViewWithUrl>> VIEWS_FOR_URLS = new HashMap<>();
    private RequestOptions circleCrop = RequestOptions.circleCropTransform();
    RequestOptions requestOptions;
    public  FastImageViewManager(){
        requestOptions=new RequestOptions();
    }
    
    @Override
    public String getName() {
        return REACT_CLASS;
    }
    
    @Override
    protected ImageViewWithUrl createViewInstance(ThemedReactContext reactContext) {
        if (requestManager == null) {
            requestManager = Glide.with(reactContext);
        }
        return new ImageViewWithUrl(reactContext);
    }
    
    @ReactProp(name = "source")
    public void setSrc(ImageViewWithUrl view, @Nullable ReadableMap source) {
        if (source == null) {
            // Cancel existing requests.
            // requestManager.clear(view);
            // Clear the image.
            view.setImageDrawable(null);
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
        if (view.circle) {
            requestOptions = requestOptions.apply(circleCrop);
        }
        requestOptions.priority(view.priority);
        ImageViewWithUrl.ScaleType scaleType = FastImageViewConverter.scaleType(view.resizeMode);
        view.setScaleType(scaleType);
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

