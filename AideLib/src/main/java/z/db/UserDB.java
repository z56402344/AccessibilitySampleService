package z.db;

import java.util.HashMap;
import android.content.ContentValues;

// 存放配置信息 (用户独立配置信息)
public interface UserDB extends DBAttr {
	// 加载Sec信息
	public static class Sec extends DBAttr.Sec {
		public Sec(String sec) {
			super(sec,true);
		}
		// 读取sec
		public static int loadSec(String sec,HashMap<String,Object> attrs) {
			return loadSec(true,sec,attrs);
		}
		public static HashMap<String,Object> loadSec(String sec) {
			return loadSec(true,sec);
		}
		// 清除sec
		public static void clearSec(String sec) {
			clearSec(true,sec);
		}
		// 保存sec
		public static void saveSec(String sec,HashMap<String,Object> attrs) {
			saveSec(true,sec,attrs);
		}
	}
	// 直接操作key
	public static class Key extends DBAttr.Key {
		public static String loadString(String sec,String key) {
			return loadString(true,sec,key);
		}
		public static Integer loadInt(String sec,String key) {
			return loadInt(true,sec,key);
		}
		public static boolean loadBoolean(String sec,String key) {
			return loadBoolean(true,sec,key);
		}
		public static Double loadDouble(String sec,String key) {
			return loadDouble(true,sec,key);
		}
		public static void update(ContentValues cv) {
			update(true,cv);
		}
		public static void update(String sec,String key,String val) {
			update(true,sec,key,val);
		}
		public static void update(String sec,String key,int val) {
			update(true,sec,key,val);
		}
		public static void update(String sec,String key,boolean val) {
			update(true,sec,key,val);
		}
		public static void update(String sec,String key,double val) {
			update(true,sec,key,val);
		}	
		public static void clear(String sec,String key) {
			clear(true,sec,key);
		}
	}
}
