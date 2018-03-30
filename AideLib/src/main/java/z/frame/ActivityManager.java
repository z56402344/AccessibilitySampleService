package z.frame;

import android.app.Activity;
import android.text.TextUtils;

import com.k12lib.afast.log.Logger;

import java.util.Stack;

/**
 * 说明：activity管理器
 */
public class ActivityManager {
	public Stack<Activity> mActivityStack;
	/**
	 * 将当前Activity推入栈中
	 * @param activity
	 */
	public void onCreate(Activity activity) {
		Logger.e("onCreate---------");
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		if(!mActivityStack.contains(activity)) {
			mActivityStack.add(activity);
		}
		Logger.e("ActivityManager", "堆栈数目----"+mActivityStack.size());
	}
	public void onResume(Activity activity) {
//		MobclickAgent.onResume(activity);
//        try {
//            FlurryAgent.onStartSession(this);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
	}
	public void onPause(Activity activity) {
//		MobclickAgent.onPause(activity);
//        try {
//            FlurryAgent.onEndSession(this);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
	}
	public void onResume(BaseFragment frag) {
		if (frag.mName != null){
//			MobclickAgent.onPageStart(frag.mName);
		}
	}
	public void onPause(BaseFragment frag) {
		if (frag.mName != null){
//			MobclickAgent.onPageEnd(frag.mName);
		}
	}
	/**
	 * 出栈
	 * @param activity
	 */
	public void onDestroy(Activity activity) {
		if (null != activity) {
			Logger.e("ActivityManager", "出栈activity名字-->" + activity.getClass().getSimpleName());
			mActivityStack.remove(activity);
		}
	}
	
	/**
	 * 通过Activity名称出栈
	 * @param activityName
	 */
	public void popActivity(String activityName) {
		if (!TextUtils.isEmpty(activityName)) {
			for (int i = 0; i < mActivityStack.size(); i++) {
				if(mActivityStack.get(i).getClass().getSimpleName().equals(activityName)){
					Logger.e("ActivityManager", "出栈activity名字-->" + activityName);
					mActivityStack.remove(mActivityStack.get(i));
				}
			}
		}
	}
	public void finishActivity(Class cact) {
			for (int i = 0; i < mActivityStack.size(); i++) {
				Activity act = mActivityStack.get(i); 
				if(act.getClass().equals(cact)){
					Logger.e("ActivityManager", "出栈activity名字-->" + cact.getSimpleName());
					mActivityStack.remove(act);
					act.finish();
					--i;
				}
			}
	}
	public Activity findActivity(Class cact) {
		for (int i = 0; i < mActivityStack.size(); i++) {
			Activity act = mActivityStack.get(i); 
			if(act.getClass().equals(cact)){
				return act;
			}
		}
		return null;
	}
	/**
	 * 获得当前栈顶Activity 
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = null;
		if(mActivityStack != null && !mActivityStack.empty())
			activity= mActivityStack.lastElement(); 
		return activity; 
	} 

	/**
	 * 退出栈中所有activity
	 */
	public void popAllActivities() {
		while (!mActivityStack.isEmpty()) { 
			Activity activity = currentActivity();
			if (null == activity) {
				break;
			}
			activity.finish();
			onDestroy(activity);
		}
	}
	
	/**
	 * 退出栈中所有activity
	 */
	public void popAllActivities(String activityName) {
		if(!mActivityStack.isEmpty()) {
			for (int i = mActivityStack.size()-1; i > 0; i--) {
				Activity curActivity = mActivityStack.lastElement().getClass().getSimpleName()
						.equals(activityName) ? mActivityStack.get(i-1) : mActivityStack.lastElement();
				Logger.e("popAllActivities---"
						+ (curActivity.getClass().getSimpleName().equals(activityName)) + ","
						+ mActivityStack.size());
				if (null == curActivity) {
					break;
				}
				if (!curActivity.getClass().getSimpleName().equals(activityName)) {
					curActivity.finish();
					onDestroy(curActivity);
				}
			}
		}
		Logger.e("popAllActivities after ---" + mActivityStack.size()
				+ "," + mActivityStack.lastElement() + "," + mActivityStack.firstElement());
	}
	
}
