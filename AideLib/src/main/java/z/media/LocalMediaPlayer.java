package z.media;

import java.io.File;
import java.io.RandomAccessFile;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;

// 只播放本地音频的播放器
// 从文件名 修改为文件句柄
public class LocalMediaPlayer extends Handler implements IMedia, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener{
	protected void _log(String txt,Throwable e) {
		if (mDebug) {
			android.util.Log.e("LocalMediaPlayer", mState+"["+mId+"]"+txt);
			if (e!=null) {
				e.printStackTrace();
			}
		}
	}
	public boolean mDebug = false;
	public int mProgMs = 500; // 进度查询间隔

	private MediaPlayer mPlayer = null; // 播放器
    // 内部状态
    protected int mState = Null;
    protected int mSeeking = -1;
    protected int mSeekNext = -1;
    // 监听器
    protected ILis mILis;
    // 播放id
    protected String mId;
	// 文件对象
	protected RandomAccessFile mRaf;

    // 生成对象
    public LocalMediaPlayer() {
        super(Looper.getMainLooper());
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnSeekCompleteListener(this);
    }

    // 播放id
    public String getId() {
        return mId;
    }

    // 播放id
    public void setId(String id) {
        mId = id;
    }

    // 监听器
    public void setILis(ILis ILis) {
        mILis = ILis;
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        mPlayer.setDisplay(surfaceHolder);
    }

    public void setVolume(float volume) {
        switch (mState) {
            case Prepared:
            case Playing:
            case Pause:
            case Complete:
            case BufferIn:
                mPlayer.setVolume(volume, volume);
                break;

        }
    }

    // 设置缓冲比例
    public void setBufferRate(int bufferRate) {
        if (bufferRate>mBufferRate) mBufferRate = bufferRate;
    }

    // 缓冲已满
    public boolean isBufferFinished() {
        return mBufferRate >= MaxRate;
    }

    //获取状态
    public int getStatus() {
        return mState;
    }

    // 获取总时长
    public int getDuration() {
        return inPlaying() ? mDur : -1;
    }

    // 获取当前时长
    public int getCurrentPosition() {
        return inPlaying() ? mPlayer.getCurrentPosition() : -1;
    }

    // 获取缓冲时长
    public int getBufferPosition() {
        int dur = getDuration();
        // 2s防止越界
        return (dur < 1 || mBufferRate == MaxRate) ? dur : (int) (((long) dur * mBufferRate / MaxRate) - 2000);
    }

	protected void closeFile() {
		if (mRaf!=null) {
			try {
				mRaf.close();
			} catch (Exception e) {
				_log("closeFile",e);
			}
			mRaf = null;
		}
	}
    // 停止
    // 释放资源
    public void stop(boolean bNotify) {
        if (mState != Idle) {
            mPlayer.reset();
            if (bNotify) {
                notifyStateChange(Idle);
            } else {
                mState = Idle;
            }
        }
	    closeFile();
        mId = null;
        mSeeking = -1;
        mSeekNext = -1;
        mBufferRate = 0;
	    mLimitBuffer = 0;
	    mLimitDur = -1;
	    mDur = -1;
    }

    // 暂停
    public void pause() {
        switch (mState) {
//		case Pause:
        case BufferIn: // 直接修改状态
                notifyStateChange(Pause);
                return;
        case Prepared:
        case Playing:
            try {
                mPlayer.pause();
                notifyStateChange(Pause);
            } catch (Exception e) {
	            _log("pause",e);
            }
            break;
        }
    }

    // 恢复播放
    public void resume() {
        switch (mState) {
        case Complete:
        case Prepared:
        case Pause:
        case BufferIn: {
            int pos = mPlayer.getCurrentPosition();
	        updateToBuffer(-1);
	        int dur = mDur;
            int bufpos = mLimitDur;
            if (bufpos == dur || pos < bufpos-1000) {
                mPlayer.start();
                notifyStateChange(Playing);
            } else {
                notifyStateChange(BufferIn);
            }
            break;
        }
        }
    }

