package com.k12lib.afast.view;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;
import android.widget.TextView;

import z.frame.ICommon;

/**
 * Created by davinci42 on 8/30/16.
 */
public class RoundBackgroundSpan extends ReplacementSpan implements ICommon {

    private TextView mTextView;
    
    private int mMarginLeft, mMarginRight;
    private int mBgWidth, mBgHeight;

    private int mRadius;
    private int mTextSize;
    private int mTextColor;
    private int mBgColor;

    private float mTextWidth;
    private Paint.FontMetrics mFontMetric;

    public RoundBackgroundSpan(TextView tv) {
        mTextView = tv;
        mTextColor = 0xffffffff;
        mTextSize = app.px(14);
    }

    private float getTextSize(Paint paint, CharSequence sequence, int start, int end) {
        paint.setTextSize(mTextSize);
        mFontMetric = paint.getFontMetrics();
        mTextWidth = paint.measureText(sequence, start, end);
        return mTextWidth;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mTextWidth = getTextSize(paint, text, start, end);
        return Math.round(mBgWidth + mMarginLeft + mMarginRight);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Paint.FontMetricsInt fm = mTextView.getPaint().getFontMetricsInt();

        int bgTop = (fm.ascent + fm.descent) / 2 + y - mBgHeight / 2;
        RectF bgRectF = new RectF(
                x + mMarginLeft,
                bgTop,
                x + mMarginLeft + mBgWidth,
                bgTop + mBgHeight);
        paint.setColor(mBgColor);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(bgRectF, mRadius, mRadius, paint);

        float centerY = (bgRectF.top + bgRectF.bottom) / 2;
        float textX = bgRectF.left + (mBgWidth - mTextWidth) / 2;
        float textY = centerY + (mFontMetric.descent - mFontMetric.ascent) / 2 - (mFontMetric.descent);
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, textX, textY, paint);
    }

    public RoundBackgroundSpan setBgSize(int w, int h) {
        mBgWidth = app.px(w);
        mBgHeight = app.px(h);
        return this;
    }

    public RoundBackgroundSpan setMargin(int l, int r) {
        mMarginLeft = app.px(l);
        mMarginRight = app.px(r);
        return this;
    }

    public RoundBackgroundSpan setTextSize(int sp) {
        mTextSize = app.px(sp);
        return this;
    }

    public RoundBackgroundSpan setRadius(int dp) {
        mRadius = app.px(dp);
        return this;
    }

    public RoundBackgroundSpan setTextColor(int textColor) {
        mTextColor = textColor;
        return this;
    }

    public RoundBackgroundSpan setBgColor(int bgColor) {
        mBgColor = bgColor;
        return this;
    }
}
