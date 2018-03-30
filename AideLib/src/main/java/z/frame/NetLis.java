package z.frame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import z.ext.frame.NotifyCenter;

// 网络监听
public class NetLis extends BroadcastReceiver {
	public static final int Wifi = 0x01;
	public static final int Mobile = 0x02;
	public static final int None = 0x00;
	public static final String Key = "network_changed";

	private int mState = -1;
	private Context mCtx;
	// 监听网络变化
	@Override
	public void onReceive(Context ctx, Intent intent) {
		dispatchState(ctx);
	}

	// 初始化
	public void init(Context ctx) {
		if (mCtx!=null) return;
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		ctx.registerReceiver(this,filter);
		mCtx = ctx;
	}
	// 销毁
	public void destroy() {
		if (mCtx==null) return;
		mCtx.unregisterReceiver(this);
		mCtx = null;
	}
	// 获取网络状态
	public static int getCurrentState(Context ctx) {
		// 获取当前网络状态
		ConnectivityManager connManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager == null) return None;
		NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info!=null&&info.isConnected()) {
			// wifi网络
			return Wifi;
		}
		info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info!=null&&info.isConnected()) {
			// mobile网络
			return Mobile;
		}
		return None;
	}
	// 支持外部主动检查并发送通知
	public void dispatchState(Context ctx) {
		// 获取当前网络状态
		ConnectivityManager connManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info!=null&&info.isConnected()) {
			// wifi网络
			notifyLis(Wifi);
			return;
		}
		info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info!=null&&info.isConnected()) {
			// mobile网络
			notifyLis(Mobile);
			return;
		}
		notifyLis(None);
	}
	// 通知网络变更 仅通知一次 如果变化快则忽略上次变化
	private void notifyLis(int next) {
		if (mState==next) return;
		int[] states = new int[2];
		states[0] = mState;
		states[1] = next;
		mState = next;
		NotifyCenter.notifySimple(Key,states);
	}
}
