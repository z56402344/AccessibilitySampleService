package com.k12lib.afast.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.k12lib.afast.log.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * 有关头像处理工具类
 */
public class BitmapUtils {
	
	/**
	 * 截取指定宽高的Bitmap类(正方形)
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zf){
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	/**
	 * 截取指定宽高的Bitmap类(长方形)
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float wf,float hf)
	{
		Matrix matrix = new Matrix();
		matrix.postScale(wf,hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 截取指定宽高的Bitmap类(圆形)
	 * 
	 * @param bitmap
	 * @param roundPX
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX)
	{
		// RCB means
		// Rounded
		// Corner Bitmap
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbmp);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return dstbmp;
	}
	
	/**
     * 将图片转换成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
    
    /**
     * 将Drawable转换成Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap convertDraToBit(Drawable drawable){
  	  Bitmap bitmap = Bitmap.createBitmap(
  			  drawable.getIntrinsicWidth(),
  			  drawable.getIntrinsicHeight(),
  			  drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
  			  : Bitmap.Config.RGB_565);
  			  Canvas canvas = new Canvas(bitmap);
  			  // canvas.setBitmap(bitmap);
  			  drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
  			  drawable.getIntrinsicHeight());
  			  drawable.draw(canvas);
  	  return bitmap;
    }
    
 // Bitmap → Drawable
    public static Drawable convertBitmap2Drawable(Bitmap bitmap) {
    BitmapDrawable bd = new BitmapDrawable(bitmap);
    // 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
    return bd;
    }


    /****
     * 将bitmap保存到本地，如果本地已存在该图片，则将该图片删除，再放新的
     * @param file
     * @param bitmap
     */
    public static void saveBitmap2Local(File file,Bitmap bitmap){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 60, fos);
            try {
                fos.flush();
                fos.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
    }

    public static void saveBitmap2Local(String dirPath, String fileName,Bitmap bitmap){
        try {
            String pathName = Environment.getExternalStorageDirectory() + "/boc_container/" + fileName;
            File file = new File(fileName);
            Logger.e("before ---"+file.exists());
            if(file.exists()) {
                file.delete();
            }
            Logger.e("after ---"+file.exists());
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /****
     * 将bitmap保存到手机相册
     * @param fileName
     * @param bitmap
     */
    public static void saveBitmap2Camera(String fileName, Bitmap bitmap) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_DCIM), "Camera");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地获取头像文件
     * @param fileName
     */
    public static Bitmap getBitmapFromLocal(String fileName) {
    	Bitmap bitmap = null;
    	String pathName = Environment.getExternalStorageDirectory() + "/boc_container/" + fileName;
    	File file = new File(pathName);
    	if(file.exists()) {
    		bitmap = BitmapFactory.decodeFile(pathName);
    	}
    	return bitmap;
    }
    
    public static Bitmap getBitmapAutoResize(String fileName) {
    	if(fileName == null || "".equals(fileName)) {
    		return null;
    	}
    	Bitmap bm = null;
    	Options opts = new Options();
    	opts.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(fileName, opts);
    	Logger.d(opts.outWidth+""+opts.outHeight+""+opts.outMimeType);
    	opts.inJustDecodeBounds = false;
    	int sampleSize = 1;
    	while(true) {
    		if (opts.outWidth*opts.outHeight / sampleSize < 1281*901) {
    			break;
    		}
    		sampleSize *= 2;
    	}
    	Logger.e("sampleSize:" + sampleSize);
    	opts.inSampleSize = sampleSize;
    	bm = BitmapFactory.decodeFile(fileName, opts);
    	return bm;
    }
    
    /**************************图片缓存器，缓存过程中不会降低图片质量，建议不要缓存过大的图片**************************************/
    private static BitmapUtils bitmapCache;
	private static final String DISK_CACHE_PATH = "/Afast_bitmap_cache/";
	private ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache;
	private String diskCachePath;
	private boolean diskCacheEnabled = false;
	private ExecutorService writeThread;
	

	public static BitmapUtils getInstance(Context context) {
		if(bitmapCache == null) {
			bitmapCache = new BitmapUtils(context);
		}
		return bitmapCache;
	}
	
