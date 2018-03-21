package com.dylanvann.fastimage;

import android.content.Context;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.GlideUrl;

/**
 * Created by okct on 21/03/2018.
 */

class ImageViewWithUrl extends android.support.v7.widget.AppCompatImageView {
  public String defaultSource = "";
  public boolean circle;
  public GlideUrl glideUrl;
  public Priority priority;
  public String resizeMode;

  public ImageViewWithUrl(Context context) {
    super(context);
  }
}
