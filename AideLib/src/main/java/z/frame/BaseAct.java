package z.frame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.k12lib.afast.utils.ToastUtils;

import java.util.ArrayList;

// activity 基础类型
// 为防止异步操作出错 如果界面处于后台 将操作延后执行
// 优化:因为pop不能忽略错误 如果后台pop,延后之后的所有操作 如果没有则执行忽略模式
// onNewIntent->onRestart->onStart->onResume
// 通过intent唤醒时,onNewIntent也采用同样策略将操作延后
public class BaseAct extends FragmentActivity implements ICommon, View.OnClickListener, Handler.Callback, LocalCenter.IReceiver {
	// 记录日志
	private String logTag = null;
    private boolean mNoFrags = true;

	public void _log(String txt) {
		if (logTag==null) {
			logTag = Util.buildTag(this);
		}
		Util._log(logTag, txt);
	}
	public ArrayList<TransInfo> mFrags = new ArrayList<TransInfo>(10);
	protected boolean popRaw(boolean bFin) {
		FragmentManager fm = getSupportFragmentManager();
		boolean ret = false;
		if (fm.getBackStackEntryCount()>0) {
			fm.popBackStack();
			ret = true;
		} else if (bFin) {
			// 默认没有回退栈就退出activity
			finish();
		} else {
            mNoFrags = true;
        }
		_log("pop:" + fm.getBackStackEntryCount());
		return ret;
	}
	public boolean pop(boolean bFin) {
		if (mIsResume) {
            onStateNotSaved(); // 重置FragmentManager的save标志 免得异常
			return popRaw(bFin);
		}
		if (bFin&&mFrags.size()==0&&getSupportFragmentManager().getBackStackEntryCount()==0) {
			_log("pop finish background");
			finish();
			return false;
		} else { // 延后执行
			_log("pop delay:" + bFin);
			mFrags.add(new TransInfo(null,bFin?2:0));
			return true;
		}
	}
	protected void popAllRaw() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount()>0) {
			fm.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		_log("popAll:" + fm.getBackStackEntryCount());
	}
	public void popAll() {
		if (mIsResume) {
            onStateNotSaved(); // 重置FragmentManager的save标志 免得异常
			popAllRaw();
			return;
		}
		if (mFrags.size()==0&&getSupportFragmentManager().getBackStackEntryCount()==0) {
			_log("popAll nothing");
			return;
		}
		// 延后执行
		_log("popAll delay");
		mFrags.add(new TransInfo(null,1));
	}
	protected void pushFragmentRaw(Fragment fg,int flag) {
		_log("pushFragment:"+fg+",flag="+flag);
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
//		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		if ((flag&PF_Add)==PF_Add) {
			tr.add(mCntId, fg, fg.getClass().getName());
		} else {
			tr.replace(mCntId, fg, fg.getClass().getName());
		}
		if ((flag&PF_Back)==PF_Back&&!mNoFrags) {
			tr.addToBackStack(fg.getClass().getName());
		}
        mNoFrags = false;
		tr.commitAllowingStateLoss();
