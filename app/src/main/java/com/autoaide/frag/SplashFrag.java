package com.autoaide.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autoaide.R;
import com.autoaide.common.IAutoParams;
import com.autoaide.core.FidAll;

import z.frame.BaseFragment;

// 闪屏页面
public class SplashFrag extends BaseFragment implements View.OnClickListener, Runnable {
    public static final int FID = FidAll.SplashFragFID;
    public static final int FExit = FID + 1;
    private static final int IA_AutoParams = FID + 2;
    private static final int IA_FrameAnim = FID + 3;

    private boolean mInMain = false; // 自动关闭
    private Runnable mTimer = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.act_splash, null);
            initView();
            initAnim();
            setTimer(2000);
        }
        return mRoot;
    }

    private void initView() {
        Util.setText(mRoot, R.id.mTvVer, "version "+IAutoParams.Sec.loadString(IAutoParams.kVerS));
    }

    private void initAnim() {
        commitAction(IA_FrameAnim, 0, 0, 200);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        clearTimer();
        super.onDestroyView();
    }

    @Override
    public void run() {
        mTimer = null;
        gotoNextUI();
    }

    private void setTimer(int ms) {
        if (mInMain) return;
        clearTimer();
        mRoot.postDelayed(mTimer = this, ms);
    }

    public void clearTimer() {
        if (mTimer != null) {
            mRoot.removeCallbacks(mTimer);
            mTimer = null;
        }
    }

    @Override
    public void onClick(View v) {
        gotoNextUI();
    }

    // 跳到下一个界面
    private void gotoNextUI() {
        clearTimer();
        notifyActivity(FExit, 0, null);
    }

    @Override
    public boolean onBackPressed() {
        gotoNextUI();
        return true;
    }

    // 获取内部视图高度
    public void saveViewHeight(int ms) {
        int h = -1;
        if (mRoot == null || (h = mRoot.getMeasuredHeight()) == 0) {
            _log("viewH=" + h);
            commitAction(IA_AutoParams, 0, null, ms);
        } else {
            _log("viewH=" + h);
            IAutoParams.Sec.update(IAutoParams.kViewH, h);
        }
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            case IA_AutoParams: {
                saveViewHeight(20);
                break;
            }
            case IA_FrameAnim:
                //Splash所有动画序列
//                AnimUtil.alpAnim(mTvSlogan, true, 0, 1, 0, 900, false);
                break;
        }
    }
}
