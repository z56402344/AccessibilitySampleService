package com.autoaide.core;

import android.view.View;
import android.widget.ImageView;

import com.autoaide.R;
import com.k12lib.afast.view.RecycleImageView;

import z.frame.BaseFragment;
//import z.frame.IAct;

// 标题设置
public class TitleFrag extends BaseFragment implements View.OnClickListener {
    protected View mTitle;
    private RecycleImageView mIvMain;

    public View getTitle() {
        if (mTitle == null) {
            if (mRoot != null)
                mTitle = mRoot.findViewById(R.id.RlTitle);
        }
        return mTitle;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    // 设置标题 支持string和resId
//    public void setTitle(Object left, Object title, Object right) {
//        View t = getTitle();
//        if (t == null) return;
//        // 左边文字
//        setLeftText(left);
//        // 中间标题
//        setTitleText(title);
//        // 右边文字
//        setRightText(right);
//        t.setVisibility(View.VISIBLE);
//    }

    // 设置标题 左右为图片
    public void setTitle(int leftRes, String title, String rightRes) {
        View t = getTitle();
        if (t == null) return;
        // 左边图片
        setLeftImage(leftRes);
        // 中间标题
        setTitleText(title);
        // 右边文字
        setRightText(rightRes);
        t.setVisibility(View.VISIBLE);
    }

    // 设置标题 左右为文字
    public void setTitle(String leftRes, String title, String rightRes) {
        View t = getTitle();
        if (t == null) return;
        // 左边文字
        setLeftText(leftRes);
        // 中间标题
        setTitleText(title);
        // 右边文字
        setRightText(rightRes);
        t.setVisibility(View.VISIBLE);
    }

    public void setLeftImage(int resId) {
        View t = getTitle();
        if (t == null) return;
        ImageView leftIcon = (ImageView) t.findViewById(R.id.iv_left);
        if (leftIcon == null) return;
        leftIcon.setImageResource(resId);
        leftIcon.setVisibility(View.VISIBLE);
        View leftLayout = Util.setVisible(t, R.id.left, View.VISIBLE);
        Util.setVisible(t, R.id.tv_left, View.GONE);
        if (leftLayout == null) return;
        leftLayout.setOnClickListener(this);
    }

    // 左边文字
    public View setLeftText(Object left) {
        View t = getTitle();
        if (left != null) {
            View v = Util.setVisible(t, R.id.left, View.VISIBLE);
            if (v != null) v.setOnClickListener(this);
            return Util.setText(t, R.id.tv_left, left);
        } else {
            return Util.setVisible(t, R.id.left, View.INVISIBLE);
        }
    }

    // 中间标题
    public View setTitleText(Object title) {
        View t = getTitle();
        View leftView = Util.setVisible(t, R.id.left, View.VISIBLE);
        if (leftView != null) leftView.setOnClickListener(this);
        if (title != null) {
            Util.setVisible(t, R.id.middle, View.VISIBLE);
            return Util.setText(t, R.id.tv_title, title);
        } else {
            return Util.setVisible(t, R.id.middle, View.INVISIBLE);
        }
    }

    // 右边文字
    public View setRightText(Object right) {
        View t = getTitle();
        if (right != null) {
            View v = Util.setVisible(t, R.id.right, View.VISIBLE);
            if (v != null) v.setOnClickListener(this);
            return Util.setText(t, R.id.tv_right, right);
        } else {
            return Util.setVisible(t, R.id.right, View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: // 默认返回上个界面
                pop(true);
                break;
        }
    }
}