//			tr.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
//			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//				tr.commit();
	}
	public void pushFragment(Fragment fg,int flag) {
		if (mFrags.size()==0) { // 和resume没有关系 因为有忽略模式
            onStateNotSaved(); // 重置FragmentManager的save标志 免得异常
			pushFragmentRaw(fg, flag);
		} else {
			_log("pushFragment delay:"+fg+",flag="+flag);
			mFrags.add(new TransInfo(fg,flag));
		}
	}
	public static final int REQ_startActivityForResult = 900;
	private int mOrgReq = -1;
	private BaseFragment mOrgFragment = null;
	public int onActMsg(int id,Object sender,int arg,Object extra) {
		_log("onActMsg:id="+id+",sender="+sender+",arg="+arg+",extra="+extra);
		switch (id) {
			case REQ_StartActivity: // startActivityForResult
				mOrgFragment = (BaseFragment) sender;
				mOrgReq = arg;
				startActivityForResult((Intent) extra,REQ_startActivityForResult);
				break;
		}
		return 1;
	}
	public int mCntId = 0;
	public DlgMgr mDlgs = new DlgMgr();
	public void showLoading(boolean bCancel) {
		mDlgs.showLoading(bCancel);
	}
	public void showLoading(boolean bCancel, int style) {
		mDlgs.showLoading(bCancel, style);
	}
	public void hideLoading() {
		mDlgs.hideLoading();
	}
	public void hideDlg(int id) {
		mDlgs.hideDlg(id);
	}
	public void showDlg(int id,View root,boolean bCancel,int style) {
		mDlgs.showDlg(id, root, bCancel, style);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (app.am!=null) app.am.onCreate(this);
		_log("onCreate");
		mDlgs.enable(this);
		super.onCreate(savedInstanceState);
		mIsResume = true;
	}
	@Override
	protected void onDestroy() {
		_log("onDestroy");
		mDlgs.hideAllDialog();
		mDlgs.enable(null);
		if (mHdler!=null) {
			mHdler.removeCallbacksAndMessages(null);
			mHdler = null;
		}
//		if (mUmShare!=null) {
//			mUmShare.destroy();
//			mUmShare = null;
//		}
		if (mRecv!=null) {
			mRecv.destroy();
			mRecv = null;
		}
		super.onDestroy();
		if (app.am!=null) app.am.onDestroy(this);
	}
	// 处理/转发返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
			if (mCntId!=0) {
				Fragment fg = getSupportFragmentManager().findFragmentById(mCntId);
				if (fg!=null&&fg instanceof BaseFragment&&((BaseFragment)fg).onBackPressed()) {
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onBackPressed() {
		try { // 防止系统异常
			super.onBackPressed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 处理/转发onclick事件
	@Override
	public void onClick(View view) {
		if (mCntId!=0) {
			Fragment fg = getSupportFragmentManager().findFragmentById(mCntId);
			if (fg!=null&&(fg instanceof View.OnClickListener)) {
				((View.OnClickListener)fg).onClick(view);
				return;
			}
		}
	}
	public boolean mIsResume = false;
	@Override
	protected void onResume() {
		if (app.am!=null) app.am.onResume(this);
        super.onResume();
	}
	protected void resumeFragmentActions() {
		if (mFrags.size()>0) {
            onStateNotSaved(); // 重置FragmentManager的save标志 免得异常
			_log("onResume do delay trans info:"+mFrags.size());
			// 依次执行延后事务
			for (TransInfo ti:mFrags) {
				if (ti.fg==null) {
					if (ti.flag==0||ti.flag==2) {
						if(!popRaw(ti.flag == 2)&&(ti.flag==2)) {
							// 已经finish
							break;
						}
					} else if (ti.flag==1) {
						popAllRaw();
					}
				} else {
					pushFragmentRaw(ti.fg, ti.flag);
				}
			}
			mFrags.clear();
		}
	}
	@Override
	protected void onPostResume() {
		super.onPostResume();
		mIsResume = true;
		resumeFragmentActions();
		app.fixScreen(this);
	}
	@Override
	protected void onPause() {
		try {
			super.onPause();
		} catch (Throwable e) {
			e.printStackTrace();
			// 针对友盟出现的onpause出现的崩溃 统一一下界面
			Fragment fg = getSupportFragmentManager().findFragmentById(mCntId);
			if (fg!=null) {
				String name = null;
				if (fg instanceof BaseFragment) {
					name = ((BaseFragment)fg).mName;
				}
				if (TextUtils.isEmpty(name)) {
					name = fg.getTag();
				}
				if (TextUtils.isEmpty(name)) {
					name = fg.getClass().getName();
				}
			}
			if (!isFinishing()) {
				finish();
			}
		}
		mIsResume = false;
		if (app.am!=null) app.am.onPause(this);
	}
	public Handler mHdler;
	// 提交异步任务
	public int commitAction(int id,int arg,Object extra,int ms) {
		if (mHdler==null) {
			mHdler = new Handler(Looper.getMainLooper(),this);
		}
		mHdler.sendMessageDelayed(Message.obtain(mHdler, id, arg, 0, extra),ms);
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
	public View getRootView() {
		Window window = getWindow();
		return window!=null?window.getDecorView():null;
	}
	/* 异步操作统一入口 避免许多Runnable */
	public void handleAction(int id, int arg, Object extra) {
	}
	@Override
	public boolean handleMessage(Message msg) {
		handleAction(msg.what,msg.arg1,msg.obj);
		return true;
	}

//	private UmBuilder mUmShare = null;
//	public UmBuilder shareBuilder(BaseFragment bf,int type) {
//		if (mUmShare==null) {
//			mUmShare = new UmBuilder(type).fragment(bf);
//		} else {
//			mUmShare.clear().type(type).fragment(bf);
//		}
//		return mUmShare;
//	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQ_startActivityForResult: {
				if (mOrgFragment==null) break;
				BaseFragment frag = this.mOrgFragment;
				this.mOrgFragment = null;
				frag.onActivityResult(mOrgReq,resultCode,data);
				break; }
		}
//		if (mUmShare!=null) mUmShare.onActivityResult(requestCode,resultCode,data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 显示提示
	public void showShortToast(Object txt) {
		if (txt==null) return;
		ToastUtils.showShortToast(txt);
	}
	public static class TransInfo {
		public Fragment fg = null; // null:pop not-null:push
		public int flag = 0; // push:PF_Add,PF_Back pop:0=pop one,1=all,2=fin
		public TransInfo(Fragment f,int fl) {
			fg = f;
			flag = fl;
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
}
