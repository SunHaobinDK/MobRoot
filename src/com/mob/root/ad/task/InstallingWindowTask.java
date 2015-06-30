package com.mob.root.ad.task;

import android.content.Context;
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
import android.widget.TextView;

import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.entity.AD;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

class InstallingWindowTask extends ADWindowTask implements Callback {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private Button mOKBtn;
	private TextView mCountDownTV;
	private Handler mHandler;
	private AD mAD;
	private int mCountDown = 5;
	
	InstallingWindowTask(Context context, AD ad) {
		super(context);
		mAD = ad;
	}

	@Override
	public void start() {
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
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installing_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installing_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.installing_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		initViews();
		initDatas();
		super.displayAD();
	}

	private void initViews() {
		mAppIconIV = (ImageView) mRootView.findViewById(R.id.appIcon);
		mAppTitleTV = (TextView) mRootView.findViewById(R.id.appTitle);
		mCountDownTV = (TextView) mRootView.findViewById(R.id.countDown);
		mOKBtn = (Button) mRootView.findViewById(R.id.ok);
		
		mOKBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
	}
	
	private void initDatas() {
		if(null == mAD) {
			return;
		}
		ImageLoader.getInstance().displayImage(mAD.getIcon_url(), mAppIconIV, AMApplication.instance.displayOption);
		mAppTitleTV.setText(mAD.getTitle());
		mCountDownTV.setText(mCountDown + "");
		
		mHandler = new Handler(this);
		if(-1 != mCountDown) {
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}
	
	@Override
	protected void closeWindow() {
		if(null != mHandler) {
			mHandler.removeMessages(0);
		}
		super.closeWindow();
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (--mCountDown < 0) {
			closeWindow();
			return true;
		}
		mCountDownTV.setText(mCountDown + "");
		mHandler.sendEmptyMessageDelayed(0, 1000);
		return true;
	}
}
