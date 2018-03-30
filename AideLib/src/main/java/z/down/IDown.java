package z.down;

import android.text.TextUtils;

import com.k12lib.afast.utils.ZipUtil;

import java.io.File;

import z.frame.Util;


// 下载模块常量及小类
public interface IDown {
	// 进度保存文件
	// 123.mp3 -> 123.pt.mp3 -> 123.pt.mp3.ts
	public static final String SuffixPt = ".pt"; // 下载中文件
	public static final String SuffixTs = ".ts"; // 下载进度文件

	// 任务状态
	public static int STNull = 0; // 未开启
	public static int STDoing = 1; // 请求中
	public static int STDownOK = 2; // 请求完成
	public static int STDownFail = 3; // 请求完成
	public static int STOK = 4; // 所有操作完成
	public static int STFail = 5;

	// 下载后处理
	public static interface IPostHandler {
		public boolean onPostDownload(AsynFile task, boolean bOK);
	}
	// ui线程处理
	public static interface IUIHandler extends IPostHandler {
		public boolean onPostUI(AsynFile task, boolean bOK);
	}
	// 重命名处理
	public static class RenameHandler implements IPostHandler {
		public static void renameDownloadFile(AsynFile af) {
			File file = af.getDstFile();
			if (!file.exists()) return;

			File f = af.mRealPath;
			if (f.exists()) {
				f.delete();
			}
			file.renameTo(f);
		}
		@Override
		public boolean onPostDownload(AsynFile af, boolean bOK) {
			if (bOK) {
				renameDownloadFile(af);
			}
			return true;
		}
	}
	// 压缩包解压
    // 写时间戳+解压处理
	public static class UnzipHandler extends RenameHandler {
		public String mDstDir;
        public boolean isDelete = false;
		public UnzipHandler(String dstDir,boolean isDelete) {
			mDstDir = dstDir;
            this.isDelete = isDelete;
		}
        public String mTs;
        public String mTsFile;
        // 写时间戳+解压处理
        public UnzipHandler(String dstDir, String ts, String tsf) {
            this(dstDir, true);
            mTs = ts;
            mTsFile = tsf;
        }
		@Override
		public boolean onPostDownload(AsynFile task, boolean bOK) {
			super.onPostDownload(task,bOK);
			if (!bOK|| TextUtils.isEmpty(mDstDir)) return false;
			try {
				// 解压指定文件
				ZipUtil.unzip(task.mRealPath.getAbsolutePath(), mDstDir);
                if (!TextUtils.isEmpty(mTs)&&!TextUtils.isEmpty(mTsFile)) {
                    return Util.writeText(mTs, mTsFile);
                }
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
                if (isDelete){
                    try {
                        File f = task.mRealPath;
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
			}
			return true;
		}
	}
}
