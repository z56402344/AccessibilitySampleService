package com.autoaide.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import z.frame.ICommon;

public class CustomTextView extends TextView implements ICommon {

    //动画时长 ms
    int mDuration = 600;
    float number;

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        try {
            setTypeface(app.mTTf);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void numberAnim(int number) {
        if (Build.VERSION.SDK_INT >= 11){
            //修改number属性，会调用setNumber方法
            ObjectAnimator objectAnimator= ObjectAnimator.ofInt(this,"number",0,number);
            objectAnimator.setDuration(mDuration);
            //加速器，从慢到快到再到慢
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
        }else{
            setNumber(number);
        }
    }
    public float getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
        setText("+ "+number);
    }

}
