package z.down;

import java.io.File;

import z.http.IZHttp;
import z.http.ZHttpItem;

// 另一种文件下载方式
public class AsynFile extends ZHttpItem implements IZHttp,IDown {
	public String mId;
	public File mRealPath;
	public long mLastUpdate; // 最后更新时间
	public IDown.IPostHandler mIPost = null;
	public int mState = 0;
	public int statusCode; // 请求状态码
	public AsynFile setPostHandler(IDown.IPostHandler ip) {
		mIPost = ip;
		return this;
	}

	public AsynFile(String id, String url, File path, int begin) {
		mId = id;
		mRealPath = path;
		setUrl(url);
		setMethod(IZHttp.ReqGet | IZHttp.RspFile);
		url = path.getAbsolutePath();
		int dot = url.lastIndexOf('.');
		if (dot>=0) {
			// 123.txt => 123.pt.txt
			url = url.substring(0,dot)+SuffixPt+url.substring(dot);
		} else {
			// 123 => 123.pt
			url = url + SuffixPt;
		}
		setDstFile(url, begin); // 断点续传
		updateLastTs();
	}

	public int getPercent(int multi) {
		if (mProgress==null || mProgress[0] == 0 || mProgress[1] == 0)
			return 0;
		// 需要后处理
		return (int) (mProgress[0] * multi / (mIPost!=null?(mProgress[1]+1):mProgress[1]));
	}

	// 检验是否严重超时
	public boolean checkTimeout(int sec) {
		long cur = System.currentTimeMillis() - sec * 1000;
		return mLastUpdate < cur;
	}

	// 更新时间戳
	public void updateLastTs() {
		mLastUpdate = System.currentTimeMillis();
	}

	// 获取实际进度
	public long getCur() {
		return mProgress!=null?mProgress[0]:0;
	}
	public long getTotal() {
		return mProgress!=null?mProgress[1]:1;
	}
}