	public BitmapUtils(Context context) {
		memoryCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

		Context appContext = context.getApplicationContext();
		diskCachePath = appContext.getCacheDir().getAbsolutePath()
				+ DISK_CACHE_PATH;

		File outFile = new File(diskCachePath);
		outFile.mkdirs();

		diskCacheEnabled = outFile.exists();

		writeThread = Executors.newSingleThreadExecutor();
	}

	/**
	 * 从缓存中拿到bitmap
	 * @param url 图片的utl
	 * @return
	 */
	public Bitmap get(final String url) {
		Bitmap bitmap  = getBitmapFromMemory(url);
		Logger.d("get url ===" + url + "," + (bitmap == null));
		if (bitmap == null) {
			bitmap = getBitmapFromDisk(url);

			if (bitmap != null) {
				cacheBitmapToMemory(url, bitmap);
			}
		}

		return bitmap;
	}
	/**'
	 * 缓存一个bitmap，key为url
	 * @param url
	 * @param bitmap
	 */
	public void put(String url, Bitmap bitmap) {
		cacheBitmapToMemory(url, bitmap);
		cacheBitmapToDisk(url, bitmap);
	}
	/**
	 * 从缓存中移除掉一个bitmap
	 * @param url
	 */
	public void remove(String url) {
		if (url == null) {
			return;
		}

		memoryCache.remove(getCacheKey(url));

		File f = new File(diskCachePath, getCacheKey(url));
		if (f.exists() && f.isFile()) {
			f.delete();
		}
	}
	/**
	 * 清空缓存
	 */
	public void clear() {
		memoryCache.clear();

		File cachedFileDir = new File(diskCachePath);
		if (cachedFileDir.exists() && cachedFileDir.isDirectory()) {
			File[] cachedFiles = cachedFileDir.listFiles();
			for (File f : cachedFiles) {
				if (f.exists() && f.isFile()) {
					f.delete();
				}
			}
		}
	}
	/**
	 * 把bitmap缓存到内存
	 * @param url
	 * @param bitmap
	 */
	private void cacheBitmapToMemory(final String url, final Bitmap bitmap) {
		Logger.d("cacheBitmapToMemory url --- " + url);
		memoryCache.put(getCacheKey(url), new SoftReference<Bitmap>(bitmap));
	}
	/**
	 * 把bitmap缓存到硬盘
	 * @param url
	 * @param bitmap
	 */
	private void cacheBitmapToDisk(final String url, final Bitmap bitmap) {
		writeThread.execute(new Runnable() {
			@Override
			public void run() {
				if (diskCacheEnabled) {
					BufferedOutputStream ostream = null;
					try {
						ostream = new BufferedOutputStream(
								new FileOutputStream(new File(
										diskCachePath, getCacheKey(url))),
								2 * 1024);
						bitmap.compress(CompressFormat.PNG, 100, ostream);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							if (ostream != null) {
								ostream.flush();
								ostream.close();
							}
						} catch (IOException e) {
						}
					}
				}
			}
		});
	}
	/**
	 * 从内存中得到bitmap
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromMemory(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softRef = memoryCache.get(getCacheKey(url));
		if (softRef != null) {
			bitmap = softRef.get();
		}

		return bitmap;
	}
	/**
	 * 从硬盘上得到bitmap
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromDisk(String url) {
		Bitmap bitmap = null;
		if (diskCacheEnabled) {
			String filePath = getFilePath(url);
			File file = new File(filePath);
			if (file.exists()) {
				InputStream stream;
				try {
					stream = new FileInputStream(filePath);
					bitmap = BitmapFactory.decodeStream(stream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
			}
		}
		return bitmap;
	}

	private String getFilePath(String url) {
		return diskCachePath + getCacheKey(url);
	}

	private String getCacheKey(String url) {
		if (url == null) {
			throw new RuntimeException("url不能为空");
		} else {
			return url.replaceAll("[.:/,%?&=]", "+")
					.replaceAll("[+]+", "+");
		}
	}
	
	private static final int CONNECT_TIMEOUT = 10000;
	private static final int READ_TIMEOUT = 30000;
	
	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			bitmap = BitmapFactory.decodeStream((InputStream) conn
					.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
