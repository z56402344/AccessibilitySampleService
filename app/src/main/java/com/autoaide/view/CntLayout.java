package com.autoaide.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.io.StringWriter;

//import com.dasheng.talk.core.UmengErr;

// 为了捕获界面异常 判断在那个界面
public class CntLayout extends FrameLayout {
	public CntLayout(Context context) {
		super(context);
	}
	public CntLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CntLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		try {
			return super.dispatchTouchEvent(ev);
		} catch (Throwable e) {
			uploadErr("wys_dispatchTouchEvent",e);
			return true;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		try {
			super.draw(canvas);
		} catch (Throwable e) {
			uploadErr("wys_draw",e);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Throwable e) {
			uploadErr("wys_dispatchDraw",e);
		}
	}
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		try {
			return super.drawChild(canvas, child, drawingTime);
		} catch (Throwable e) {
			uploadErr("wys_drawChild",e);
			return true;
		}
	}
	// 找出异常的界面 并上报异常
	private void uploadErr(String evtName, Throwable e) {
		android.util.Log.e("uploadErr",evtName+e.toString());
		FragmentActivity act = (FragmentActivity) getContext();
		FragmentManager fm = act.getSupportFragmentManager();
		StringWriter info = new StringWriter(4096);
		if (fm!=null&&fm.getBackStackEntryCount()>0) {
			FragmentManager.BackStackEntry bse = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1);
			info.write(bse.getName());
		} else {
			info.write(act.getClass().getName());
		}
		info.write("\r\n");
//		UmengErr.buildStack(info,e);
//		UmengErr.reportSimple(evtName,info.toString());
	}
}
