package z.db;

import java.util.HashMap;
import android.content.ContentValues;

// 存放配置信息 (全局配置信息)
public interface ShareDB extends DBAttr {
	// 加载Sec信息
	public static class Sec extends DBAttr.Sec {
		public Sec(String sec) {
			super(sec,false);
		}
		// 读取sec
		public static int loadSec(String sec,HashMap<String,Object> attrs) {
			return loadSec(false,sec,attrs);
		}
		public static HashMap<String,Object> loadSec(String sec) {
			return loadSec(false,sec);
		}
		// 清除sec
		public static void clearSec(String sec) {
			clearSec(false,sec);
		}
		// 保存sec
		public static void saveSec(String sec,HashMap<String,Object> attrs) {
			saveSec(false,sec,attrs);
		}
	}
	// 直接操作key
	public static class Key extends DBAttr.Key {
		public static String loadString(String sec,String key) {
			return loadString(false,sec,key);
		}
		public static Integer loadInt(String sec,String key) {
			return loadInt(false,sec,key);
		}
		public static boolean loadBoolean(String sec,String key) {
			return loadBoolean(false,sec,key);
		}
		public static Double loadDouble(String sec,String key) {
			return loadDouble(false,sec,key);
		}
		public static void update(ContentValues cv) {
			update(false,cv);
		}
		public static void update(String sec,String key,String val) {
			update(false,sec,key,val);
		}
		public static void update(String sec,String key,int val) {
			update(false,sec,key,val);
		}
		public static void update(String sec,String key,boolean val) {
			update(false,sec,key,val);
		}
		public static void update(String sec,String key,double val) {
			update(false,sec,key,val);
		}	
		public static void clear(String sec,String key) {
			clear(false,sec,key);
		}
	}
}
