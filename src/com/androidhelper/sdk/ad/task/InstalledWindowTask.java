package com.androidhelper.sdk.ad.task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
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

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.tools.CommonUtils;
import com.androidhelper.sdk.R;
import com.nostra13.universalimageloader.core.ImageLoader;

class InstalledWindowTask extends ADWindowTask {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mCloseIV;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private TextView mAppCategoryTV;
	private RatingBar mAppRatingBar;
	private TextView mAppFavorsTV;
	private Button mOpenAppBtn;
	private AD mAD;
	
	InstalledWindowTask(Context context, AD ad) {
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
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installed_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.installed_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.installed_window, null);
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
		
		mCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
		
		mOpenAppBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//启动应用
				if(null == mAD) {
					return;
				}
				Intent intent = new Intent(); 
			 	PackageManager packageManager = mContext.getPackageManager(); 
			 	intent = packageManager.getLaunchIntentForPackage(mAD.getPackageName()); 
			 	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
			 	mContext.startActivity(intent);

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
		mAppCategoryTV.setText(mAD.getCategory());
		mAppRatingBar.setRating(mAD.getRating());
		mAppFavorsTV.setText(String.format("(%s likes)", CommonUtils.formatNum(mAD.getFavors() + "")));
	}
}
