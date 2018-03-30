package z.down;

import java.io.File;
import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import z.ext.frame.ZWorkThread;
import z.http.IZHttp;
import z.http.ZHttpCenter;
import z.http.ZHttpItem;

// 下载管理类 实现断点续传管理
// 只下载 不负责存储任务
// org.xx --doing--> org.pt.xx+org.pt(存放cur/total数据) --done--> org.xx
public class DMgr implements IZHttp.INStrPro,Handler.Callback,IDown {
	public static boolean log = false;
	public static void _log(String txt) {
		if (log&& !TextUtils.isEmpty(txt)) android.util.Log.e("DMgr",txt);
	}
	// 下载任务列表 限制同时下载个数
	private HashMap<String, AsynFile> mAsyn = new HashMap<String, AsynFile>();
	// ui线程任务
	private Handler mUI = new Handler(Looper.getMainLooper(),this);
	// 工作线程任务
	private Handler mWork = new Handler(ZWorkThread.getLooper(),this);
	private static final int IA_ProcPost = 2016; // work
	private static final int IA_AllFinish = 2017; // ui

	private static DMgr s_mgr = null;
	public synchronized static DMgr getMgr() {
		if (s_mgr == null) {
			s_mgr = new DMgr();
		}
		return s_mgr;
	}
    public static AsynFile Download(String id, String url, File dst, IDown.IPostHandler ip) {
        return Download(id,url,dst,ip,-1);
    }
	public static AsynFile Download(String id, String url, File dst, IDown.IPostHandler ip,int begin) {
		_log("start:url=" + url);
		DMgr mgr = getMgr();
		AsynFile af = mgr.mAsyn.get(id);
		if (af != null) {
			if (!af.checkTimeout(5))
				return af;
			af.cancel(true); // 进度5s没有动 取消重来 防止死掉
		}
		af = new AsynFile(id, url, dst, begin);
		af.setPostHandler(ip);
		af.setCtx(ZHttpCenter.getCtx());
		af.setKey(mgr);
		af.setNotify(mgr);
		mgr.mAsyn.put(id, af); // 新添加
		af.mState = STDoing;
		af.request();
		return af;
	}

	// 查询下载进度 0-100 ,-1表示没这个任务
	public static AsynFile queryTask(String id) {
		DMgr mgr = getMgr();
		return mgr.mAsyn.get(id);
	}
	public static int queryProgress(String id) {
		AsynFile af = queryTask(id);
		if (af == null)
			return -1; // 没有这个任务 已经失败了
		if (af.mState==STFail||af.mState==STDownFail) {
			removeDownload(id);
			return -1;
		}
		return af.getPercent(100);
	}
	// 移除任务 成功了需要移除
	public static void removeDownload(String id) {
		DMgr mgr = getMgr();
		mgr.remove(id);
	}
	// 删除下载项
	public void remove(String id) {
		AsynFile af = mAsyn.remove(id);
		if (af != null) {
			_log("remove:url=" + af.getUrl());
			af.cancel(true); // 取消下载
		}
	}
	// 更新下载进度
	@Override
	public void onProgress(ZHttpItem hi, long cur, long total) {
		AsynFile af = (AsynFile)hi;
		af.updateLastTs();
		// 更新时间戳
//		_log("cur=" + cur + ",total=" + total);
	}
	@Override
	public void onFinish(ZHttpItem hi, int status, String sBody, Exception e) {
		AsynFile af = (AsynFile)hi;
		af.statusCode = status;
		if (status<300&&e==null) { // 成功
			af.mState = STDownOK;
			_log("AsynFile:onSuccess," + af.getUrl());
			af.updateLastTs();
			if (af.mIPost!=null) {
				mWork.sendMessage(Message.obtain(mWork, IA_ProcPost, 1, 0, af));
			} else {
				mUI.sendMessage(Message.obtain(mUI, IA_AllFinish, 1, 0, af));
			}
		} else {
			af.mState = STDownFail;
			// 处理失败的任务
			_log("AsynFile:onFailure," + af.getUrl());
			remove(af.mId);
			if (af.mIPost!=null) {
				mWork.sendMessage(Message.obtain(mWork, IA_ProcPost, 0, 0, af));
			} else {
				mUI.sendMessage(Message.obtain(mUI, IA_AllFinish, 0, 0, af));
			}
		}
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case IA_ProcPost: { // 工作线程中处理下载后的操作
			AsynFile af = (AsynFile)msg.obj;
			IDown.IPostHandler iph = af.mIPost;
			if (iph!=null) {
				af.updateLastTs();
				if (!iph.onPostDownload(af, msg.arg1==1)) {
					msg.arg1 = 0; // 失败了
				}
				af.updateLastTs();
			}
			mUI.sendMessage(Message.obtain(mUI, IA_AllFinish, msg.arg1, 0, af));
			break; }
		case IA_AllFinish: { // ui线程中处理下载完毕
			AsynFile af = (AsynFile)msg.obj;
			af.mState = (msg.arg1==1)?STOK:STFail;
			if (af.mIPost!=null&&(af.mIPost instanceof IUIHandler)) {
				((IUIHandler)af.mIPost).onPostUI(af,(msg.arg1==1));
			}
			af.mIPost = null;
			break; }
		}
		return true;
	}
}
