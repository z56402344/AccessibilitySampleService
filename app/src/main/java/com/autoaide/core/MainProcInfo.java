package com.autoaide.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.autoaide.R;
import com.autoaide.common.GlobaleParms;
import com.autoaide.common.IAutoParams;
import com.autoaide.db.AppDBHelper;
import com.k12lib.afast.log.ILogger;
import com.k12lib.afast.log.Logger;
import com.k12lib.afast.log.PrintToLogCatLogger;

import java.io.File;

import z.db.ShareDB;
import z.frame.ActivityManager;
import z.frame.BaseFragment;
import z.frame.ICommon;
import z.frame.NetLis;
import z.image.universal_image_loader.cache.disc.impl.UnlimitedDiscCache;
import z.image.universal_image_loader.cache.memory.impl.WeakMemoryCache;
import z.image.universal_image_loader.core.ImageLoader;
import z.image.universal_image_loader.core.ImageLoaderConfiguration;

// 主进程信息
public class MainProcInfo implements IAutoParams, ICommon {
    private ILogger fileLogger;
    public ActivityManager mActivityManager = null;
    private String mDownloadPath = null; // 下载地址
    private NetLis mNetLis = new NetLis();

    public void init(Context ctx) {
        initCommonLibs(ctx);
        // 关闭Log true==打开log，false==关闭log
        Logger.setDeBug(GlobaleParms.isDebug);
        Logger.addLogger(fileLogger = new PrintToLogCatLogger());
        updateParams(ctx);
//        MobclickAgent.openActivityDurationTrack(false);
        initImageLoader(ctx);
        mNetLis.init(ctx);
    }

    public void uninit() {
        // 移除写入文件的Logger
        Logger.removeLogger(fileLogger);
        mNetLis.destroy();
    }

    public void initImageLoader(Context ctx) {
        getCachImagePath(ctx);
        File cacheDir = new File(mDownloadPath);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1024 * 1024 * 15)
                // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(config);
    }

    public String getCachImagePath(Context ctx) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mDownloadPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + "k12"
                    + File.separator + "cache";
        } else {
            mDownloadPath = Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + "data" + File.separator
                    + ctx.getPackageName() + File.separator + "k12"
                    + File.separator + "cache";
        }
        return mDownloadPath;
    }

    // 更新参数
    public void updateParams(Context ctx) {
        // 应用升级了 需要加入的功能
        ShareDB.Sec sec = new ShareDB.Sec(kSec);
        try {
            PackageManager mgr = ctx.getPackageManager();
            String packageName = ctx.getPackageName();
            PackageInfo info = mgr != null ? mgr.getPackageInfo(packageName, 0) : null;
            int curCode = info != null ? info.versionCode : 0;
            if (sec.getInt(kVerI) != curCode) {
                ApplicationInfo appInfo = null;
                appInfo = mgr.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                packageName = appInfo != null && appInfo.metaData != null ? appInfo.metaData.getString("UMENG_CHANNEL") : null;
                sec.put(kVendor, TextUtils.isEmpty(packageName) ? "unknown" : packageName);
                // 应用升级了 需要加入的功能
                packageName = info != null ? info.versionName : null;
                sec.put(kVerS, TextUtils.isEmpty(packageName) ? "unknown" : packageName);
                sec.put(kVerI, curCode);
                sec.save(false);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCommonLibs(Context ctx) {
        mActivityManager = new ActivityManager() {
            @Override
            public void onResume(BaseFragment frag) {
                super.onResume(frag);
//                SyncMgr.onActive();// 程序激活了
            }
        };

        app.isDebug = GlobaleParms.isDebug;
        app.init(ctx);
        app.app_name = ctx.getString(R.string.app_name);
        app.am = mActivityManager;
        app.db = new AppDBHelper();
//        app.mTTf = Typeface.createFromAsset(ctx.getAssets(), "fonts/youyuan.ttf");
        app.Toast_Layout = R.layout.toast_normal;
//		app.Toast_Animtion = R.style.anim_view;
        app.Toast_ID_Text = R.id.mTvContent;
        app.Loading_Layout = R.layout.dialog_loading;
        app.Loading_Style = R.style.LoadingDialog;
        app.DefaultShareUrl = "";
        app.mWXAppId = "";
        app.mWXAppSecret = "";
        // QQ相关的ID和key
//        app.mQQAppId = "";
//        app.mQQAppKey = "";
        // 微博相关的ID和key
//        app.mWBAppId = "";
//        app.mWBAppKey = "";
        // 分享界面
        app.logo_share = R.mipmap.ic_launcher;
//        app.Share_Layout = R.layout.dialog_share;
//        app.mPlatIds[TCancel] = R.id.mView;
//        app.mPlatIds[TQQ] = R.id.mLlQQ;
//        app.mPlatIds[TWeibo] = R.id.mLlWB;
//        app.mPlatIds[TWeixin] = R.id.mLlWX;
//        app.mPlatIds[TWeixinCircle] = R.id.mLlWxQ;
    }
}