    // 通知状态变更
    protected void notifyStateChange(int state) {
        mState = state;
        postMessageOnly(MSG_State, state, 0, mId, 20);
    }

    // 播放文件
    public boolean play(String file, String id) {
        return playPartFile(file, id, MaxRate);
    }

    // 播放部分文件
    public boolean playPartFile(String file, String id, int bufRate) {
        return playPartFile(file, id, bufRate, -1);
    }

    public boolean playPartFile(String file, String id, int bufRate, int seekpos) {
        // 释放原有资源
        stop(true);
        try {
	        mRaf = new RandomAccessFile(file,"r");
	        mPlayer.setDataSource(mRaf.getFD());
//            mPlayer.setDataSource(file);
            mState = Init;
            mPlayer.prepare();
            mState = Prepared;
	        mDur = mPlayer.getDuration();
	        if (bufRate<MaxRate) {
		        mPlayer.reset();
		        mState = Idle;
		        mPlayer.setDataSource(mRaf.getFD(),0,mRaf.length()*bufRate/MaxRate);
		        mState = Init;
		        mPlayer.prepare();
		        mState = Prepared;
                mLimitBuffer = bufRate;
		        mLimitDur = mPlayer.getDuration();
                fixDur();
	        } else {
		        mLimitDur = mDur;
		        mLimitBuffer = MaxRate;
	        }
	        _log("playPartFile:dur="+mLimitDur+"/"+mDur+",buf="+mLimitBuffer+"/1000000",null);
            mPlayer.start();
            mBufferRate = bufRate;
            mId = id;
            if (seekpos > 0) {
                try {
                    mSeeking = seekpos;
                    mPlayer.seekTo(seekpos);
                } catch (Exception e) {
                    mSeeking = -1;
                    e.printStackTrace();
                }
            }
            notifyStateChange(Playing);
            postEmptyMessageOnly(MSG_Progress, 100);
            return true;
        } catch (Exception e) {
	        _log("playPartFile",e);
            if (mState!=Idle||bufRate==MaxRate) {
                postMessageOnly(MSG_Error, 0, 0, id, 0);
            }
            return false;
        }
    }

    // 切换文件
    public boolean switchFile(File src, File dst, int bufRate) {
        if (!inPlaying()) { // 不再播放中
            src.renameTo(dst);
            return false;
        }
        int state = mState;
	    if (state==Playing) mPlayer.pause();
        int pos = getCurrentPosition();
        if (mState != Idle) {
            mPlayer.reset();
            mState = Idle;
        }
	    closeFile();
        mSeeking = -1;
        mSeekNext = -1;

        _log("switchFile:dur="+mLimitDur+"/"+mDur+",buf="+mLimitBuffer+"/1000000",null);
        src.renameTo(dst);
        try {
	        mRaf = new RandomAccessFile(dst,"r");
            mPlayer.setDataSource(mRaf.getFD());
            mState = Init;
            mPlayer.prepare();
            mState = Prepared;
            mBufferRate = bufRate;
	        mLimitDur = mDur;
            if (pos > 0) {
                try {
                    mSeeking = pos;
                    mPlayer.seekTo(pos);
                } catch (Exception e) {
                    mSeeking = -1;
                    e.printStackTrace();
                }
            }
            switch (state) {
            case Prepared:
            case Pause:
            case Complete:
                break;
            case BufferIn:
                notifyStateChange(Playing);
//            case BufferOut:
            case Playing:
                mPlayer.start();
                break;
            }
            postEmptyMessageOnly(MSG_Progress, 20);
            return true;
        } catch (Exception e) {
	        _log("switchFile",e);
	        postMessageOnly(MSG_Error, 0, 0, mId, 0);
            return false;
        }
    }

