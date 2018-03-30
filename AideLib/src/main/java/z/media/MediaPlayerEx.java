package z.media;

import java.util.Observable;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

//import android.media.MediaPlayer.OnTimedTextListener;
//import android.media.TimedText;

public class MediaPlayerEx extends Observable implements IMedia {
    public int getAudioSessionId() {
        return mPlayer != null ? mPlayer.getAudioSessionId() : 0;
    }

	private Context mCtx = null; // 上下文
	private MediaPlayer mPlayer = null; // 播放器
	private int mState = Null; // 当前状态
	private SurfaceHolder mVideo = null; // 视频

	// 外部状态
	private boolean misUrl = false; // 外部类型
	private String mOutUrl = null; // 外部url
	private String mOutID = null; // 名字 显示用
	private int mOutState = 0; // 外部状态 0=停止 其他=播放
	private int mStreamType = AudioManager.STREAM_MUSIC;

	// 防止无用的上报
	private int mBuffer = 0;
	private int mPos = 0;
	private int mLastErr = 0;
	private String mLastErrExt = null;

	private int mSeq = 0; // 总共播放次数

	// 内部状态
	private String mInUrl = null; // 当前播放的url
	private String mInID = null; // 名字 显示用
	private int mInState = 0; // 内部状态
	private boolean mSeeking = false;
	// 创建播放器
	public MediaPlayerEx(Context ctx) {
		mCtx = ctx;
	}
	// 设置播放类型
	public void setStreamType(int stype) { mStreamType = stype; }
	// 设置视频窗口
	public void setVideoWin(SurfaceHolder sHolder) { mVideo = sHolder; }
	// 播放
	public void play(String file,String nameid,boolean isUrl) {
		mOutUrl = file;
		mOutID = nameid;
		misUrl = isUrl;
		mOutState = (mOutState<CtrlStop)?CtrlPlay:(++mSeq);
		fireTimer();
	}
	// 暂停
	public void pause() {
		if (mOutState==CtrlPause) return;
		mOutState = CtrlPause;
		fireTimer();
	}
	// 继续
	public void resume() {
		if (mOutState==CtrlResume) return;
		mOutState = CtrlResume;
		fireTimer();
	}
	// 停止
	public void stop() {
		if (mOutState==CtrlStop) return;
		mOutState = CtrlStop;
		fireTimer();
	}
	// 释放资源
	public void reset() {
		if (mOutState==CtrlReset) return;
		mOutState = CtrlReset;
		fireTimer();
	}
	// 销毁
	public void destroy() {
		if (mPlayer!=null) { // 需要考虑准备中状态么
			mPlayer.reset();
			mPlayer.setDisplay(null);
			mPlayer.setOnPreparedListener(null);
			mPlayer.setOnBufferingUpdateListener(null);
			mPlayer.setOnCompletionListener(null);
			mPlayer.setOnErrorListener(null);
			mPlayer.setOnSeekCompleteListener(null);
			mPlayer.setOnInfoListener(null);
//			mPlayer.setOnTimedTextListener(null);
			mPlayer.setOnVideoSizeChangedListener(null);
			mPlayer.release();
			mPlayer = null;
		}
		deleteObservers();
		mHdler.removeCallbacks(null);
		mInState = mOutState = Null;
		mInUrl = mOutUrl = null;
		mInID = mOutID = null;
		mVideo = null;
	}
	// 定位
	public boolean seek(int ms) {
		if (mPlayer==null||mSeeking) return false; // 上一个过程中
		switch (mState) {
		case Prepared:
		case Playing:
		case Pause:
		case Complete:
			break;
		default:
			return false;
		}
		try {
			mPlayer.seekTo(ms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	// 获取总时长
	public int getDuration() {
		if (mPlayer==null) return -1;

		switch (mState) {
		case Idle:
		case Prepareing:
			return -1;
		default:
			break;
		}
		return mPlayer.getDuration();
	}
	// 获取当前进度
	public int getCurrentPosition() {
		if (mPlayer==null) return -1;

		switch (mState) {
		case Idle:
		case Prepareing:
			return -1;
		default:
			break;
		}
		return mPlayer.getCurrentPosition();
	}

	// 发送定时器
	private void fireTimer() {
		mHdler.removeCallbacks(mDoEvt);
		mHdler.postDelayed(mDoEvt,100);
	}
	private void notifyStateChange(int newState) {
		mState = newState;
		Evt et = new Evt();
		et.mEvt = Evt_Change;
		et.mArg = mState;
		postNotify(et);
	}
	private Handler mHdler = new Handler(Looper.getMainLooper());
	private Runnable mDoEvt = new Runnable() {
		@Override
		public void run() {
			onProcState();
		}
	};
	private Runnable mDoProgress = new Runnable() {
		@Override
		public void run() {
			updateProgress();
		}
	};
	// 更新进度
	private void updateProgress() {
		// 停止了
		if (mPlayer==null||mOutState==0||mInState!=mOutState) return;

		switch (mState) {
//		case Prepared:
		case Playing: {
			int position = mPlayer.getCurrentPosition();
			if (position/20==mPos) break; // 精确到秒 20ms
			// 记住上次的秒数
			mPos = position/20;
			Evt et = new Evt();
			et.mEvt = Evt_Progress;
			et.mArg = position;
			postNotify(et);
			} break;
		}
		mHdler.removeCallbacks(mDoProgress);
		mHdler.postDelayed(mDoProgress,20);
	}
	private void onProcState() {
		_log("onProcState","st="+mState+",in="+mInState+",out="+mOutState+",inid="+mInID+",outid="+mOutID+",inurl="+mInUrl+",outurl="+mOutUrl);
		if (mOutState!=mInState) {
			// 状态变更了
			if (mState== Prepareing) {
				// 准备阶段 不做操作 推迟
				return;
			}
			mInState = mOutState;
			switch (mInState) {
			case CtrlStop: // 停止
				_stop();
				return;
			case CtrlPause: // 暂停
				_pause();
				return;
			case CtrlResume: // 继续
				_resume();
				return;
			case CtrlReset: // 释放资源
				_reset();
				return;
			}
			if (mInState>0) {
				// 播放
				_play();
			}
			return;
		}
		// 状态没有变化 是内部触发的通知
		if (mState== Prepared) {
			// 准备好 正常播放了
			if (mPlayer!=null) {
				Evt et = new Evt();
				et.mEvt = Evt_Prepared;
				et.mArg = mPlayer.getDuration();
				postNotify(et);
//				simpleNotify(Evt_Prepared); // 在这里报上去 避免切下一个url了还报旧的
				_startraw();
			} else {
				// 状态不一致错误
			}
		} else if (mState== Error) {
			// 错误事件 在这里上报
			Evt et = new Evt();
			et.mEvt = Evt_Error;
			et.mArg = mLastErr;
			et.mExtra = mLastErrExt;
			postNotify(et);
			notifyStateChange(Error); // 出错了
			if (mPlayer!=null) {
				mPlayer.reset();
			}
			mState = Idle;
		} else if (mState== Complete) {
			simpleNotify(Evt_Complete); // 播完了上报
		}
	}
	private class Listeners implements OnPreparedListener, OnBufferingUpdateListener, OnCompletionListener,
		OnErrorListener, OnSeekCompleteListener, OnInfoListener
//		, OnTimedTextListener 
, OnVideoSizeChangedListener
		{
		@Override
		public void onPrepared(MediaPlayer arg0) {
			_log("onPrepared","id="+mInID+",url="+mInUrl);
			// 准备完成 不直接上报 避免上报旧url事件
			mState = Prepared;
			// 触发推迟的事件逻辑
			fireTimer();
		}
//		@Override
//		public void onTimedText(MediaPlayer arg0, TimedText txt) {
//			// TODO Auto-generated method stub
//			_log("onTimedText","txt="+txt.getText());
//		}
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			_log("onInfo","what="+what+",extra="+extra);
			return false;
		}
		@Override
		public void onSeekComplete(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			_log("onSeekComplete","id="+mInID+",url="+mInUrl);
		}
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			_log("onError","what="+what+",extra="+extra);
			mLastErr = what;
			mLastErrExt = ""+extra;
			mState = Error; // 出错了
			// 触发推迟的事件逻辑 不直接上报 避免上报旧url事件
			fireTimer();
			return true;
		}
		@Override
		public void onCompletion(MediaPlayer arg0) {
			_log("onCompletion","id="+mInID+",url="+mInUrl);
			mState = Complete; // 完成了
			fireTimer(); // 延迟上报
		}
		@Override
		public void onBufferingUpdate(MediaPlayer arg0, int percent) {
			if (mBuffer!=percent) {
				mBuffer = percent;
				_log("onBufferingUpdate","per="+percent+",id="+mInID+",url="+mInUrl);
				if (mOutState==0||mOutState!=mInState) return; // 不在播放状态 不报 不是同一url不上报
				// 实时报进度
				Evt et = new Evt();
				et.mEvt = Evt_Buffering;
				et.mArg = percent;
				postNotify(et);
			}
		}
		@Override
		public void onVideoSizeChanged(MediaPlayer player, int w, int h) {
			// TODO Auto-generated method stub

		}
	}
	private void simpleNotify(int evt) {
		if (countObservers()>0) {
			Evt et = new Evt();
			et.mEvt = evt;
			et.mSender = mInID;
			setChanged();
			notifyObservers(et);
		}
	}
	private void postNotify(Evt et) {
		if (countObservers()>0) {
			et.mSender = mInID;
			setChanged();
			notifyObservers(et);
		}
	}
	private Listeners mLis = new Listeners();
	private void _log(String t,String s) {
//		android.util.Log.e(t,s);
	}
	private void _error() {
	}
	private void _play() {
		_log("_play","st="+mState);
		if (mPlayer==null) {
			mPlayer = new MediaPlayer();
			mPlayer.setOnPreparedListener(mLis);
			mPlayer.setOnBufferingUpdateListener(mLis);
			mPlayer.setOnCompletionListener(mLis);
			mPlayer.setOnErrorListener(mLis);
			mPlayer.setOnSeekCompleteListener(mLis);
			mPlayer.setOnInfoListener(mLis);
//			mPlayer.setOnTimedTextListener(mLis);
			mPlayer.setOnVideoSizeChangedListener(mLis);
			mPlayer.setScreenOnWhilePlaying(true);
			mState = Idle;
		}
		switch (mState) {
		case Idle: break;
		case Complete:
		case Pause:
		case Playing: mPlayer.stop(); // 先停止
		case Init:
		case Prepared:
		case Error: mPlayer.reset(); notifyStateChange(Idle); break;
		case Prepareing: _error(); return;// 已过滤 不该有
		}
		mInUrl = mOutUrl;
		mInID = mOutID;
		try {
			if (misUrl) {
				Uri uri = Uri.parse(mInUrl);
				mPlayer.setDataSource(mCtx, uri);
				mState = Init;
			} else {
				mPlayer.setDataSource(mInUrl);
				mState = Init;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 失败了
			mPlayer.reset();
			notifyStateChange(Error); // 出错了
			mState = Idle;
		}
		if (mState== Init) {
			try {
				mBuffer = 0;
				mPlayer.setAudioStreamType(mStreamType);
				if (mVideo!=null) {
					mPlayer.setDisplay(mVideo);
				}
				mPlayer.prepareAsync();
				mState = Prepareing;
			} catch (Exception e) {
				e.printStackTrace();
				// 失败了
				notifyStateChange(Error); // 出错了
				mPlayer.reset();
				mState = Idle;
			}
		}
	}
	private void _stop() {
		_log("_stop","st="+mState);
		if (mPlayer==null) return;
		switch (mState) {
		case Idle:
		case Init:
			break;
		case Prepared:
		case Complete:
		case Pause:
		case Playing: mPlayer.stop(); notifyStateChange(Stop); break;// 停止
		case Error: mPlayer.reset(); notifyStateChange(Idle); break;
		case Prepareing: _error(); return;// 已过滤 不该有
		}
	}
	private void _reset() {
		_log("_reset","st="+mState);
		if (mPlayer==null) return;
		switch (mState) {
		case Idle:
			break;
		case Init:
		case Prepared:
		case Complete:
		case Pause:
		case Playing:
		case Error: mPlayer.reset(); notifyStateChange(Idle); break;
		case Prepareing: _error(); return;// 已过滤 不该有
		}
	}
	private void _pause() {
		_log("_pause","st="+mState);
		if (mPlayer==null) return;
		switch (mState) {
		case Idle:
		case Init:
		case Prepared:
		case Complete:
			break;
		case Pause:
		case Playing: mPlayer.pause(); notifyStateChange(Pause); break;// 暂停
		case Error: mPlayer.reset(); notifyStateChange(Idle); break;
		case Prepareing: _error(); return;// 已过滤 不该有
		}
	}
	private void _resume() { // 播放
		_log("_play","st="+mState);
		if (mPlayer==null) return;
		switch (mState) {
		case Idle:
		case Init:
			break;
		case Complete:
		case Prepared:
		case Pause:
		case Playing: _startraw(); break;// 播放
		case Error: mPlayer.reset(); notifyStateChange(Idle); break;
		case Prepareing: _error(); return;// 已过滤 不该有
		}
	}
	private void _startraw() {
		mPlayer.start();
		mPos = -1;
		notifyStateChange(Playing);
		updateProgress();
	}
}
