package com.k12lib.afast.view.pulltorefresh.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.k12lib.afast.view.pulltorefresh.ILoadingLayout;

/**
 * Created by duguang on 15-10-15.
 */
public class LoadingLayout  extends FrameLayout implements ILoadingLayout {

    public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingLayout(Context context) {
        super(context);
    }

    private void setSubTextAppearance(int value) {}

    private void setSubTextColor(ColorStateList color) {}

    private void setTextAppearance(int value) {}

    private void setTextColor(ColorStateList color) {}

    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }

    @Override
    public void setLoadingDrawable(Drawable drawable) {

    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {

    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {

    }

    @Override
    public void setTextTypeface(Typeface tf) {

    }
}