    // 定位 需要参照缓冲进度来
    public boolean seek(int progress) {
	    switch (mState) {
	    case Prepared:
	    case Playing:
	    case Pause:
	    case BufferIn:
	    case Complete:
//        case BufferOut:
		    break;
	    default:
		    return false;
	    }
        _log("seek:"+progress+",dur="+mLimitDur+"/"+mDur+",buf="+mLimitBuffer+"/1000000",null);
        if (updateToBuffer(progress)) return true;
	    if (progress<0) progress = 0;
	    if (mLimitDur>=mDur) {
		    if (progress>mDur) progress = mDur;
	    } else {
		    if (progress>mLimitDur-2000) {
			    progress = mLimitDur-2000;
		    }
	    }
        if (mSeeking>-1) { // 预约下次定位
            mSeekNext = progress;
        } else {
            try {
                mSeekNext = -1;
                mSeeking = progress;
                mPlayer.seekTo(progress);
            } catch (Exception e) {
                mSeeking = -1;
	            _log("seek",e);
                return false;
            }
        }
        return true;
    }

    // 监听完成/错误/定位事件
    @Override
    public void onSeekComplete(MediaPlayer mp) {
	    mSeeking = mSeekNext;
	    mSeekNext = -1;
	    if (mSeeking > -1) {
	        try {
	            mp.seekTo(mSeeking);
            } catch (Exception e) {
                mSeeking = -1;
		        _log("onSeekComplete",e);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
	    _log("onError:"+what+","+extra,null);
	    postMessageOnly(MSG_Error, 0, 0, mId, 0);
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
	    if (mBufferRate==MaxRate) {
		    postEmptyMessageOnly(MSG_Progress, 0);
		    notifyStateChange(Complete);
	    } else {
		    // 播放器有bug 出错了也报完成
		    _log("onCompletion:<100%",null);
		    postMessageOnly(MSG_Error, 0, 0, mId, 0);
	    }
    }

    // 发消息到主线程
    protected void postMessage(int what, int arg1, int arg2, Object obj, int ms) {
        Message msg = obtainMessage();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        sendMessageDelayed(msg, ms);
    }

    protected void postMessageOnly(int what, int arg1, int arg2, Object obj, int ms) {
        removeMessages(what);
        postMessage(what, arg1, arg2, obj, ms);
    }

    protected void postEmptyMessageOnly(int id, int ms) {
        postMessageOnly(id, 0, 0, null, ms);
    }

    // 是否处在播放过程中(进度等参数有效) 注:非播放中状态
    public boolean inPlaying() {
        switch (mState) {
        case Prepared:
        case Playing:
        case Pause:
        case BufferIn:
        case Complete:
//        case BufferOut:
            return true;
        }
        return false;
    }

    // 修正音频长度问题 有些mp3会出现下载一部分仍显示最大时长
    private void fixDur() {
        int dur = (int)(((long)mDur)*mLimitBuffer/MaxRate);
        if (mLimitDur>dur) {
            _log("dur fix:"+mLimitDur+"->"+dur+"/"+mDur,null);
            mLimitDur = dur;
        }
    }
	// 需要保存一个缓冲进度限制
	protected int mBufferRate = 0; // 0-1000000
	protected int mDur = -1; // 最大进度
	protected int mLimitDur = -1; // 已给播放器进度
	protected int mLimitBuffer = -1; // 已给播放器缓冲位置
	// 更新到最大缓冲数据 并支持定位(<0默认定位到当前播放点)
	protected boolean updateToBuffer(int pos) {
		if (mLimitDur==mDur||mLimitBuffer==mBufferRate) return false;
		_log("updateToBuffer:"+mLimitBuffer+"->"+mBufferRate+",dur="+mLimitDur+"/"+mDur,null);
		try {
			int oldState = mState;
			if (oldState==Playing) mPlayer.pause();
			if (pos<0) // 定位到当前进度
				pos = mSeeking<0?mPlayer.getCurrentPosition():(mSeekNext<0?mSeeking:mSeekNext); // 定位
			mSeeking = -1;
			mSeekNext = -1;
			if (oldState!=Idle){
                mPlayer.reset();
            }
			mState = Idle;
			mPlayer.setDataSource(mRaf.getFD(),0,mRaf.length()*mBufferRate/MaxRate);
			mLimitBuffer = mBufferRate;
			mState = Init;
			mPlayer.prepare();
			mState = Prepared;
			mLimitDur = mPlayer.getDuration();
            fixDur();
			if (pos>0) {
				if (mLimitDur>=mDur) {
					if (pos>mDur) pos = mDur;
				} else {
					if (pos>mLimitDur-2000) {
						pos = mLimitDur-2000;
					}
				}
				mSeeking = pos;
				mPlayer.seekTo(pos);
			}
			if (oldState==Playing) mPlayer.start();
			mState = oldState;
		} catch (Exception e) {
			_log("updateToBuffer",e);
			postMessageOnly(MSG_Error, 0, 0, mId, 0);
		}
		return true;
	}

    // 上报进度
    protected void updateProgress() {
        if (!inPlaying()) return;
	    postEmptyMessageOnly(MSG_Progress, mProgMs);
	    int dur = mDur; // 总时间
	    int pos = mPlayer.getCurrentPosition(); // 当前播放时间
	    // 数据都给播放器了 直接上报并返回
	    if (mLimitDur==mDur) {
		    if (mILis != null) {
                if (mState==IMedia.Complete) {
                    pos = dur;
                }
			    mILis.onProgress(mId, pos, dur, dur);
		    }
		    return;
	    }
	    // 给播放器的快播完了或者文件下载完毕了 需要把数据都给播放器
	    if (mBufferRate==MaxRate||(mLimitDur-pos<3000&&mBufferRate-mLimitBuffer>1000)) {
		    updateToBuffer(-1);
	    }
	    // 自动暂停/自动开始
	    if (mState == Playing) {
		    // 没有缓冲完毕 && 给播放器的快播完了
		    if (mBufferRate < MaxRate && mLimitDur - pos < 1000) {
			    mPlayer.pause();
			    notifyStateChange(BufferIn);
		    }
	    } else if (mState == BufferIn) {
		    // 又缓冲了一定量数据 恢复播放
		    if (mBufferRate >= MaxRate || mLimitDur - pos > 2000) {
			    mPlayer.start();
			    notifyStateChange(BufferOut);
//			    mState = Playing;
		    }
	    }
	    // 上报进度
	    if (mILis != null) {
		    int bufpos = mBufferRate==MaxRate?dur:(int)((long)(mBufferRate)*dur/MaxRate); // 缓冲时间
		    if (bufpos < pos) {
			    bufpos = pos; // 防止负buffer
		    }
		    mILis.onProgress(mId, pos, bufpos, dur);
	    }
    }

    protected static final int MSG_Base = 10100;
    protected static final int MSG_Progress = MSG_Base + 1;
    protected static final int MSG_Error = MSG_Base + 2;
    protected static final int MSG_State = MSG_Base + 3;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_Progress:
            updateProgress();
            break;
        case MSG_Error:
            stop(false);
            // 上报错误
            postMessageOnly(MSG_State, Error, 0, msg.obj, 20);
            break;
        case MSG_State:
            // 播放状态变更
            if (mILis != null) {
                mILis.onPlayStateChange((String) msg.obj, msg.arg1);
            }
            break;
        }
    }

    // 销毁对象
    public void destroy() {
        if (mPlayer != null) {
            mPlayer.setOnErrorListener(null);
            mPlayer.setOnCompletionListener(null);
            mPlayer.setOnSeekCompleteListener(null);
            stop(false);
            removeCallbacksAndMessages(null);
            mPlayer.release();
            mPlayer = null;
        }
    }

}
