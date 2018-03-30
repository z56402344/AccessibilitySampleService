package z.media;

// 媒体处理
public interface IMedia {
	// 播放器状态
	public static final int Null = 0;
	public static final int Idle = 1;
	public static final int Init = 2;
	public static final int Stop = Init; // init
	public static final int Prepareing = 3;
	public static final int Error = 4;
	public static final int Prepared = 5;
	public static final int Playing = 6;
	public static final int Pause = 7;
	public static final int Complete = 8;
	public static final int BufferIn = 9; // 缓冲中暂停
	public static final int BufferOut = Playing; // 缓冲中暂停恢复
	public static final int Custom = 1000; // 自定义状态

	// 播放器控制id
	public static final int CtrlPlay = 1;
	public static final int CtrlStop = 0;
	public static final int CtrlPause = -1;
	public static final int CtrlResume = -2;
	public static final int CtrlReset = -3;

	// 事件id
	public static final int Evt_Change = 100; // 播放器状态变更
	public static final int Evt_Prepared = 101; // 媒体准备播放
	public static final int Evt_Complete = 102;
	public static final int Evt_Progress = 103;
	public static final int Evt_Error = 104;
	public static final int Evt_Buffering = 105;

	public static final int ErrStart = 1001; // 媒体开始播放出错
	public static final int ErrPlaying = 1002; // 媒体播放过程出错

	public static final int MaxRate = 1000000; // 防止浮点数问题
	public static class Rate {
		// 0.0f-1.0f 到百万分比
		public static int toRate(float rate) {
			return rate >= 1 ? MaxRate : (int)(rate * MaxRate);
		}
		// 从进度到百万分比
		public static int toRate(long cur, long total) {
			return cur < total ? (int)(cur * MaxRate / total) : MaxRate;
		}
		// 从百万分比到进度
		public static int toProgress(long curRate, int total) {
			return curRate < MaxRate ? (int)(curRate * total / MaxRate) : total;
		}
	}

		// 监听媒体播放
	public static interface ILis {
		// 监听播放状态 播放/暂停/停止/错误等
		public boolean onPlayStateChange(String id,int state);
		// 监听进度+缓冲进度
		public boolean onProgress(String id,int cur,int buffer,int total);
	}
}
