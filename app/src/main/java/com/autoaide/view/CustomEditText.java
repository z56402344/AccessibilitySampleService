package com.autoaide.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import z.frame.ICommon;

public class CustomEditText extends EditText implements ICommon {

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
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

}
