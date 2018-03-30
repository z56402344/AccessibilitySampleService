package z.frame;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;

import z.db.BaseDBHelper;

// app通用配置常量
public class AppCommonInfo extends HashMap<String,Object> {
	public boolean isDebug = false;
	public Context ctx = null; // app Ctx
	public String app_name = null;
	public String phoneType = "Android"; // Android
	public String deviceType; // 设备型号
	public String deviceId; // 设备id
	public String packageName; // app报名
	public String curVersion; // 当前版本
	public int curVerCode; // 当前版本数字
	public String vendor; // 渠道
	public String clientId = null; // 个推唯一id
	public DisplayMetrics metrics;// resource显示参数
	public int statusH = 0; // 状态栏高度
	public int screenW; // 屏幕宽高
	public int screenH; // 屏幕宽高
	public int viewH; // View高 比例布局
    public int softKey = 0; // 0=uncheck 1=none 2=has

	public void init(Context ctx) {
		this.ctx = ctx;
		phoneType = "Android";
		deviceType = android.os.Build.MODEL;
		try {
			PackageManager mgr = ctx.getPackageManager();
			String tmp = ctx.getPackageName();
			packageName = tmp;
			PackageInfo info = mgr != null ? mgr.getPackageInfo(tmp, 0) : null;
			curVerCode = info != null ? info.versionCode : 0;
			ApplicationInfo appInfo = null;
			appInfo = mgr.getApplicationInfo(tmp, PackageManager.GET_META_DATA);
			tmp = appInfo != null && appInfo.metaData != null ? appInfo.metaData.getString("UMENG_CHANNEL") : null;
			vendor = TextUtils.isEmpty(tmp) ? "unknown" : tmp;
			// 应用升级了 需要加入的功能
			tmp = info != null ? info.versionName : null;
			curVersion = TextUtils.isEmpty(tmp) ? "0.0.1" : tmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 设备尺寸参数
		metrics = ctx.getResources().getDisplayMetrics();
		screenW = metrics.widthPixels;
		screenH = metrics.heightPixels;
		viewH = screenH;
		// 获取设备ID 按规则获取
		try {
			TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm!=null?tm.getDeviceId():null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设备号获取失败，取mac地址
        if (TextUtils.isEmpty(deviceId)){
            try {
                WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    WifiInfo info = wifi.getConnectionInfo();
                    if (info != null) {
                        deviceId = info.getMacAddress();
                        if (!TextUtils.isEmpty(deviceId)) {
                            deviceId = "mac_"+deviceId;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //获取ANDROID_ID
        if (TextUtils.isEmpty(deviceId)) {
            try {
                ContentResolver cr = ctx.getContentResolver();
                if (cr != null) {
                    deviceId = Settings.System.getString(cr, Settings.Secure.ANDROID_ID);
                    if (!TextUtils.isEmpty(deviceId)) {
                        deviceId = "android_"+deviceId;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		// 避免低版本系统AsyncTask的bug 第一个在主线程创建
		AsyncTask<Void,Void,Void> uiTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				return null;
			}
		};
		uiTask = null;
	}
	// 纠正屏幕尺寸
	public void fixScreen(Activity act) {
		if (statusH>0) return;
		Display defaultDisplay = act.getWindowManager().getDefaultDisplay();
		screenW = defaultDisplay.getWidth();
		screenH = defaultDisplay.getHeight();
		try {
			final View v = act.getWindow().getDecorView();
			if (v==null) return;
			v.post(new Runnable() {
				@Override
				public void run() {
					if (statusH>0) return;
					Rect rc = new Rect();
					v.getWindowVisibleDisplayFrame(rc);
					if (rc.top>0) {
						statusH = rc.top;
						viewH = rc.height();
					}
//					android.util.Log.e("getWindowVisibleDisplayFrame","size="+rc);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public boolean hasSoftKey() {
        while (softKey==0) {
            //api低于17的时候不能获取
            int api = android.os.Build.VERSION.SDK_INT;
            if (api < 17) {
                softKey = 1;
                break;
            }
            WindowManager systemService = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            if (systemService == null) {
                break;
            }
            Display d = systemService.getDefaultDisplay();
            if (d == null) {
                break;
            }
            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getRealMetrics(displayMetrics);
            int realHeight = displayMetrics.heightPixels;
//            int realWidth = displayMetrics.widthPixels;
            d.getMetrics(displayMetrics);
            int displayHeight = displayMetrics.heightPixels;
//            int displayWidth = displayMetrics.widthPixels;
            if (realHeight>displayHeight+px(1)) { // 1dp兼容
                softKey = 2;
            } else {
                softKey = 1;
            }
            break;
        }
        return softKey == 2;
    }

	public int dp(float px) {
		int result = (int)(px/metrics.density+0.5f);
		return result > 0 ? result : 1;
	}

	public int px(float dp) {
		int result = (int)(dp*metrics.density+0.5f);
		return result > 0 ? result : 1;
	}

	public ActivityManager am;
	public int Toast_Layout = 0; // 通用toast布局
	public int Toast_ID_Text = 0; // 通用toast的文本id
	public int Loading_Layout = 0; // 通用Loading布局
	public int Loading_Style = 0; // 通用Loading style
	public int Toast_Animtion = 0;//通用Toast动画
	// 注意：在微信授权的时候，必须传递appSecret
	// wx937d17ebd3b05ace是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
	public String DefaultShareUrl = null;
	public int logo_share = 0;
	public String mWXAppId = null;
	public String mWXAppSecret = null;
	// QQ相关的ID和key
	public String mQQAppId = null;
	public String mQQAppKey = null;
	// 微博相关的ID和key
	public String mWBAppId = null;
	public String mWBAppKey = null;
	public int Share_Layout = 0; // 分享面板layout
	public int mPlatIds[] = new int[5]; // 下标 0=cancel TQQ=1 TWeibo=2 TWeixin=3 TWeixinCircle=4
	/** 评分系统相关 */
	public double mNoiseLevel = 3.0;  //评分时降噪级别，越大过滤杂音越多，反之越少

	public BaseDBHelper db; // 数据库对象
	public Typeface mTTf = null;

    public AppCommonInfo add(String k, Object v) {
        put(k,v);
        return this;
    }
    public String getString(String k) {
        return getObject(k,String.class);
    }
    public <T> T getObject(String k,Class<T> cls) {
        Object v = get(k);
        if (v!=null&&cls.isInstance(v)) {
            return (T) v;
        }
        return null;
    }
    public int getInt(String k,int v) {
        Integer i = getObject(k,Integer.class);
        return i!=null?i:v;
    }
    public long getLong(String k,long v) {
        Long i = getObject(k,Long.class);
        return i!=null?i:v;
    }
    public float getFloat(String k,float v) {
        Float i = getObject(k,Float.class);
        return i!=null?i:v;
    }
    public boolean getBoolean(String k,boolean v) {
        Boolean i = getObject(k,Boolean.class);
        return i!=null?i:v;
    }
}
