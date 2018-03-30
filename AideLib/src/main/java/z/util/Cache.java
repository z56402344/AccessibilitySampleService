package z.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import z.frame.JsonTree;

// 使用属性类缓存数据 并实现常用数据转换
public class Cache {
	public static final int ZipMask = 0x88;
	public static final int ZipOn = 0x80;
	public static final int ZipOff = 0x08;
	public void enableZip(boolean bOn) {
		mZip = bOn;
	}
	private boolean mZip = true;
	private File mFile;
	private HashMap<String, Object> cacheMap = new HashMap<String, Object>(8);
	public Cache(String path) {
		mFile = new File(path);
	}
	public Cache(File path) {
		mFile = path;
	}
	public Cache(String dir, String id, boolean bMd5) {
		mFile = new File(dir,(bMd5?MD5.md5Lower(id):id)+".ch");
	}
	public Cache(File dir, String id, boolean bMd5) {
		mFile = new File(dir,(bMd5?MD5.md5Lower(id):id)+".ch");
	}

	/**
	 * 根据当前对象的path，id来加载properties对象。将数据存储到内存中的cacheMap里
	 */
	public void load() {
		// 清空数据
		cacheMap.clear();
		Properties properties = new Properties();
		if (load(properties,mFile)) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				cacheMap.put((String) entry.getKey(), entry.getValue());
			}
		}
	}

	public void save() {
		// 根据缓存路径创建缓存文件
		Properties properties = new Properties();
		if (cacheMap.size()>0) {
			for (Entry<String, Object> entry : cacheMap.entrySet()) {
				Object obj = entry.getValue();
				if (obj instanceof Integer || obj instanceof String
				    || obj instanceof Boolean || obj instanceof Long
				    || obj instanceof Float || obj instanceof Double) {
					// 不需要解析，直接存储
					properties.setProperty(entry.getKey(), String.valueOf(obj));
				} else {
					// 需要解析进行存储
					String v = JsonTree.toJSONString(obj);
					if (v!=null) properties.setProperty(entry.getKey(), v);
				}
			}
		}
		save(properties,mFile,mZip);
	}

	/**
	 * @param key ,键
	 * @param def, 默认值
	 * 根据键获取int值的方法,如果不能获取就返回默认值
	 */
	public int getInt(String key, int def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			if (v instanceof Integer) return (Integer)v;
			if (v instanceof Long) return (int)(long)(Long)v;
			if (v instanceof String) return Integer.parseInt((String)v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * @param key ,键
	 * @param def, 默认值
	 * 根据键获取String值的方法,如果不能获取就返回默认值
	 */
	public String getString(String key, String def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			return String.valueOf(v);
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}

	/**
	 * @param key ,键
	 * @param def, 默认值
	 * 根据键获取boolean值的方法,如果不能获取就返回默认值
	 */
	public boolean getBoolean(String key, boolean def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			if (v instanceof Boolean) return (Boolean)v;
			if (v instanceof Integer) return ((Integer)v)==1;
			if (v instanceof String) return Boolean.parseBoolean((String)v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * @param key ,键
	 * @param def, 默认值
	 * 根据键获取Long值的方法,如果不能获取就返回默认值
	 */
	public long getLong(String key, long def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			if (v instanceof Integer) return (Integer)v;
			if (v instanceof Long) return (Long)v;
			if (v instanceof String) return Long.parseLong((String)v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * @param key ,键
	 * @param def, 默认值
	 * 根据键获取float值的方法,如果不能获取就返回默认值
	 */
	public float getFloat(String key, float def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			if (v instanceof Float) return (Float)v;
			if (v instanceof Double) return (float)(double)(Double)v;
			if (v instanceof String) return Float.parseFloat((String)v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * 
	  * @param key ,键
	 * @param def, 默认值
	 * 根据键获取double值的方法,如果不能获取就返回默认值
	 */
	public double getDouble(String key, double def) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return def;
			if (v instanceof Float) return (Float)v;
			if (v instanceof Double) return (Double)v;
			if (v instanceof String) return Double.parseDouble((String)v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * 根据键获取集合的方法,如果不能获取就返回null
	 * 
	 * @param <T>
	 */
	public <T> ArrayList<T> getList(String key, Class<T> clazz) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return null;
			if (v instanceof ArrayList) return (ArrayList<T>)v;
			if (v instanceof String) return JsonTree.getArray((String)v, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据键获取任意对象的方法,如果不能获取就返回null
	 */
	public <T> T getObject(String key, Class<T> clazz) {
		Object v = cacheMap.get(key);
		try {
			if (v==null) return null;
			if (clazz.isInstance(v)) return (T)v;
			if (v instanceof String) return JsonTree.getObject((String)v, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param key
	 * @param v
	 */
	public void put(String key, Object v) {
		cacheMap.put(key, v);
	}

	// 考虑压缩存取
	// 文件 => 属性
	public static boolean load(Properties properties,File path) {
		properties.clear();
		if (!path.exists()) return false;
		try {
			InputStream in = new FileInputStream(path);
			int head = in.read();
			if ((head&ZipMask)==ZipOn) {
				in.skip(head&0x7);
				in = new GZIPInputStream(in);
			} else if ((head&ZipMask)==ZipOff) {
			} else {
//				in.reset();
			}
			properties.load(in);
			in.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	// 保存到属性文件
	public static boolean save(Properties properties,File path,boolean bZip) {
		if (!path.exists()) {
			path.getParentFile().mkdirs();
		}
		try {
			OutputStream out = new FileOutputStream(path);
			if (bZip) {
				int head = out.hashCode()&0x77;
				out.write(head|ZipOn);
				int bytes = head;
				head &= 0x07;
				while (head-->0) {
					bytes += head;
					out.write(bytes);
				}
				out = new GZIPOutputStream(out);
			} else {
				int head = out.hashCode()&0x77;
				out.write(head|ZipOff);
			}
			properties.store(out, "");
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
