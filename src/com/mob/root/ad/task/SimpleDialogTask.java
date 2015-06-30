package com.mob.root.ad.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.adapter.SimpleWindowAppPhotosAdapter;
import com.mob.root.entity.AD;
import com.mob.root.net.IResponseListener;
import com.mob.root.net.SimpleDialogRequest;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;
import com.mob.root.view.ADHorizontalScrollview;
import com.nostra13.universalimageloader.core.ImageLoader;

class SimpleDialogTask extends ADWindowTask implements IResponseListener<AD> {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mAppCoverIV;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private TextView mAppCategoryTV;
	private RatingBar mAppRatingRB;
	private TextView mAppFavorTV;
	private TextView mAppInstallTV;
	private ADHorizontalScrollview mAppPhotosView;
	private ImageView mCloseIV;
	private AD mAD;
	
	private SimpleWindowAppPhotosAdapter mAdapter;
	
	SimpleDialogTask(Context context) {
		super(context);
	}

	@Override
	public void start() {
		// 从服务器拉取最新的广告数据
		try {
			pullDatas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void pullDatas() throws Exception {
		SimpleDialogRequest request = new SimpleDialogRequest(this);
		request.start();
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
		Point point = new Point();
		mWindowManager.getDefaultDisplay().getRealSize(point);
		lpWindow.x = 0;  
		lpWindow.y = 0; 
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.simple_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.simple_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.simple_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		initViews();
		initDatas();
		super.displayAD();
		
		SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
	}

	private void initViews() {
		mAppCoverIV = (ImageView) mRootView.findViewById(R.id.appCover);
		mCloseIV = (ImageView) mRootView.findViewById(R.id.close);
		mAppIconIV = (ImageView) mRootView.findViewById(R.id.appIcon);
		mAppTitleTV = (TextView) mRootView.findViewById(R.id.appTitle);
		mAppCategoryTV = (TextView) mRootView.findViewById(R.id.appCategory);
		mAppRatingRB = (RatingBar) mRootView.findViewById(R.id.appRating);
		mAppFavorTV = (TextView) mRootView.findViewById(R.id.appFavor);
		mAppInstallTV = (TextView) mRootView.findViewById(R.id.install);
		mAppPhotosView = (ADHorizontalScrollview) mRootView.findViewById(R.id.appPhotosView);
		
		mCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
		
		mAppInstallTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callInstallGuide(mContext, mAD);
			}
		});
	}
	
	private void initDatas() {
		if(null == mAD) {
			return;
		}
		ImageLoader.getInstance().displayImage(mAD.getIcon_url(), mAppIconIV, AMApplication.instance.displayOption);
		ImageLoader.getInstance().displayImage(mAD.getCover_url(), mAppCoverIV, AMApplication.instance.displayOption);
		mAppTitleTV.setText(mAD.getTitle());
		mAppCategoryTV.setText(mAD.getCategory());
		mAppRatingRB.setRating(mAD.getRating());
		mAppFavorTV.setText(String.format("(%s likes)", CommonUtils.formatNum(mAD.getFavors() + "")));
		
		mAdapter = new SimpleWindowAppPhotosAdapter(mContext, mAD.getPics());
		mAppPhotosView.setAdapter(mContext, mAdapter);
	}

	@Override
	public void onResponse(AD ad) {
		mAD = ad;
		displayAD();
	}
}
