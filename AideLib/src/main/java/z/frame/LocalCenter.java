package z.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.SparseArray;

import z.ext.frame.ZUIThread;

// app内部广播消息
public abstract class LocalCenter {
	// 消息对象
	public static class Msg extends HashMap<String,Object> implements Runnable {
		public int cmd; // 消息id
		public int arg; // 消息拓展参数
		public Object extra; // 消息拓展对象
		// 设置参数
		public Msg with(String k, Object v) {
			put(k,v);
			return this;
		}
		public Msg with(int args) {
			arg = args;
			return this;
		}
		public Msg with(Object ext) {
			extra = ext;
			return this;
		}
		// 获取参数
		public boolean vbool(String k) {
			return vbool(k,false);
		}
		public boolean vbool(String k,boolean def) {
			Object v = get(k);
			return v!=null&&(v instanceof Boolean)?(Boolean)v:def;
		}
		public int vint(String k) {
			return vint(k, 0);
		}
		public int vint(String k,int def) {
			Object v = get(k);
			return v!=null&&(v instanceof Number)?((Number)v).intValue():def;
		}
		public long vlong(String k) {
			return vlong(k, 0);
		}
		public long vlong(String k,long def) {
			Object v = get(k);
			return v!=null&&(v instanceof Number)?((Number)v).longValue():def;
		}
		public float vfloat(String k) {
			return vfloat(k, 0);
		}
		public float vfloat(String k,float def) {
			Object v = get(k);
			return v!=null&&(v instanceof Number)?((Number)v).floatValue():def;
		}
		public double vdouble(String k) {
			return vdouble(k, 0);
		}
		public double vdouble(String k,double def) {
			Object v = get(k);
			return v!=null&&(v instanceof Number)?((Number)v).doubleValue():def;
		}
		public String vstr(String k) {
			return vstr(k, null);
		}
		public String vstr(String k,String def) {
			Object v = get(k);
			return v!=null&&(v instanceof String)?((String)v):def;
		}
		public <T> T vobj(String k,Class<T> cls) {
			return vobj(k, cls, null);
		}
		public <T> T vobj(String k,Class<T> cls,T def) {
			Object v = get(k);
			return v!=null&&(cls.isInstance(v))?((T)v):def;
		}
		public <T> ArrayList<T> varr(String k) {
			return varr(k, null);
		}
		public <T> ArrayList<T> varr(String k,ArrayList<T> def) {
			Object v = get(k);
			return v!=null?((ArrayList<T>)v):def;
		}
		public <T> List<T> vlist(String k) {
			return vlist(k, null);
		}
		public <T> List<T> vlist(String k,List<T> def) {
			Object v = get(k);
			return v!=null?((List<T>)v):def;
		}
		// 发送
		public void send() {
			// 过滤无人关注的广播
			ArrayList<Receiver> receivers = center.get(cmd);
			if (receivers==null||receivers.size()==0) return;
			ZUIThread.postRunnable(this);
		}
		@Override
		public void run() {
			// dispatch msg
			ArrayList<Receiver> receivers = center.get(cmd);
			if (receivers==null||receivers.size()==0) return;
			// make a copy to invoid item's removed during dispatch
			receivers = new ArrayList<Receiver>(receivers);
			for (Receiver rec : receivers) {
				if (rec.cb!=null) {
					rec.cb.onReceive(rec,this);
				}
			}
		}

        @Override
        public boolean equals(Object o) {
            return o == this;
        }
    }

	// 创建实例
	public static Msg create(int cmdId) {
		Msg lm = new Msg();
		lm.cmd = cmdId;
		return lm;
	}

	// 发送简单消息
	public static void send(int cmd,int arg,Object extra) {
		create(cmd).with(arg).with(extra).send();
	}

	// 管理接收者
	private static SparseArray<CmdHolder> center = new SparseArray<CmdHolder>(8);
	private static class CmdHolder extends ArrayList<Receiver> {
		public int cmd; // 消息id
		public CmdHolder(int c) {
			super(8);
			cmd = c;
		}

        @Override
        public boolean equals(Object o) {
            return o == this;
        }
	}

	// 处理接口
	public static interface IReceiver {
		public void onReceive(Receiver ci,Msg msg);
	}
	// 接收器 负责接收并管理回调
	public static class Receiver extends ArrayList<CmdHolder> {
		private IReceiver cb;
		public Receiver(IReceiver callback) {
			super(8);
			cb = callback;
		}
		// 销毁
		public void destroy() {
			if (cb==null) return;
			cb = null;
			clear();
		}
		// 清空监听器
		@Override
		public void clear() {
			if (size()==0) return;
			for (CmdHolder receivers : this) {
				receivers.remove(this);
				if (receivers.size()==0) center.remove(receivers.cmd);
			}
			super.clear();
		}
		// 注册接收消息
		public Receiver register(int cmd) {
			if (cb==null) return this;
			CmdHolder receivers = center.get(cmd);
			if (receivers==null) {
				receivers = new CmdHolder(cmd);
				center.put(cmd,receivers);
			}
			if (!receivers.contains(this)) {
				receivers.add(this);
				add(receivers);
			}
			return this;
		}
		// 注销接收消息
		public Receiver unregister(int cmd) {
			CmdHolder receivers = center.get(cmd);
			if (receivers==null) return this;
			if (receivers.remove(this)) {
				remove(receivers);
				if (receivers.size()==0) center.remove(cmd);
			}
			return this;
		}
        @Override
        public boolean equals(Object o) {
            return o == this;
        }

	}
}
