package com.autoaide.core;

import android.content.Context;

import com.autoaide.common.GlobaleParms;

import orgx.apache.http.client.config.RequestConfig;
import z.ext.frame.ZBaseApp;
import z.http.ZHttpCenter;

public class MainApplication extends ZBaseApp {

    protected static final String TAG = MainApplication.class.getSimpleName();
    public static MainApplication mApplication;
    private MainProcInfo mInfo = null;
//    private Handler handler;
//    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";


    public static Context getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        mApplication = this;
        super.onCreate();
    }

    @Override
    protected void onInit(String packageName,String procName) {
        if (procName==null||packageName.equals(procName)) {
            super.onInit(packageName,procName);
            // 只有主进程初始化这些信息
            mInfo = new MainProcInfo();
            mInfo.init(this);
            RequestConfig cfg = RequestConfig.custom().setConnectionRequestTimeout(5000).setSocketTimeout(25000).setConnectTimeout(5000).build();
            ZHttpCenter.createInstance(cfg, null);
            ZHttpCenter.setDebug(GlobaleParms.isDebug);
        } else if (procName.equals(packageName+":pushservice")) {
            super.onInit(packageName,packageName);
            // :pushservice进程
        } else {
            super.onInit(packageName,procName);
        }
    }

    @Override
    public void onTerminate() {
        if (mInfo!=null) {
            mInfo.uninit();
            mInfo = null;
            ZHttpCenter.destroyInstance();
        }
        super.onTerminate();
    }

}
