package z.media;

import java.util.HashMap;

// 实现包装多个 并分发下去
public class BaseCntLis extends HashMap<Object,IMedia.ILis> implements IMedia.ILis {
	public BaseLis mInner = new BaseLis();
	// 监听播放状态 播放/暂停/停止/错误等
	@Override
	public boolean onPlayStateChange(String id, int state) {
		if (mInner.onPlayStateChange(id,state)) {
			dispatchStateChange(id,state);
		}
		return true;
	}
	// 分发播放状态消息
	public void dispatchStateChange(String id, int state) {
		IMedia.ILis lis;
		for (Entry<Object, IMedia.ILis> et : super.entrySet()) {
			lis = et.getValue();
			if (lis!=null) {
				lis.onPlayStateChange(id,state);
			}
		}
	}
	// 监听进度+缓冲进度
	@Override
	public boolean onProgress(String id,int cur,int buffer,int total) {
		if (mInner.onProgress(id,cur,buffer,total)) {
			dispatchProgress(id,cur,buffer,total);
		}
		return true;
	}
	// 分发进度消息
	public void dispatchProgress(String id,int cur,int buffer,int total) {
		IMedia.ILis lis;
		for (Entry<Object, IMedia.ILis> et : super.entrySet()) {
			lis = et.getValue();
			if (lis!=null) {
				lis.onProgress(id,cur,buffer,total);
			}
		}
	}

	// 是否处在播放过程中(进度等参数有效) 注:非播放中状态
	public boolean inPlaying() {
		return mInner.inPlaying();
	}
	@Override
	public boolean equals(Object object) {
		return object == this;
	}
}
