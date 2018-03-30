package com.autoaide.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.autoaide.R;
import com.autoaide.core.FidAll;
import com.autoaide.service.AccessibilityOperator;
import com.autoaide.utils.AccessibilityLog;

import z.frame.BaseAct;

/**
 *演示的Demo
 */
public class EleAct extends BaseAct {

    public static final int FID = FidAll.EleActFID;
    public static final int IA_CLICK = FID + 1;


    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_normal_sample);
        initViews();
    }

    private void initViews() {

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.normal_sample_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commitAction(IA_CLICK,0,null,2000);
    }

    private void simulationKidClickById() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickById("me.ele.crowdsource:id/button2");
                AccessibilityLog.printLog(result ? "取消成功1" : "取消失败1");
                if (!result){
                    result = AccessibilityOperator.getInstance().clickByText("取消");
                }
                AccessibilityLog.printLog(result ? "取消成功2" : "取消失败2");

            }
        }, 4000);
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id){
        case IA_CLICK:
            simulationKidClickById();
            commitAction(IA_CLICK,0,null,2000);
            break;
        }
        super.handleAction(id, arg, extra);
    }
}
