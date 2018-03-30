package z.media;

// 实现包装多个
public class BaseLis implements IMedia.ILis,IMedia {
	public String mId;
	public int mState = IMedia.Null;
	public int mDur = -1;
	public int mPos = -1;
	public int mBuffer = -1;
	// 监听播放状态 播放/暂停/停止/错误等
	@Override
	public boolean onPlayStateChange(String id, int state) {
		if (mId==null||id==null||!id.equals(mId)
				||state==IMedia.Idle||state==IMedia.Error) {
			// 清除进度信息
			mDur = -1;
			mPos = -1;
			mBuffer = -1;
		}
		mId = id;
		if (state<IMedia.Custom) mState = state;
		return true;
	}
	// 监听进度+缓冲进度
	@Override
	public boolean onProgress(String id,int cur,int buffer,int total) {
		boolean bRet = false;
		if (mDur!=total) {
			mDur = total;
			bRet = true;
		}
		if (mBuffer!=buffer) {
			mBuffer = buffer;
			bRet = true;
		}
		if (mPos!=cur) {
			mPos = cur;
			bRet = true;
		}
		return bRet;
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
}
