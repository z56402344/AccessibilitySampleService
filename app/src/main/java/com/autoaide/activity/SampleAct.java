package com.autoaide.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.autoaide.service.AccessibilityOperator;
import com.autoaide.R;
import com.autoaide.utils.AccessibilityLog;

import z.frame.BaseAct;

/**
 *演示的Demo
 */
public class SampleAct extends BaseAct {

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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                simulationClickByText();
//                simulationClickById();
                simulationKidClickById();
            }
        }, 2000);
    }

    private void simulationClickByText() {
        boolean result = AccessibilityOperator.getInstance().clickByText("复选框开关");
        AccessibilityLog.printLog(result ? "复选框模拟点击成功" : "复选框模拟点击失败");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("单选按钮");
                AccessibilityLog.printLog(result ? "单选按钮模拟点击成功" : "单选按钮模拟点击失败");
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("OFF");
                AccessibilityLog.printLog(result ? "OnOff开关模拟点击成功" : "OnOff开关模拟点击失败");
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("退出本页面");
                AccessibilityLog.printLog(result ? "退出本页面模拟点击成功" : "退出本页面模拟点击失败");
            }
        }, 6000);
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

    private void simulationClickById() {
        boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_checkbox");
        AccessibilityLog.printLog(result ? "复选框模拟点击成功" : "复选框模拟点击失败");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_radiobutton");
                AccessibilityLog.printLog(result ? "单选按钮模拟点击成功" : "单选按钮模拟点击失败");
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_togglebutton");
                AccessibilityLog.printLog(result ? "OnOff开关模拟点击成功" : "OnOff开关模拟点击失败");
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_back");
//                AccessibilityLog.printLog(result ? "退出本页面模拟点击成功" : "退出本页面模拟点击失败");
                // 下面这个模拟点击系统返回键
                boolean result = AccessibilityOperator.getInstance().clickBackKey();
                AccessibilityLog.printLog(result ? "返回键模拟点击成功" : "返回键模拟点击失败");
            }
        }, 6000);
    }
}
