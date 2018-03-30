package com.k12lib.afast.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
/**
 * 操作资源文件工具类
 */
public class ResourceUtil {
	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string",
				paramContext.getPackageName());
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	public static int getStyleId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style",
				paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color",
				paramContext.getPackageName());
	}

	public static String parseAssetsString(Context context, String str) {
		String result = "";
		AssetManager asseets = context.getAssets();
		InputStream is = null;
		try {
			is = asseets.open("strings.xml");
			XmlPullParser xmlpull = Xml.newPullParser();
			try {
				xmlpull.setInput(is, "utf-8");
				int eventCode = xmlpull.getEventType();

				while (eventCode != XmlPullParser.END_DOCUMENT) {
					switch (eventCode) {
					case XmlPullParser.START_TAG:
						if (xmlpull.getName().equals("string")) {
							String name = xmlpull.getAttributeValue(null,
									"name");
							if (str.equals(name)) {
								result = xmlpull.nextText();
							}
						}
						break;
					}
					eventCode = xmlpull.next();// 没有结束xml文件就推到下个进行解�?
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static Bitmap getSDBitmap(String bmpPath) {
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = new FileInputStream(bmpPath);
			BufferedInputStream bufin = new BufferedInputStream(in);
			if (bufin != null) {
				bitmap = BitmapFactory.decodeStream(bufin);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
	public static Bitmap getAssertBitmap(Context context, String bmpName) {
		AssetManager asseets = context.getAssets();
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			try {
				is = asseets.open(bmpName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (is != null) {
				bitmap = BitmapFactory.decodeStream(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
	
	public static void CopyAssets(Context context, String assetDir, String dir) {
		String[] files;
		try {
			files = context.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		/*File path = Environment.getDataDirectory();  
		Logger.i("path ----------->" + path.getPath());*/
	    //StatFs stat = new StatFs(path.getPath());    
		
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {

			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(context, fileName, dir + fileName + "/");
					} else {
						CopyAssets(context, assetDir + "/" + fileName, dir + fileName
								+ "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = context.getAssets().open(assetDir + "/" + fileName);
				else
					in = context.getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
