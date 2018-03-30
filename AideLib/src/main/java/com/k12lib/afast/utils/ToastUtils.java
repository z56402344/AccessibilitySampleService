package com.k12lib.afast.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.k12lib.afast.view.ExToast;

import z.frame.ICommon;

public class ToastUtils implements ICommon {
	// 普通的Tost弹窗风格
	public static void showShortToast(Object txt){
		showShortToast(txt,-1);
	}

	private static void showShortToast(Object txt,int duration){
		if (txt instanceof CharSequence&& TextUtils.isEmpty((CharSequence)txt)) {
			return;
		}
		View view = View.inflate(app.ctx, app.Toast_Layout, null);
		TextView mTvContent = (TextView) view.findViewById(app.Toast_ID_Text);
		if (txt instanceof CharSequence) {
			mTvContent.setText((CharSequence)txt);
		} else if (txt instanceof Integer) {
			mTvContent.setText((Integer)txt);
		}

		if (app.Toast_Animtion == 0){
			Toast toast = new Toast(app.ctx);
			if (duration != -1){
				toast.setDuration(duration);
			}
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(view);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		}else{
			ExToast toast = new ExToast(app.ctx);
			if (duration != -1){
				toast.setDuration(duration);
			}
			toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.TOP, 0, 0);
			toast.setView(view);
			toast.setDuration(ExToast.LENGTH_SHORT);
			toast.setAnimations(app.Toast_Animtion);
			toast.show();
		}
	}

	public static void showLong(Context context, String message) {
		show(context, message, Toast.LENGTH_LONG);
	}

	public static void showShort(Context context, String message) {
		show(context, message, Toast.LENGTH_SHORT);
	}

	public static void showLong(Context context, int textId) {
		show(context, textId, Toast.LENGTH_LONG);
	}

	public static void showShort(Context context, int textId) {
		show(context, textId, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		// toast.setGravity(GRAVITY, 80, 80);
		toast.show();
	}

	public static void show(Context context, int textId, int duration) {
		Toast toast = Toast.makeText(context, textId, duration);
		// toast.setGravity(GRAVITY, 80, 80);
		toast.show();
	}
}
