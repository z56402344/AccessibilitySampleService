package com.autoaide.frag.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.autoaide.R;
import com.autoaide.activity.AccessibilityOpenHelperActivity;
import com.autoaide.activity.EleAct;
import com.autoaide.core.FidAll;
import com.autoaide.service.AccessibilityOperator;
import com.autoaide.utils.AccessibilityLog;
import com.autoaide.view.CustomEditText;

import z.frame.BaseFragment;
import z.frame.DelayAction;

// 主界面
public class HomeFrag extends BaseFragment {

    public static final int FID = FidAll.HomeFragFID;
    public static final int IA_AutoLogin = FID + 2;
    public static final int IA_CLICK = FID + 3;

    private static final String ACTION = "action";
    private static final String ACTION_START_ACCESSIBILITY_SETTING = "action_start_accessibility_setting";

    private TextView mTvStart;
    private CustomEditText mEtTime,mEtName;
    private Button mBtnStop,mBtnStart;

    private DelayAction mDelay = new DelayAction(); // 防止点击太频繁
    private long mPressedTime;
    private String mName = "确认";//轮询的查找的按钮名称
    private int mTime = 200;//轮询的间隔时间
    private boolean isStop = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_home, null);
            initView();
            initData();
            AccessibilityOperator.getInstance().init(mRoot.getContext());
        }
        return mRoot;
    }

    private void initView() {
        mEtTime = (CustomEditText) findViewById(R.id.mEtTime);
        mEtName = (CustomEditText) findViewById(R.id.mEtName);
        mTvStart = (TextView) findViewById(R.id.mTvStart);
        mBtnStop = (Button) findViewById(R.id.mBtnStop);
        mBtnStart = (Button) findViewById(R.id.mBtnStart);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) return;
        switch (v.getId()) {
        case R.id.open_accessibility_setting:
            jumpToSettingPage(mRoot.getContext());
            break;
        case R.id.mBtnStart:
//            startActivity(new Intent(mRoot.getContext(), SampleAct.class));
            startActivity(new Intent(mRoot.getContext(), EleAct.class));
//            mBtnStop.setVisibility(View.VISIBLE);
//            mBtnStart.setVisibility(View.GONE);
//            isStop = false;
//            mName = mEtName.getText().toString();
//            mTime = Integer.valueOf(mEtTime.getText().toString());
//            commitAction(IA_CLICK,0,null,5000);
            break;
        case R.id.mBtnStop:
            mBtnStop.setVisibility(View.GONE);
            mBtnStart.setVisibility(View.VISIBLE);
            isStop = true;
            removeActionById(IA_CLICK);
            break;
        }
    }

    //跳转到无障碍服务页面
    public static void jumpToSettingPage(Context context) {
        try {
            Intent intent = new Intent(context,  AccessibilityOpenHelperActivity.class);
            intent.putExtra(ACTION, ACTION_START_ACCESSIBILITY_SETTING);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        long curTs = System.currentTimeMillis();
        if (mPressedTime == 0 || curTs - mPressedTime > 2000) {
            mPressedTime = curTs;
            Toast.makeText(getActivity(), R.string.string_exit,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (curTs - mPressedTime < 2000) {
//            System.exit(0);
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
            return false;
        }
        return false;
    }

    // 异步操作统一入口 避免许多Runnable
    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case IA_CLICK:
            if (isStop){
                return;
            }
            clickByText(mName);
            commitAction(IA_CLICK,0,null,mTime);
            break;
            default:
                break;
        }
    }

    private void clickByText(String name) {
        boolean result = AccessibilityOperator.getInstance().clickById("me.ele.crowdsource:id/button2");
        AccessibilityLog.printLog(result ? "ById "+name+" 成功" : "ById "+name+" 失败");
        if (!result){
            result = AccessibilityOperator.getInstance().clickByText(name);
        }
        AccessibilityLog.printLog(result ? "ByText "+name+" 成功" :  "ByText "+name+" 失败");
    }

}
