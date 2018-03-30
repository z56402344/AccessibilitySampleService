package com.k12lib.afast.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.k12lib.afast.log.Logger;
/**
 * GPS工具类
 */
public class GPSUtil {
	
	private static final String TAG = "GPSUtil";

	/**
	 * 查看GPS是否开启
	 * @param context
	 * @return
	 */
	public static boolean isGPSOpen(Context context){
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 跳转到GPS设置页面
	 * @param context
	 */
	public static void gotoGPSSetting(Context context){
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try{
			context.startActivity(intent);
		}catch(ActivityNotFoundException e){
			intent.setAction(Settings.ACTION_SETTINGS);
			context.startActivity(intent);
		}
	}
	
	/**
	 * GPS获取回调
	 */
	public interface OnGetLocationComplete {
		/**
		 * onSuccess Retrieve location information of device.
		 */
		public void onSuccess(Location location);
		/**
		 * onError Callback interface that handles errors.
		 */
		public void onError(String error);
	}
	
	private static OnGetLocationComplete mCompleteListener;
	private static Context mContext;
	
	/**
	 * 根据GPS获得用户所在位置的经纬度
	 */
	public static void getLocation(Context context,OnGetLocationComplete onComplete){
		mContext = context;
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		mCompleteListener = onComplete;
		if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			mCompleteListener.onError("没有网络");
			return;
		}else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);   
			if(null != locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)){
				mCompleteListener.onSuccess(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			}
		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, locationListener);   
			if(null != locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)){
				mCompleteListener.onSuccess(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
			}
		}
	}
		
	
	private static LocationListener locationListener = new LocationListener() {

		// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		// Provider被enable时触发此函数，比如GPS被打开
		@Override
		public void onProviderEnabled(String provider) {

		}

		// Provider被disable时触发此函数，比如GPS被关闭 
		@Override
		public void onProviderDisabled(String provider) {

		}

		//当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发 
		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "onLoactionChagned getLocation...");
			if (location != null) {   
				if(mCompleteListener != null){
					mCompleteListener.onSuccess(location);
					LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
					locationManager.removeUpdates(locationListener);
				}
				Logger.i(TAG, "Location changed : Lat: "  
						+ location.getLatitude() + " Lng: "  
						+ location.getLongitude());   
			}
		}

	};
	
}
