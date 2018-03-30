package z.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import z.down.AsynFile;
import z.down.DMgr;
import z.down.IDown;
import com.k12lib.afast.log.Logger;
import z.util.MD5;
import com.k12lib.afast.utils.ToastUtils;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import z.ext.base.ZGlobalMgr;
import z.ext.frame.NotifyCenter;
import z.ext.frame.ZKeys;
import z.frame.NetLis;

// 封装播放短音频 唯一实例
// 处理下载+播放 并上报状态
public class UrlPlayer extends BaseCntLis implements Observer, IDown.IPostHandler,IMedia, Handler.Callback {
    private Handler mHdler = new Handler(Looper.getMainLooper(),this);
    private String mAudioId = null;
    private LocalMediaPlayer mPlayer;
    private AsynFile mLastDown = null;
    private FocusLis mFocusLis;
    private AudioManager mAm;
    private File mDirDown;

    public static UrlPlayer instance() {
        UrlPlayer mgr = ZGlobalMgr.getGlobalObj("UrlPlayer");
        if (mgr==null) {
            Context ctx = ZGlobalMgr.<Context>getGlobalObj(ZKeys.kAppContext);
            mgr = new UrlPlayer(ctx);
            ZGlobalMgr.setGlobalObj("UrlPlayer", mgr);
        }
        return mgr;
    }

    private UrlPlayer(Context ctx) {
        //初始化播放器
        mPlayer = new LocalMediaPlayer();
        mPlayer.setILis(this);
        NotifyCenter.register(NetLis.Key, this);
        mWifi = NetLis.getCurrentState(ctx);
    }

    public void setCacheDir(File dir) {
        mDirDown = dir;
    }

	// type: 0=file 1=aac 2=mp3
    public String playUrl(String url, int type){
        stop(true);
        mAudioId = url;
        File f = null;
        //如果是url用url来创建一个文件名，否则就是文件路径直接创建文件
        if (type!=0){
            url = MD5.md5Lower(url);
            f = new File(mDirDown, url +(type==1?".aac":".mp3"));
        }else {
            f = new File(url);
        }
        // 检查本地文件 下载完毕后可以直接播放
        if (f.exists()) {
            mPlayer.play(f.getAbsolutePath(), mAudioId);
            sendOnlyAction(true, IA_AudioFocus, 1, 100);
            return null;
        }
        if (type==0) {
            //如果是文件并且文件不存在直接报错
            onPlayStateChange(mAudioId, Error);
            mAudioId = null;
            ToastUtils.showShortToast("播放错误，文件不存在");
            return "播放错误，文件不存在";
        }
        if (mWifi==NetLis.None) {
            // 没有网络 直接失败
            onPlayStateChange(mAudioId,Error);
            mAudioId = null;
            ToastUtils.showShortToast("网络连接失败，请检查网络");
            return "网络连接失败，请检查网络";
        }
        // 开始下载
        mLastDown = DMgr.Download(url, mAudioId, f, this);
        // 修改内部状态为初始值
        onPlayStateChange(mAudioId, BufferIn);
        onProgress(mAudioId,0,0,1);
        return null;
    }
    private static final int IA_AudioFocus = 102; // 音频焦点处理 0=放弃 1=申请
    private static final int IA_FinishDown = 103; // 下载完毕

    public int mWifi = NetLis.None;

    public void stop(boolean bNotify) {
        if (mLastDown!=null) {
            //需要释放资源
            releaseLastDown();
            if (bNotify&&mAudioId!=null) {
                //更新界面
                onPlayStateChange(mAudioId,Stop);
            }
        } else {
            mPlayer.stop(bNotify);
        }
        mAudioId = null;
        mInner.mPos = mInner.mBuffer = 0;
    }

    //播放到指定的进度
    public void seekPlay(int pos){
        mPlayer.seek(pos);
    }

    public int getStatus(){
        return mPlayer.getStatus();
    }

