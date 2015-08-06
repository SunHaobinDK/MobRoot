package com.androidhelper.sdk.statistical;

import com.androidhelper.sdk.AMApplication;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class AMLocation implements LocationListener {
	
	private static AMLocation sLocationUtil;
	private LocationManager mLocationManager;
	private Criteria mCriteria;
	private AMLocationListener mListener;
	private Handler handler;
	private long mConnectedStamp;
	
	private AMLocation(long stamp) {
		this.handler = new Handler(Looper.getMainLooper());
		mConnectedStamp = stamp;
	}
	
	public static AMLocation getInstance(long stamp) {
		if(null == sLocationUtil) {
			synchronized (AMLocation.class) {
				if(null == sLocationUtil) {
					sLocationUtil = new AMLocation(stamp);
				}
			}
		}
		return sLocationUtil;
	}

	public void getCurrentLocation(AMLocationListener listener) {
		mListener = listener;
		if(null == mLocationManager) {
			mLocationManager = (LocationManager) AMApplication.instance.getSystemService(Context.LOCATION_SERVICE);
		}
		if(null == mCriteria) {
			mCriteria = new Criteria();
			mCriteria.setAccuracy(Criteria.ACCURACY_MEDIUM);// 精度
			mCriteria.setAltitudeRequired(false);// 无海拔要求
			mCriteria.setBearingRequired(false);// 无方位要求
			mCriteria.setCostAllowed(true);// 允许产生资费
			mCriteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
		}
		 // 获取最佳服务对象
        String provider = mLocationManager.getBestProvider(mCriteria,true);
        handler.post(new RequestLocation(provider));
	}
	
	public Location getLastLocation() {
		Location location = null;
		if(null == mLocationManager) {
			mLocationManager = (LocationManager) AMApplication.instance.getSystemService(Context.LOCATION_SERVICE);
		}
		if(null == mCriteria) {
			mCriteria = new Criteria();
			mCriteria.setAccuracy(Criteria.ACCURACY_MEDIUM);// 精度
			mCriteria.setAltitudeRequired(false);// 无海拔要求
			mCriteria.setBearingRequired(false);// 无方位要求
			mCriteria.setCostAllowed(true);// 允许产生资费
			mCriteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
		}
		 // 获取最佳服务对象
        String provider = mLocationManager.getBestProvider(mCriteria,true);
        location = mLocationManager.getLastKnownLocation(provider);
		return location;
	}
	
	private void removeUpdates() {
		mListener = null;
		if(null == mLocationManager) {
			mLocationManager = (LocationManager) AMApplication.instance.getSystemService(Context.LOCATION_SERVICE);
		}
		mLocationManager.removeUpdates(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(null != mListener) {
			mListener.onLocationChanged(location, mConnectedStamp);
		}
		removeUpdates();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		removeUpdates();
	}
	
	private class RequestLocation implements Runnable {
		
		private String mProvider;

		public RequestLocation(String provider) {
			mProvider = provider;
		}

		@Override
		public void run() {
			if(null == mLocationManager) {
				mLocationManager = (LocationManager) AMApplication.instance.getSystemService(Context.LOCATION_SERVICE);
			}
			mLocationManager.requestLocationUpdates(mProvider, 0, 0, AMLocation.this);
		}
	}
	
	public interface AMLocationListener {
		public void onLocationChanged(Location location, long stamp);
	}
}
