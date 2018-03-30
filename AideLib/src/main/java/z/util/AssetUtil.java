package z.util;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import z.frame.ICommon;

/**
 * 说明：Asset资源管理
 */
public class AssetUtil implements ICommon {
	/** 复制Assets文件夹下的图片文件夹到指定目录. */
	public static boolean copyAssetFiles(String srcPath, File dstDir) {
		try {
			// 初始化即将保存到的文件夹目录
			if (!dstDir.exists()) {
				dstDir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		AssetManager assets = app.ctx.getAssets();
		String[] files = null;
		try {
			files = assets.list(srcPath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (files==null||files.length==0) {
			return true;
		}
		InputStream assetFile = null;
		FileOutputStream out = null;
		byte[] buf = new byte[64*1024];
		File outFile;
		int failCount = 0;
		for (int i = 0; i < files.length; i++) {
			try {
				assetFile = assets.open(srcPath + "/" + files[i]);
				outFile = new File(dstDir,files[i]);
				if (!outFile.exists()) {
					File outDir = outFile.getParentFile();
					if (!outDir.exists()) outDir.mkdirs();
					outFile.createNewFile();
				}
				out = new FileOutputStream(outFile);
				int readedBytes;
				while ((readedBytes = assetFile.read(buf)) > 0) {
					out.write(buf, 0, readedBytes);
				}
				out.flush();
				assetFile.close();
				assetFile = null;
				out.close();
				out = null;
			} catch (Exception e) {
				e.printStackTrace();
				Util.closeSafe(assetFile);
				Util.closeSafe(out);
				++failCount; // 失败计数+1
				continue;
			}
		}
		return true;
	}
}