    private void releaseLastDown() {
        if (mLastDown!=null) {
            AsynFile af = mLastDown;
            mLastDown = null;
            // 先移除 保证播放进行
            af.cancel(true);
            DMgr.removeDownload(af.mId);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o!=null&&(o instanceof int[])) {
            int[] states = (int[])o;
            if (states.length<2) return;
            // 网络变更了
            int old = states[0];
            int cur = states[1];
            // 网络状态变更
            if (mWifi==cur) return;
            mWifi = cur;
            if (mLastDown==null) return;
            DMgr.removeDownload(mLastDown.mId);
            switch (cur) {
                case NetLis.Wifi:
                case NetLis.Mobile:
                    mLastDown = DMgr.Download(mLastDown.mId,mLastDown.getUrl(),mLastDown.mRealPath,this);
                    break;
                case NetLis.None:
                    ToastUtils.showShortToast("网络连接错误，请检查网络");
                    mLastDown = null;
                    stop(false);
                    onPlayStateChange(mAudioId, Error);
                    break;
            }
        }
    }

    private void sendOnlyAction(boolean bOn,int msg,int arg,int ms){
        mHdler.removeMessages(msg);
        if (bOn) {
            mHdler.sendMessageDelayed(Message.obtain(mHdler, msg, arg, 0), ms);
        }
    }

    public void pause(){
        if (mLastDown != null){
            stop(true);
            return;
        }
        if (mAudioId != null) {
            mPlayer.pause();
        }
    }

    public void resume() {
        if (mAudioId == null) return;
        if (mPlayer.inPlaying()) {
            sendOnlyAction(true,IA_AudioFocus,1,100);
            mPlayer.resume();
        } else {
            // 没有在播放 从头播放
            File f = new File(mDirDown, MD5.md5Lower(mAudioId) +".aac");
            if (f.exists()){
                mPlayer.play(f.getAbsolutePath(), mAudioId);
	            sendOnlyAction(true, IA_AudioFocus, 1, 100);
            }
        }
    }

    @Override
    public boolean onPostDownload(AsynFile task, boolean bOK) {
        mHdler.sendMessage(Message.obtain(mHdler, IA_FinishDown, bOK ? 1 : 0, 0, task));
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case IA_FinishDown: {
                // 任务下载完成 通知我的下载列表
                AsynFile af = (AsynFile)msg.obj;
                DMgr.removeDownload(af.mId);
                if (msg.arg1==1) {
                    // 下载成功
                    IDown.RenameHandler.renameDownloadFile(af);
                    if (af==mLastDown) {
                        mLastDown = null;
                        File f = af.mRealPath;
                        mPlayer.play(f.getAbsolutePath(), mAudioId);
                        sendOnlyAction(true, IA_AudioFocus, 1, 100);
                    }
                } else {
                    // 下载失败
                    if (af==mLastDown) {
                        // 提示并停止播放
                        mLastDown = null;
	                    String id = mAudioId;
                        stop(false);
                        onPlayStateChange(id, Error);
                    }
                }
                break; }
            case IA_AudioFocus: { // 延时处理音频焦点
                if (msg.arg1 == 1) {
                    if (mFocusLis!=null) break;
                    mFocusLis = new FocusLis();
                    if (mAm==null) {
                        Context ctx = ZGlobalMgr.<Context>getGlobalObj(ZKeys.kAppContext);
                        mAm = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                    }
                    mAm.requestAudioFocus(mFocusLis, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                } else {
                    if (mFocusLis==null) break;
                    FocusLis lis = mFocusLis;
	                mFocusLis = null; // 焦点在 对象在; 焦点失 对象空
                    mAm.abandonAudioFocus(lis);
                }
                break; }
        }
        return true;
    }

    private class FocusLis implements AudioManager.OnAudioFocusChangeListener{
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logger.i("UrlPlayer", "onAudioFocusChange >>>> " + focusChange + "," + mFocusLis + "," + this);
            if (mFocusLis!=this) return;
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                    ||focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
	            mFocusLis = null; // 焦点在 对象在; 焦点失 对象空
	            mAm.abandonAudioFocus(this);
                stop(true);
            }
        }
    }
}
