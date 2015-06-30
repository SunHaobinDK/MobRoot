package com.mob.root.ad.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.entity.AD;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

class InstalledAppWindowTask extends ADWindowTask implements Callback {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mCloseIV;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private TextView mAppCategoryTV;
	private RatingBar mAppRatingBar;
	private TextView mAppFavorsTV;
	private Button mOpenAppBtn;
	private TextView mCountDownTV;
	private Handler mHandler;
	
	private AD mAD;
	
	InstalledAppWindowTask(Context context, AD ad) {
		super(context);
		mAD = ad;
	}

	@Override
	public void start() {
		// 从服务器拉取最新的广告数据
		pullDatas();
		// 显示广告
		displayAD();
	}

	@Override
	protected void pullDatas() {
	}

	@Override
	protected void displayAD() {
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		LayoutParams lpWindow = new LayoutParams();
		lpWindow.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lpWindow.flags = LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		lpWindow.gravity = Gravity.CENTER;
		lpWindow.x = 0;  
		lpWindow.y = 0; 
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installed_app_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installed_app_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.installed_app_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		initViews();
		initDatas();
		super.displayAD();
	}

	private void initViews() {
		mCloseIV = (ImageView) mRootView.findViewById(R.id.close);
		mAppIconIV = (ImageView) mRootView.findViewById(R.id.appIcon);
		mAppTitleTV = (TextView) mRootView.findViewById(R.id.appTitle);
		mAppCategoryTV = (TextView) mRootView.findViewById(R.id.appCategory);
		mAppRatingBar = (RatingBar) mRootView.findViewById(R.id.appRating);
		mAppFavorsTV = (TextView) mRootView.findViewById(R.id.appFavors);
		mOpenAppBtn = (Button) mRootView.findViewById(R.id.openApp);
		mCountDownTV = (TextView) mRootView.findViewById(R.id.countDown);
		mCountDownTV.setText("open in\n3 sec");
		
		mCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mHandler) {
					mHandler.removeMessages(0);
				}
				closeWindow();
			}
		});
		
		mOpenAppBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//启动应用
				openApp();
			}
		});
	}
	
	private void initDatas() {
		if(null == mAD) {
			return;
		}
		ImageLoader.getInstance().displayImage(mAD.getIcon_url(), mAppIconIV, AMApplication.instance.displayOption);
		mAppTitleTV.setText(mAD.getTitle());
		mAppCategoryTV.setText(mAD.getCategory());
		mAppRatingBar.setRating(mAD.getRating());
		mAppFavorsTV.setText(String.format("(%s likes)", CommonUtils.formatNum(mAD.getFavors() + "")));
		
		mHandler = new Handler(this);
		Message message = mHandler.obtainMessage();
		message.what = 0;
		message.arg1 = 3;
		mHandler.sendMessageDelayed(message, 1000);
	}

	@Override
	public boolean handleMessage(Message msg) {
		int time = msg.arg1;
		if(0 == time) {
			// 打开app
			openApp();
			return true;
		}
		Message message = mHandler.obtainMessage();
		message.what = 0;
		message.arg1 = --time;
		mCountDownTV.setText(String.format("open in\n%s sec", time));
		mHandler.sendMessageDelayed(message, 1000);
		return true;
	}
	
	private void openApp() {
		if(null == mAD) {
			return;
		}
		if (null != mHandler) {
			mHandler.removeMessages(0);
		}
		try {
			Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mAD.getPackageName());
			mContext.startActivity(intent);
		} catch (Exception e) {
		}
		closeWindow();
	}
	
	@Override
	protected void closeWindow() {
		if(null != mHandler) {
			mHandler.removeMessages(0);
		}
		super.closeWindow();
	}
}
