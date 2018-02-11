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
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.yoga.YogaConstants;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_ERROR_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_END_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_EVENT;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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

class FastImageViewManager extends SimpleViewManager<ImageViewWithUrl> implements ProgressListener {
    private static final String REACT_CLASS = "FastImageView";
    private static final String REACT_ON_LOAD_START_EVENT = "onFastImageLoadStart";
    private static final String REACT_ON_PROGRESS_EVENT = "onFastImageProgress";
    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);
    private static RequestManager requestManager = null;
    private final Map<String, List<ImageViewWithUrl>> VIEWS_FOR_URLS = new HashMap<>();
    private RequestOptions circleCrop = RequestOptions.circleCropTransform();
    //    private RequestOptions fitCenter = RequestOptions.fitCenterTransform();
//    private RequestOptions centerCrop = RequestOptions.centerCropTransform();
//    private MultiTransformation multiTransformation = new MultiTransformation(new FitCenter(), new CircleCrop());
    RequestOptions options;

    public FastImageViewManager() {
        options = new RequestOptions();
        options.format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .placeholder(TRANSPARENT_DRAWABLE);
    }

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
            // Cancel existing requests.
            requestManager.clear(view);
            if (view.glideUrl != null) {
                OkHttpProgressGlideModule.forget(view.glideUrl.toStringUrl());
            }
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
        requestManager.clear(view);
    }

    @ReactProp(name = ViewProps.RESIZE_MODE)
    public void setResizeMode(ImageViewWithUrl view, String resizeMode) {
        view.resizeMode = resizeMode;
    }


    @Override
    protected void onAfterUpdateTransaction(ImageViewWithUrl view) {
        String key = view.glideUrl.toStringUrl();
        OkHttpProgressGlideModule.expect(key, this);
        List<ImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null && !viewsForKey.contains(view)) {
            viewsForKey.add(view);
        } else if (viewsForKey == null) {
            List<ImageViewWithUrl> newViewsForKeys = new ArrayList(Arrays.asList(view));
            VIEWS_FOR_URLS.put(key, newViewsForKeys);
        }
        ThemedReactContext context = (ThemedReactContext) view.getContext();
        RCTEventEmitter eventEmitter = context.getJSModule(RCTEventEmitter.class);
        int viewId = view.getId();
        eventEmitter.receiveEvent(viewId, REACT_ON_LOAD_START_EVENT, new WritableNativeMap());
//        options.priority(view.priority);

//        if (view.circle && "cover".equals(view.resizeMode)) {
//            options = options.apply(bitmapTransform(multiTransformation));
//        } else {
        if (view.circle) {
            options = options.apply(circleCrop);
        }
//            else if ("cover".equals(view.resizeMode)) {
//                options = options.apply(fitCenter);
//            }
        ImageViewWithUrl.ScaleType scaleType = FastImageViewConverter.scaleType(view.resizeMode);
        view.setScaleType(scaleType);
//        }
        if (TextUtils.isEmpty(view.defaultSource)) {
            requestManager
                    .load(view.glideUrl.toStringUrl())
                    .apply(options)
//                    .listener(LISTENER)
                    .into(view);

        } else
            requestManager
                    .load(view.glideUrl.toStringUrl())
                    .thumbnail(requestManager.load(view.defaultSource))
                    .apply(options)
//                    .listener(LISTENER)
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
        final String key = view.glideUrl.toStringUrl();
        OkHttpProgressGlideModule.forget(key);
        List<ImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null) {
            viewsForKey.remove(view);
            if (viewsForKey.size() == 0) VIEWS_FOR_URLS.remove(key);
        }
        super.onDropViewInstance(view);
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                REACT_ON_LOAD_START_EVENT,
                MapBuilder.of("registrationName", REACT_ON_LOAD_START_EVENT),
                REACT_ON_PROGRESS_EVENT,
                MapBuilder.of("registrationName", REACT_ON_PROGRESS_EVENT),
                REACT_ON_LOAD_EVENT,
                MapBuilder.of("registrationName", REACT_ON_LOAD_EVENT),
                REACT_ON_ERROR_EVENT,
                MapBuilder.of("registrationName", REACT_ON_ERROR_EVENT),
                REACT_ON_LOAD_END_EVENT,
                MapBuilder.of("registrationName", REACT_ON_LOAD_END_EVENT)
        );
    }

    @Override
    public void onProgress(String key, long bytesRead, long expectedLength) {
        List<ImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null) {
            for (ImageViewWithUrl view : viewsForKey) {
                WritableMap event = new WritableNativeMap();
                event.putInt("loaded", (int) bytesRead);
                event.putInt("total", (int) expectedLength);
                ThemedReactContext context = (ThemedReactContext) view.getContext();
                RCTEventEmitter eventEmitter = context.getJSModule(RCTEventEmitter.class);
                int viewId = view.getId();
                eventEmitter.receiveEvent(viewId, REACT_ON_PROGRESS_EVENT, event);
            }
        }
    }

    private static RequestListener<Drawable> LISTENER = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model,
                                    Target<Drawable> target, boolean isFirstResource) {
            if (!(target instanceof ImageViewTarget)) {
                return false;
            }
            ImageViewWithUrl view = (ImageViewWithUrl) ((ImageViewTarget) target).getView();
            OkHttpProgressGlideModule.forget(view.glideUrl.toStringUrl());
            ThemedReactContext context = (ThemedReactContext) view.getContext();
            RCTEventEmitter eventEmitter = context.getJSModule(RCTEventEmitter.class);
            int viewId = view.getId();
            eventEmitter.receiveEvent(viewId, REACT_ON_ERROR_EVENT, new WritableNativeMap());
            eventEmitter.receiveEvent(viewId, REACT_ON_LOAD_END_EVENT, new WritableNativeMap());
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (!(target instanceof ImageViewTarget)) {
                return false;
            }
            ImageViewWithUrl view = (ImageViewWithUrl) ((ImageViewTarget) target).getView();
            ThemedReactContext context = (ThemedReactContext) view.getContext();
            RCTEventEmitter eventEmitter = context.getJSModule(RCTEventEmitter.class);
            int viewId = view.getId();
            eventEmitter.receiveEvent(viewId, REACT_ON_LOAD_EVENT, new WritableNativeMap());
            eventEmitter.receiveEvent(viewId, REACT_ON_LOAD_END_EVENT, new WritableNativeMap());
            return false;
        }
    };

    @Override
    public float getGranularityPercentage() {
        return 0.5f;
    }

}
