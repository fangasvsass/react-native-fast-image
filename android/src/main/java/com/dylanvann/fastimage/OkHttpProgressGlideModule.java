package com.dylanvann.fastimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.module.LibraryGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

//@GlideModule
public class OkHttpProgressGlideModule extends AppGlideModule {
    
    RequestOptions requestOptions;
    MemorySizeCalculator calculator;
    LruResourceCache lruResourceCache;
    LruBitmapPool lruBitmapPool;
    //    @Override
    //    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    //        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    //    }
    
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        if(requestOptions==null){
            requestOptions=new RequestOptions();
            requestOptions.dontTransform();
            //.fitCenter();
        }
        if(calculator==null){
            calculator=new MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(4)
            .setBitmapPoolScreens(2)
            .build();
        }
        if(lruResourceCache==null){
            lruResourceCache=new LruResourceCache(calculator.getMemoryCacheSize());
        }
        if(lruBitmapPool==null){
            lruBitmapPool=new LruBitmapPool(calculator.getBitmapPoolSize());
        }
        builder.setMemoryCache(lruResourceCache);
        builder.setBitmapPool(lruBitmapPool);
        builder.setDefaultRequestOptions(requestOptions);
        //        Log.i("size",calculator.getMemoryCacheSize()+"---"+calculator.getBitmapPoolSize());
        
    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}

