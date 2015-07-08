package com.mob.root.ad.task;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mob.root.R;
import com.mob.root.entity.AD;
import com.mob.root.net.IResponseListener;
import com.mob.root.net.SimplePopRequest;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

class SimplePopWindowTask extends ADWindowTask implements IResponseListener<List<AD>> {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private List<AD> mAds;
	
	SimplePopWindowTask(Context context) {
		super(context);
	}

	@Override
	public void start() {
		// 从服务器拉取最新的广告数据
		try {
			pullDatas();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	@Override
	protected void pullDatas() throws Exception {
		SimplePopRequest request = new SimplePopRequest(this);
		request.start();
	}

	@Override
	protected void displayAD() {
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		LayoutParams lpWindow = new LayoutParams();
		lpWindow.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lpWindow.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		lpWindow.gravity = Gravity.RIGHT | Gravity.TOP;
		lpWindow.x = 0;  
		lpWindow.y = 0; 
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.simple_float_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.simple_float_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.simple_banner_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		
		initViews();
		initDatas();
		
		SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
	}

	private void initViews() {
		mRootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AdvancedDialogTask windowTask = new AdvancedDialogTask(mContext, mAds);
				windowTask.start();
				closeWindow();
			}
		});
	}
	
	private void initDatas() {
	}

	@Override
	public void onResponse(List<AD> ads) {
		mAds = ads;
		displayAD();
	}
}
