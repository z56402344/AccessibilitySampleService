package com.autoaide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.autoaide.R;
import com.autoaide.common.IAutoParams;
import com.autoaide.core.FileCenter;
import com.autoaide.frag.SplashFrag;

import z.db.ShareDB;

public class SplashActivity extends HomeAct implements OnGestureListener, IAutoParams {
    private GestureDetector mGestureDetector;
    private SplashFrag mSplashFrag;

    @Override
    protected void onEnterCreate(Bundle savedInstanceState) {
        mGestureDetector = new GestureDetector(this);
        // 隐藏目录中的文件 不被音乐播放器等搜索到
        FileCenter.hideAllMedia();
        if (savedInstanceState != null) return;
        pushFragment(mSplashFrag = new SplashFrag(), 0);
        updateScrVer();
    }

    // 版本升级后 更新信息
    private void updateScrVer() {
        // 应用升级了 需要加入的功能
        ShareDB.Sec sec = new ShareDB.Sec(kSec);
        // 获取设备通用参数
        Display defaultDisplay = this.getWindowManager().getDefaultDisplay();
        sec.put(kScrW, defaultDisplay.getWidth());
        sec.put(kScrH, defaultDisplay.getHeight());
        sec.put(kDensity, getResources().getDisplayMetrics().density);
        sec.save(false);
        mSplashFrag.saveViewHeight(0);
    }

    @Override
    public void onBackPressed() {
        gotoNextUI();
    }

    // 跳到下一个界面
    private void gotoNextUI() {
        if (mSplashFrag != null) {
            mSplashFrag.clearTimer();
        }
        Intent it = new Intent(this, HomeAct.class);
        it.putExtra(kFID, SplashFrag.FExit);
        startActivity(it);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public int onActMsg(int id, Object sender, int arg, Object extra) {
        if (id == SplashFrag.FExit) {
            gotoNextUI();
        }
        return 1;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;
        if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 600) {
            gotoNextUI();
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
