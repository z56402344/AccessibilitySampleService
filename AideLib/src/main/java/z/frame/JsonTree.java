package z.frame;

import java.util.ArrayList;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

// 封装json的深入访问
public class JsonTree {
    public JSONObject json; // 回复包体
    public String lastKey;
    public JsonTree() {
    }
    public JsonTree(JSONObject js) {
        json = js;
    }
    public JsonTree setData(JSONObject js) {
        json = js;
        return this;
    }
	public JsonTree setData(byte[] data) {
		return setData(new String(data));
	}
	public JsonTree setData(String data) {
		try {
			json = JSON.parseObject(data);
		} catch (Exception e) {
			e.printStackTrace();
			json = null;
		}
		return this;
	}
    // 解析对象
    public <T> T getObject(Class<T> cls,String...paths) {
        JSONObject last = lastParent(paths);
        if (last==null||lastKey==null) return null;
//        return last.getObject(lastKey,cls);
	    T t = null;
	    try {
		    t = JSON.parseObject(last.getString(lastKey), cls);
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
	    return t;
    }
	public static  <T> T getObject(String s,Class<T> cls) {
		if (TextUtils.isEmpty(s)) return null;
		T t = null;
		try {
			t = JSON.parseObject(s,cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
    // 解析数组
    public <T> ArrayList<T> getArray(Class<T> cls, String... paths) {
        String val = getString(paths);
	    ArrayList<T> ar = null;
	    try {
		    ar = val!=null? (ArrayList<T>)JSON.parseArray(val, cls):null;
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
	    return ar;
    }
	public static <T> ArrayList<T> getArray(String s,Class<T> cls) {
		if (TextUtils.isEmpty(s)) return null;
		ArrayList<T> ar = null;
		try {
			ar = (ArrayList<T>)JSON.parseArray(s, cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ar;
	}
	public static JSONArray getJSONArray(String s) {
		if (TextUtils.isEmpty(s)) return null;
		JSONArray ar = null;
		try {
			ar = JSON.parseArray(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ar;
	}

    // 获取最后一个父节点对象
    public JSONObject lastParent(String... paths) {
        if (json==null) return null;
        JSONObject sub = json;
        int len = paths.length;
        for (String p : paths) {
            if (--len==0) {
                lastKey = p;
                return sub;
            }
            sub = sub.getJSONObject(p);
            if (sub==null) {
                return null;
            }
        }
        return null;
    }
    // 获取字符串
    public String getString(String... paths) {
        JSONObject last = lastParent(paths);
        if (last==null||lastKey==null) return null;
        return last.getString(lastKey);
    }
    // 获取bool
    public boolean getBoolean(boolean def,String... paths) {
        JSONObject last = lastParent(paths);
        if (last==null||lastKey==null) return def;
        Boolean i = last.getBoolean(lastKey);
        return i!=null?i:def;
    }
    // 获取整数
    public int getInt(int def,String... paths) {
        JSONObject last = lastParent(paths);
        if (last==null||lastKey==null) return def;
        Integer i = last.getInteger(lastKey);
        return i!=null?i:def;
    }
	public long getLong(long def,String... paths) {
		JSONObject last = lastParent(paths);
		if (last==null||lastKey==null) return def;
		Long i = last.getLong(lastKey);
		return i!=null?i:def;
	}
    // 获取浮点数
    public double getDouble(double def,String... paths) {
        JSONObject last = lastParent(paths);
        if (last==null||lastKey==null) return def;
        Double i = last.getDouble(lastKey);
        return i!=null?i:def;
    }
	public static String getOneString(String json,String key) {
		if (TextUtils.isEmpty(json)) return null;
		try {
			JSONObject jsonObject = JSON.parseObject(json);
			return jsonObject!=null?jsonObject.getString(key):null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getOneInt(String json,String key,int def) {
		if (TextUtils.isEmpty(json)) return def;
		try {
			JSONObject jsonObject = JSON.parseObject(json);
			return jsonObject!=null?jsonObject.getInteger(key):def;
		} catch (Exception e) {
			e.printStackTrace();
			return def;
		}
	}

	public static String toJSONString(Object obj) {
		if (obj==null) return null;
		try {
			return (obj instanceof JSON)?obj.toString():JSON.toJSONString(obj);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJSONString(json);
	}

	public JsonTree init() {
		json = new JSONObject();
		return this;
	}
	public JsonTree put(String k,Object v) {
		if (json==null) json = new JSONObject();
		json.put(k,v);
		return this;
	}
	// 添加多个k,v对 buildObject(k1,v1,k2,v2...kn,vn)
	public static JSONObject buildObject(String k,Object... vs) {
		JSONObject json = new JSONObject();
		for (Object v : vs) {
			if (k!=null) {
				json.put(k,v);
				k = null;
			} else {
				if (v==null||!(v instanceof String)) return json;
				k = (String)v;
			}
		}
		return json;
	}
	public static JSONArray buildArray(Object... vs) {
		JSONArray json = new JSONArray();
		for (Object v : vs) {
			json.add(v);
		}
		return json;
	}
}
