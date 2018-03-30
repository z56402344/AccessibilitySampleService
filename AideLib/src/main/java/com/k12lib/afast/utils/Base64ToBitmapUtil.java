package com.k12lib.afast.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.k12lib.afast.log.Logger;

/**
 * Base64转换成Bitmap常用的一些方法
 *
 */
public class Base64ToBitmapUtil {
	/**
	 * 图片转换为base64编码的格式
	 * 
	 * @param imgPath
	 * @return
	 */
	public static String imgToBase64OfPath(String imgPath) {
		Bitmap bitmap = null;
		if (imgPath != null && imgPath.length() > 0) {
			bitmap = readBitmap(imgPath);
		}
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 图片转为base64
	 * 
	 * @param bitmap
	 *            bitmap图片
	 * @return
	 */
	public static String imgToBase64OfBtm(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();

			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Bitmap readBitmap(String imgPath) {
		try {
			return BitmapFactory.decodeFile(imgPath);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * base64转为bitmap图片
	 * 
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64toBitmap_(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		if (bitmap == null) {
			Logger.d("生成bitmap为空");
			return null;
		}
		return bitmap;
	}

	/**
	 * 保存图片到sdcard
	 * 
	 * @param bitmap
	 * @param imgName
	 * @param path
	 */
	public static void saveImageToSdcard(Bitmap bitmap, String imgName,
			String path) {

		BufferedOutputStream bos = null;
		try {
			File file = new File(path, imgName);
			// int end = _file.lastIndexOf(File.separator);
			// String _filePath = _file.substring(0, end);
			File filePath = new File(path);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file.createNewFile();
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		} catch (IOException e) {
			Logger.d(e.toString() + "\n" + e.getMessage());
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					Logger.d(e.toString() + "\n" + e.getMessage());
				}
			}
		}
	}

	/**
	 * 获取sdcard存储图片
	 * 
	 * @param imagePathName
	 * @return
	 */
	public static Bitmap getBitmap(String imagePathName) {
		File imageFile = new File(imagePathName);
		Bitmap bitmap = null;
		if (imageFile.exists()) {
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(
						imageFile));
			} catch (FileNotFoundException e) {
				Logger.d(e.toString() + "\n" + e.getMessage());
			}
		}
		return bitmap;
	}

	/**
	 * 
	 * @param base64Data
	 * @param imgName
	 *            图片格式
	 */
	public static Bitmap base64ToBitmap(String base64Data, String imgName,
			String path) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		File myCaptureFile = new File(path, imgName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myCaptureFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		boolean isTu = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		if (isTu) {
			// fos.notifyAll();
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
}
