package z.frame;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.k12lib.afast.log.Logger;

// 通用功能
public interface ICommon {
	public static final String kFID = "kFID"; // 通用传递fragment id
	// 存放fragment事务
	public static final int PF_Add = 0x01; // add/0=replace 半透明需要用add
	public static final int PF_Back = 0x02; // can back

	public static final int REQ_StartActivity = 8099;

	public static final int TextOnly = 1;
	public static final int ImageOnly = 2;
	public static final int TextImage = 3;
	public static final int MusicImage = 4;
	public static final int TFail = -1;
	public static final int TCancel = 0;
	public static final int TQQ = 1;
	public static final int TWeibo = 2;
	public static final int TWeixin = 3;
	public static final int TWeixinCircle = 4;
	public static final int TOther = 1000;

    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

	public static class Util {
		// 设置点击监听
		public static View setOnClick(View root, int id, View.OnClickListener lis) {
			if (root==null) return null;
			root = root.findViewById(id);
			if (root!=null) {
				root.setOnClickListener(lis);
			}
			return root;
		}
		// 设置文本
		public static View setText(View root, int id, Object txt) {
			if (root==null) return null;
			root = root.findViewById(id);
			if (root==null) return null;
			if (root instanceof TextView) {
				TextView tv = ((TextView)root);
				if (txt instanceof CharSequence) {
					tv.setText((CharSequence)txt);
				} else if (txt instanceof Integer) {
					tv.setText((Integer)txt);
				}
			}
			return root;
		}
		// 设置图片
		public static void setImageDrawable(View root, int resId, Drawable drawable) {
			if (root == null) return;
			root = root.findViewById(resId);
			if (root == null) return;
			if (root instanceof ImageView) {
				ImageView iv = ((ImageView) root);
				iv.setImageDrawable(drawable);
			}
		}

		public static void setImageResource(View root, int resId, int drawableId) {
			if (root == null) return;
			root = root.findViewById(resId);
			if (root == null) return;
			if (root instanceof ImageView) {
				ImageView iv = ((ImageView) root);
				iv.setImageResource(drawableId);
			}
		}
		// 设置可见
		public static View setVisible(View root, int id, int vis) {
			if (root==null) return root;
			root = root.findViewById(id);
			if (root!=null) {
				root.setVisibility(vis);
			}
			return root;
		}
		public static void setVisible(View v, int vis) {
			if (v!=null) {
				v.setVisibility(vis);
			}
		}
		public static void setSelected(View v, boolean selected) {
			if (v != null) {
				v.setSelected(selected);
			}
		}
		// 设置文本颜色
		public static void setTextColorId(TextView tv, int resId) {
			tv.setTextColor(tv.getResources().getColor(resId));
		}
		// 记录日志
		public static void _log(String tag,String txt) {
			Logger.w(tag, txt);
		}
		public static String buildTag(Object ts) {
			return String.format("IAct(%x):%s",ts.hashCode(),ts.getClass().getSimpleName());
		}
		// 从父窗口移除
		public static void setParent(View v,ViewGroup vg,ViewGroup.LayoutParams lp) {
			setParent(v, vg, lp, -1);
		}
		// 从父窗口移除
		public static void setParent(View v,ViewGroup vg,ViewGroup.LayoutParams lp, int order) {
			if (v==null) return;
			ViewGroup old = (ViewGroup)v.getParent();
			if ( old==vg) return;
			try { // 华为H60-L03
				if (old != null) old.removeView(v);
				if (vg!=null) {
					if (lp != null) {
						if (order != -1){
							vg.addView(v,lp);
						}else{
							vg.addView(v, order, lp);
						}
					}else {
						if (order != -1){
							vg.addView(v, order);
						}else{
							vg.addView(v);
						}
					}
				}
			} catch (Throwable e) {
			}
		}
		// 获取资源颜色
		public static int getColor(View v,int id) {
			return v.getResources().getColor(id);
		}

		// 从ViewStub中获取布局
		public static View fetch(View v,boolean useSelfLayoutParams) {
			if (v instanceof ViewStub) {
				if (useSelfLayoutParams) {
					v.setLayoutParams(null);
				}
				v = ((ViewStub)v).inflate();
			}
			return v;
		}
		// 从子控件ViewStub中获取布局
		public static View fetchChild(View v,int id,boolean useSelfLayoutParams) {
			if (v == null) return null;
			return fetch(v.findViewById(id),useSelfLayoutParams);
		}

		public static void startRotaAnim(final ImageView view, final View view2) {
			// 如果view为空，就什么都不做,容错处理
			if (view == null)
				return;
			Drawable drawable = view.getDrawable();
			if (drawable instanceof AnimationDrawable){
				AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
				animationDrawable.start();
			}
			view.setVisibility(View.VISIBLE);
			if (view2 != null) {
				view2.setVisibility(View.INVISIBLE);
			}
		}

		public static void clearRotaAnim(final ImageView view, final View view2) {
			if (view != null) {
				view.setVisibility(View.INVISIBLE);
			}
			if (view2 != null) {
				view2.setVisibility(View.VISIBLE);
			}
			if (view == null) return;
			Drawable drawable = view.getDrawable();
			if (drawable instanceof  AnimationDrawable){
				AnimationDrawable animationDrawable = (AnimationDrawable)drawable ;
				animationDrawable.stop();
			}
		}
		/**设置资源并启动动画**/
		public static void setAnim(ImageView view, int id){
			if (view == null) return;
			Drawable drawable = view.getDrawable();
			if (drawable != null && drawable instanceof AnimationDrawable){
				((AnimationDrawable) drawable).stop();
			}
			view.setImageResource(id);
			drawable = view.getDrawable();
			if (drawable != null && drawable instanceof AnimationDrawable){
				((AnimationDrawable) drawable).start();
			}
		}
		/**直接启动动画**/
		public static void startAnim(ImageView view){
			if (view == null) return;
			Drawable drawable = view.getDrawable();
			if (drawable != null && drawable instanceof AnimationDrawable){
				((AnimationDrawable) drawable).start();
			}
		}
		/**直接清除动画**/
		public static void clearAnim(ImageView view){
			if (view == null) return;
			Drawable drawable = view.getDrawable();
			if (drawable != null && drawable instanceof AnimationDrawable){
				((AnimationDrawable) drawable).stop();
			}
		}
		public static boolean closeSafe(java.io.Closeable c) {
			if (c!=null) {
				try {
					c.close();
				} catch (Throwable e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
	}

	public static final int ID_Loading = 300; // loading dialog id
	public static final int ID_SHARE = ID_Loading - 1; // share dialog id

	public static final int IA_Run = 101; // 执行run
	public static final int IA_RfCom = 102; // 执行
	public static final int IA_ShareRes = 103;

	public static AppCommonInfo app = new AppCommonInfo();

	public static final String HttpErr = "wys_http_err"; // http请求返回错误
	public static final String AppWarn = "wys_app_warning"; // app中发现的一些错误
}
