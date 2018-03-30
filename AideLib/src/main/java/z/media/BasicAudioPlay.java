package z.media;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import z.frame.ICommon;

// 播放代理 每个界面有单独的实例
public class BasicAudioPlay extends BaseLis implements ICommon {
    protected UrlPlayer mUp;

    //播放时更新的进度条
    protected ProgressBar mPb;
    //加载音频时候的动画
    protected ImageView mIvLoading = null;
    //播放时的图标
    protected ImageView mIvPlaying = null;
    //播放时补上左边的空缺
    protected View mVYellow = null;

    //播放中的资源
    protected int mPlayingRes = -1;
    //暂停时候的资源
    protected int mPauseRes = -1;
    // 播放完毕后是否重置进度条，默认重置
    protected int mPbFlag;
    //正在播放的音频的索引，-1表示没有
    public int mPlayingIdx = -1;
    //当前播放的url
    protected String mPlayUrl = null;

    public BasicAudioPlay setPlayer(UrlPlayer up){
        if (mUp != null) mUp.remove(this);
        mUp = up;
        if (mUp != null) mUp.put(this,this);
        return this;
    }

    public void destroy() {
        if (mUp != null) {
	        mUp.remove(this);
	        reset();
            mUp = null;
        }
    }

    public void reset(){
        if (mPlayUrl != null){
            resetUI(0);
            if (isThis()) {
                mPlayUrl = null;
                mPlayingIdx = -1;
                mUp.stop(true);
            }
        }
    }

    //判断id是否是当前正在播放的url
    private boolean isThis(){
        return mPlayingIdx!=-1 && mPlayUrl != null && (mPlayUrl == mId || (mId != null && mPlayUrl.equals(mId)));
    }

    public void pause(){
        if (isThis()) {
            mUp.pause();
        }
    }

    public void resume(){
        if (isThis()){
            mUp.resume();
        }
    }

	// type: 0=file 1=aac 2=mp3
    public void play(String url, int type){
        mPlayUrl = url;
        mUp.playUrl(url, type);
    }

    public void seekPlay(String url, int pos){
        mPlayUrl = url;
        if (isThis()) mUp.seekPlay(pos);
    }

    //设置加载中的View
    public BasicAudioPlay loadingView(ImageView iv){
        mIvLoading = iv;
        return this;
    }
    //设置播放中的View
    public BasicAudioPlay playingView(ImageView iv){
        mIvPlaying = iv;
        return this;
    }
    //设置补上进度条中的View
    public BasicAudioPlay yellowView(View v){
        mVYellow = v;
        return this;
    }
    //设置播放中的资源
    public BasicAudioPlay playingIcon(int id){
        mPlayingRes = id;
        return this;
    }
    //设置暂停的资源
    public BasicAudioPlay pauseIcon(int id){
        mPauseRes = id;
        return this;
    }
    //设置播放时候的进度条
    public BasicAudioPlay progressBar(ProgressBar pb){
        mPb = pb;
        return this;
    }

    public void resetUI(int progress){
        //清除loading的动画
        Util.clearRotaAnim(mIvLoading, mIvPlaying);
        //设置播放按钮的图片为三角形
        Util.setAnim(mIvPlaying,mPauseRes);
        //设置进度条为初始化状态
        if (mPb != null){
            if (progress > -1) {
                mPb.setProgress(progress);
            }
            //如果是seekbar就不需要设置为隐藏
            if (!(mPb instanceof SeekBar)) mPb.setVisibility(View.INVISIBLE);
        }
        //隐藏补上进度条的View
        if (mVYellow != null) mVYellow.setVisibility(View.INVISIBLE);
    }

