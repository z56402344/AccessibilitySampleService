package com.k12lib.afast.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import z.frame.ICommon;
import z.image.universal_image_loader.core.DisplayImageOptions;
import z.image.universal_image_loader.core.ImageLoader;

// 回收图片的imageView 从屏幕上移除后就释放掉图片
public class RecycleImageView extends ImageView implements ICommon {

    private String mUrl;
    private DisplayImageOptions mOptions;
    private boolean mIsAttached = false;
    private boolean mIsChange = false;

    public RecycleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView init(int resId) {
        return init(resId,mOptions);
    }

    public RecycleImageView init(int resId, DisplayImageOptions op) {
        return init(new StringBuilder(64).append("drawable://").append(resId).append('?').append(app.curVersion).toString(), op);
    }

    public RecycleImageView init(String url) {
        return init(url,mOptions);
    }

    public RecycleImageView init(String url, DisplayImageOptions op) {
        if (TextUtils.isEmpty(url) && op != null && op.shouldShowImageForEmptyUri()) {
            Drawable d = op.getImageForEmptyUri(getResources());
            setImageDrawable(d);
            if (d instanceof AnimationDrawable) {
                ((AnimationDrawable) d).start();
            }
            mUrl = url;
            return this;
        }
        if (mUrl == url || (mUrl != null && url != null && url.equals(mUrl))) {
            mUrl = url;
            return this;
        }
        mUrl = url;
        mOptions = op;
        mIsChange = true;
        if (mIsAttached) {
            ImageLoader.getInstance().displayImage(mUrl, this, mOptions);
        }
        return this;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadOrFreeImage(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        loadOrFreeImage(false);
    }

    protected void loadOrFreeImage(boolean load) {
        if (load) {
            if (!mIsAttached) {
                mIsAttached = true;
                if (mIsChange && !TextUtils.isEmpty(mUrl)) {
                    ImageLoader.getInstance().displayImage(mUrl, this, mOptions);
                }
            }
        } else {
            if (mIsAttached) {
                mIsAttached = false;
                if (!TextUtils.isEmpty(mUrl)) {
                    mIsChange = true;
                    setImageDrawable(null);
                }
            }
        }
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        loadOrFreeImage(false);
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        loadOrFreeImage(true);
    }
}
