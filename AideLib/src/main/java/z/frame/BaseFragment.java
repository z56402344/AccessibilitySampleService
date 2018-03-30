package z.frame;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentFixs;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.k12lib.afast.log.Logger;
import com.k12lib.afast.utils.ToastUtils;
import com.k12lib.afast.view.pulltorefresh.PullToRefreshBase;

import java.io.Serializable;

import z.http.ZHttpCenter;

// fragment基础类
public class BaseFragment extends FragmentFixs implements ICommon, Handler.Callback, LocalCenter.IReceiver, View.OnClickListener {

	public String mName;//页面名称（友盟）

	public void setName(String name){
		mName = name;
	}

	// 记录日志
	private String logTag = null;
	public void _log(String txt) {
		if (logTag==null) {
			logTag = Util.buildTag(this);
		}
		Logger.w(logTag, txt);
	}
	public View mRoot;
	public Handler mHdler;
	public View findViewById(int id) {
		return mRoot!=null?mRoot.findViewById(id):null;
	}
	public View setOnClick(int id,View.OnClickListener lis) {
		return Util.setOnClick(mRoot, id, lis);
	}
	@Override
	public void onDestroyView() {
		_log("onDestroyView");
		hideAllDialog();
		super.onDestroyView();
		if (mRoot!=null) {
			mFocus.save(mRoot.findFocus());
			Util.setParent(mRoot,null,null);
		}
	}
	@Override
	public void onResume() {
		_log("onResume:"+isResumed());
		if (app.am!=null) app.am.onResume(this);
		super.onResume();

		// 恢复焦点
		mFocus.restore();
	}
	@Override
	public void onPause() {
		_log("onPause:"+isRemoving());
		super.onPause();
		if (app.am!=null) app.am.onPause(this);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		_log("onCreate");
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onDestroy() {
		_log("onDestroy");
		if (mHdler!=null) {
			mHdler.removeCallbacksAndMessages(null);
			mHdler = null;
		}
		if (mRecv!=null) {
			mRecv.destroy();
			mRecv = null;
		}
//		shareExit();
		mRoot = null;
//			TalkHttpClient.getClient().cancelRequests(this, true);
		ZHttpCenter.cancelAll(this);
		super.onDestroy();
	}
	protected void superDestroy() {
		super.onDestroy();
	}
	// 是否消费返回键
	public boolean onBackPressed() {
		return false;
	}
	// 发送消息
	public int notifyActivity(int id,int arg,Object extra) {
		_log("notifyActivity:id=" + id + ",arg=" + arg + ",extra=" + extra);
		FragmentActivity act = getActivity();
		if (act!=null&&act instanceof BaseAct) {
			return ((BaseAct)act).onActMsg(id,this,arg,extra);
		}
		return -1;
	}
	// 显示提示
	public void showShortToast(Object txt) {
		if (txt==null||getActivity()==null) return;
		ToastUtils.showShortToast(txt);
	}
	// 显示进度条
	public void showLoading(boolean bCancel) {
		_log("showLoading:" + bCancel);
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			((BaseAct)act).mDlgs.showLoading(bCancel);
		}
	}
	public void hideLoading() {
		_log("hideLoading");
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			((BaseAct)act).mDlgs.hideLoading();
		}
	}
	public void hideDlg(int id) {
		_log("hideDlg:id=" + id);
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			((BaseAct)act).mDlgs.hideDlg(id);
		}
	}
	public Dialog showDlg(int id,View root,boolean bCancel,int style) {
		_log("showDlg:id=" + id + ",cancel=" + bCancel);
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			return ((BaseAct)act).mDlgs.showDlg(id, root, bCancel, style);
		}
		return null;
	}
	public void hideAllDialog() {
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			((BaseAct)act).mDlgs.hideAllDialog();
		}
	}
	// 弹出1个 (返回)
	public boolean pop(boolean bFin) {
		ZHttpCenter.cancelAll(this);
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			return ((BaseAct)act).pop(bFin);
		} else {
			FragmentManager fm = getFragmentManager();
            noteStateNotSaved(fm);
			boolean ret = false;
			if (fm!=null&&fm.getBackStackEntryCount()>0) {
				fm.popBackStack();
				ret = true;
			} else if (bFin&&act!=null) {
				act.finish();
			}
			if (fm!=null) {
				_log("pop:" + fm.getBackStackEntryCount());
			}
			return ret;
		}
	}
	// 弹出所有
	public void popAll() {
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			((BaseAct)act).popAll();
		} else {
			FragmentManager fm = getFragmentManager();
			if (fm!=null) {
				fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				_log("popAll:" + fm.getBackStackEntryCount());
			}
		}
	}
	// 加入一个
	public void pushFragment(Fragment dst,int flag) {
		pushFragment(dst, flag, null, null, null, null);
	}
	// 带参数
	public void pushFragment(Fragment dst,int flag,String k0,String v0) {
		pushFragment(dst,flag,k0,v0,null,null);
	}
	//带int参数
	public void pushFragment(Fragment dst,int flag,String k0,int v0) {
		Bundle args = new Bundle();
		args.putInt(k0,v0);
		dst.setArguments(args);
		pushFragment(dst, flag);
	}
	public void pushFragment(Fragment dst,int flag,String k0,String v0,String k1,Object v1) {
		FragmentActivity act = getActivity();
		if (act instanceof BaseAct) {
			if (k0!=null) {
				Bundle args = new Bundle();
				args.putString(k0,v0);
				if (k1!=null) {
					if (v1 instanceof String)
					args.putString(k1, (String)v1);
					else if (v1 instanceof Integer)
						args.putInt(k1, (Integer)v1);
				}
				dst.setArguments(args);
			}
			((BaseAct)act).pushFragment(dst, flag);
		} else {
			// 不做操作
		}
	}
	public void initHandler() {
		if (mHdler==null) {
			mHdler = new Handler(Looper.getMainLooper(),this);
		}
	}
	// 提交异步任务
	public int commitAction(int id,int arg,Object extra,int ms) {
		if (mHdler==null) {
			initHandler();
		}
		mHdler.sendMessageDelayed(Message.obtain(mHdler,id,arg,0,extra),ms);
		return id;
	}
	// 根据id移除
	public void removeActionById(int id) {
		if (mHdler!=null) {
			mHdler.removeMessages(id);
		}
	}
	// 根据扩展参数移除
	public void removeActionByExtra(Object extra) {
		if (mHdler!=null) {
			mHdler.removeCallbacksAndMessages(extra);
		}
	}
	public void removeActionByIdExtra(int id,Object extra) {
		if (mHdler!=null) {
			mHdler.removeMessages(id, extra);
		}
	}
	/* 异步操作统一入口 避免许多Runnable */
	public void handleAction(int id, int arg, Object extra) {
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case IA_RfCom:
			if (msg.obj instanceof PullToRefreshBase){
				setRefresh(false,((PullToRefreshBase<ListView>)msg.obj));
			}
			break;
		case IA_Run:
			if (msg.obj instanceof Runnable) {
				((Runnable)msg.obj).run();
			}
			break;
		default:
			handleAction(msg.what,msg.arg1,msg.obj);
			break;
		}
		return true;
	}
	// 打开activity并捕获异常
	public boolean startActivityEx(Intent it) {
		try {
			startActivity(it);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
	public boolean startActivityEx(Intent it,int reqId) {
		try {
			if (getParentFragment()!=null) {
				// 跨fragment了
				notifyActivity(REQ_StartActivity, reqId, it);
			} else {
				startActivityForResult(it, reqId);
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public static class Builder {
		private Intent mIt;
		private Context mCtx;
		private BaseFragment mFrom;
		private BaseFragment mTo;
		private Bundle mArgs;
		public Builder(Context ctx, Class<?> clazz, int fid) {
			mIt = new Intent(ctx,clazz);
			mIt.putExtra(kFID,fid);
			mArgs = mIt.getExtras();
			mCtx = ctx;
		}

		public Builder(BaseFragment from, BaseFragment to) {
			mFrom = from;
			mTo = to;
			mArgs = to.getArguments();
			if (mArgs == null){
				mArgs = new Bundle();
				to.setArguments(mArgs);
			}
		}

		public Builder fid(int id) {
			return with(kFID,id);
		}

		public Builder with(String key, int value) {
			mArgs.putInt(key, value);
			return this;
		}

		public Builder with(String key, String value) {
			mArgs.putString(key, value);
			return this;
		}

		public Builder with(String key, double value) {
			mArgs.putDouble(key, value);
			return this;
		}

		public Builder with(String key, long value) {
			mArgs.putLong(key, value);
			return this;
		}

		public Builder with(String key, Serializable value) {
			mArgs.putSerializable(key, value);
			return this;
		}

		public Builder with(String key, Parcelable value) {
			mArgs.putParcelable(key, value);
			return this;
		}

		public void show(int flag){
			if (mIt != null){
				mIt.putExtras(mArgs);
				mCtx.startActivity(mIt);
			}else {
				mFrom.pushFragment(mTo,flag);
			}
		}
		public void add(){
			show(PF_Add|PF_Back);
		}

		public void show(){
			show(PF_Back);
		}
		public void root() {
			mFrom.popAll();
			mFrom.pushFragment(mTo, 0);
		}
	}
	// app内部消息接收器
	public LocalCenter.Receiver mRecv;
	public LocalCenter.Receiver registerLocal(int cmd) {
		if (mRecv==null) {
			mRecv = new LocalCenter.Receiver(this);
		}
		return mRecv.register(cmd);
	}

	public LocalCenter.Receiver unregisterLocal(int cmd) {
		if (mRecv!=null) {
			mRecv.unregister(cmd);
		}
		return mRecv;
	}
	@Override
	public void onReceive(LocalCenter.Receiver ci,LocalCenter.Msg msg) {
		handleAction(msg.cmd,msg.arg,msg.size()>0?msg:msg.extra);
	}
	// 发送广播消息
	public static void broadcast(int cmd,int arg,Object extra) {
		LocalCenter.send(cmd, arg, extra);
	}
	// 友盟分享对象
//	protected UmBuilder mUmShare = null;
//	public UmBuilder shareEnter(int type) {
//		BaseAct act = (BaseAct)getActivity();
//		if (act==null) return null;
//		return mUmShare = act.shareBuilder(this, type);
//	}
//	public void shareExit() {
//		if (mUmShare==null) return;
//		UmBuilder b = mUmShare;
//		mUmShare = null;
//		b.destroy();
//		return;
//	}
	// 支持异步Runnable
	public void post(Runnable r,int delayMs) {
		commitAction(IA_Run,0,r,delayMs);
	}
	// 移除异步Runnable
	public void removeRunnable(Runnable r) {
		if (mHdler!=null) {
			mHdler.removeMessages(IA_Run,r);
		}
	}
	public void setLastFocus(View focus) {
		mFocus.setFocus(focus);
	}
	public void showImm(boolean bOn,View focus) {
//		mFocus.show(bOn,focus);
		if (bOn) {
			if (focus!=null&&isResumed()) {
				ImmFocus.show(true, focus);
			} else {
				setLastFocus(focus);
			}
		} else {
			if (mRoot!=null) {
				ImmFocus.show(false, mRoot);
			}
		}
	}
	protected ImmFocus mFocus = new ImmFocus();

	public static final int TYPE_DOWN = 0;
	public static final int TYPE_UP = 1;
	public static final int TYPE_SCROLL = 2;
	public static final int TYPE_SCROLL_STATE_CHANGE = 3;
	private OnScroll mOnScroll = null;
	public class OnScroll implements AbsListView.OnScrollListener,PullToRefreshBase.OnRefreshListener2<ListView>{

		public boolean isRefresh = false;

		@Override
		public void onScrollStateChanged(AbsListView absListView, int i) {
			if (isRefresh || absListView.getAdapter() == null)return;
			BaseFragment.this.onScrollStateChanged(absListView,i);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (isRefresh || totalItemCount == 0 || visibleItemCount ==0 || view.getAdapter() == null || visibleItemCount == totalItemCount)return;
			if (mRoot==null||getActivity()==null) return; // 防止系统异步事件在界面消失后依旧调用
			BaseFragment.this.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			if (totalItemCount-firstVisibleItem-visibleItemCount < 3) {
				onPullToRefresh(TYPE_SCROLL);
			}
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> rf) {
			if (getRefresh()){
				commitAction(IA_RfCom, 0, rf, 0);
				return;
			}
			onPullToRefresh(TYPE_DOWN);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> rf) {
			if (getRefresh()){
				commitAction(IA_RfCom,0,rf,0);
				return;
			}
			onPullToRefresh(TYPE_UP);
		}

	}

	public void bindListView(ListView lv){
		if (mOnScroll == null){
			mOnScroll = new OnScroll();
		}
		lv.setOnScrollListener(mOnScroll);
	}

	public void bindListView(PullToRefreshBase<ListView> rv){
		bindListView(rv.getRefreshableView());
		rv.setOnRefreshListener(mOnScroll);
	}

	public void setRefresh(boolean isRefresh,PullToRefreshBase<ListView> rf){
		if (!isRefresh && rf != null)rf.onRefreshComplete();
		if (mOnScroll != null){
			mOnScroll.isRefresh = isRefresh;
		}
	}

	public boolean getRefresh(){
		return mOnScroll != null && mOnScroll.isRefresh;
	}

	/** TYPE_DOWN = 下拉操作, TYPE_UP = 上提操作*/
	public void onPullToRefresh(int type){

	}

	public void onScrollStateChanged(AbsListView absListView, int i) {

	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onClick(View view) {
	}

	public void clickRefresh() {

	}
}