    /**
     *
     * @param url 播放的url
     * @param pos 默认为-1，当前播放的位置，唯一
     * @param cfg 需要设置配置参数
     * @param type: 0=file 1=aac 2=mp3
     * @return
     */
    public int updatePlay(String url, int pos, Cfg cfg, int type){
        int value = 0;
        //是同一个位置 同一个url
        if (mPlayingIdx == pos&&(mPlayUrl==null||url==null||mPlayUrl.equals(url))){
            switch (mState){
                case IMedia.Idle:
                case IMedia.Complete:
                case IMedia.Error:
                case IMedia.Stop:
                    value = 0;
                    //直接播放
                    loadingView(cfg.mIvLoading).playingView(cfg.mIvPlaying).playingIcon(cfg.mPlayingRes).pauseIcon(cfg.mPauseRes).progressBar(cfg.mPb).yellowView(cfg.mVYellow);
                    mPbFlag = cfg.mPbFlag;
                    play(url,type);
                    break;
                case IMedia.BufferIn:
                case IMedia.Pause:
                    //恢复播放
                    resume();
                    value = 2;
                    break;
                case IMedia.Playing:
                    //变成暂停
                    pause();
                    value = 1;
                    break;
            }
        }else {
            //不是同一个位置，需要播放其他的url
            if (mPlayUrl != null) resetUI(0);
            loadingView(cfg.mIvLoading).playingView(cfg.mIvPlaying).playingIcon(cfg.mPlayingRes).pauseIcon(cfg.mPauseRes).progressBar(cfg.mPb).yellowView(cfg.mVYellow);
	        mPlayingIdx = pos;
            mPbFlag = cfg.mPbFlag;
	        play(url, type);
            value = 3;
        }
        return value;
    }

    public int updatePlay(String url, int pos, Cfg cfg){
        return updatePlay(url, pos, cfg, 1);
    }

    @Override
    public boolean onProgress(String id, int cur, int buffer, int total) {
        if (mPb != null && isThis() && total > 0){
            if (total != mDur){
                //如果总进度不一致就重新设置
                mPb.setProgress(0);
                mPos = 0;
                mPb.setMax(total);
            }
            if (cur != mPos){
                mPb.setProgress(cur);
            }
            return super.onProgress(id, cur, buffer, total);
        }
        return true;
    }

    @Override
    public boolean onPlayStateChange(String id, int state) {
        boolean res = super.onPlayStateChange(id, state);
        //如果url和id一样处理所有状态，否则在播放的时候停止当前的url
        if (!isThis()){
            if (state == IMedia.Playing && mPlayUrl != null) {
                resetUI(0);
                mPlayUrl = null;
                mPlayingIdx = -1;
            }
//            resetUI();
            return res;
        }
        switch (mState) {
            case IMedia.Prepareing:
            case IMedia.BufferIn:
                Util.startRotaAnim(mIvLoading,mIvPlaying);
                break;
            case IMedia.Playing:
                //如果加载的动画为空， 在准备播放的时候已经设置好了播放的图片，不需要设置
                Util.clearRotaAnim(mIvLoading, mIvPlaying);
                Util.setAnim(mIvPlaying, mPlayingRes);
                if (mPb != null){
//                    mPb.setProgress(0);
                    mPb.setVisibility(View.VISIBLE);
                }
                if (mVYellow != null) mVYellow.setVisibility(View.VISIBLE);
                break;
            case IMedia.Complete:
            case IMedia.Error:
                mPlayUrl = null;
                mPlayingIdx = -1;
            case IMedia.Idle:
            case IMedia.Stop:
                resetUI(mState == IMedia.Complete && hasFlag(mPbFlag, Cfg.NO_RESET) ? -1 : 0);
                break;
            case IMedia.Pause:
                Util.setAnim(mIvPlaying, mPauseRes);
                break;
        }
        return res;
    }

    public static boolean hasFlag(int flag, int mask) {
        return (flag & mask ) != 0;
    }
    /**player的参数配置类**/
    public static class Cfg{
        // 音频播放结束后，不重置进度条
        public static final int NO_RESET = 0x01;

        public ProgressBar mPb;
        public ImageView mIvLoading = null;
        public ImageView mIvPlaying = null;
        public View mVYellow = null;

        public int mPlayingRes = -1;
        public int mPauseRes = -1;
        public int mPbFlag = 0;


        /**设置需要改变的View的参数**/
        public Cfg views(ImageView loading, ImageView playing) {
            return views(loading,playing,null,null);
        }

        public Cfg views(ImageView loading, ImageView playing, ProgressBar pb) {
            return views(loading,playing,pb,null);
        }

        public Cfg views(ImageView loading, ImageView playing, ProgressBar pb, View v){
            mPb = pb;
            mIvLoading = loading;
            mIvPlaying = playing;
            mVYellow = v;
            return this;
        }

        public Cfg flags(int flag){
            mPbFlag = flag;
            return this;
        }

        /**设置资源参数**/
        public Cfg imageId(int playingId, int pauseId){
            mPauseRes = pauseId;
            mPlayingRes = playingId;
            return this;
        }
    }
}
