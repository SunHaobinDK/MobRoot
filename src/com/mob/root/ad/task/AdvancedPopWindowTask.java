package com.mob.root.ad.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.entity.AD;
import com.mob.root.net.AdDisplayUploadRequest;
import com.mob.root.net.AdvancedPopRequest;
import com.mob.root.net.IResponseListener;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

class AdvancedPopWindowTask extends ADWindowTask implements IResponseListener<AD> {

	private RelativeLayout mAnimContainer;
	private LayoutInflater mInflater;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private RatingBar mRatingBar;
	private TextView mAppFavorsTV;
	private TextView mAppIntroTV;
	private Button mInstallBtn;
	private TextView mUpCloseTV;
	private ImageView mUpCloseIV;
	private View mUpCloseLayout;
	private View mBannerLayout;
	private AD mAD;
	
	/** 用于标志图标是否被点击，如果10s未点击，则收起所有view，如果点击，则展现所有banner内容 */
	private boolean isIconClick;
	private LayoutParams lpWindow;
	
	AdvancedPopWindowTask(Context context) {
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
		AdvancedPopRequest request = new AdvancedPopRequest(this);
		request.start();
	}

	@Override
	protected void displayAD() {
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		lpWindow = new LayoutParams();
		lpWindow.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lpWindow.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		lpWindow.gravity = Gravity.TOP;
		Point point = new Point();
		mWindowManager.getDefaultDisplay().getRealSize(point);
		lpWindow.x = 0;  
		lpWindow.y = 0; 
		
		lpWindow.width = point.x;
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.advanced_float_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.advanced_banner_window, null);
		}
		initViews();
		initDatas();
	}

	private void checkClickTask() {
		Handler mHandler = new Handler(new android.os.Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if(!isIconClick) {
					if (null == mAnimContainer) {
						return true;
					}
					if (null == mWindowManager) {
						mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
					}
					mWindowManager.removeView(mAnimContainer);
				}
				return true;
			}
		});
		mHandler.sendEmptyMessageDelayed(0, 1000 * 10);
	}

	private void initViews() {
		mAppIconIV = (ImageView) mRootView.findViewById(R.id.bannerAppIcon);
		mAppTitleTV = (TextView) mRootView.findViewById(R.id.appTitle);
		mRatingBar = (RatingBar) mRootView.findViewById(R.id.appRating);
		mAppFavorsTV = (TextView) mRootView.findViewById(R.id.appFavors);
		mAppIntroTV = (TextView) mRootView.findViewById(R.id.appIntro);
		mInstallBtn = (Button) mRootView.findViewById(R.id.install);
		mUpCloseTV = (TextView) mRootView.findViewById(R.id.upClose);
		mUpCloseIV = (ImageView) mRootView.findViewById(R.id.upCloseIcon);
		mUpCloseLayout = mRootView.findViewById(R.id.upCloseLayout);
		mBannerLayout = mRootView.findViewById(R.id.bannerContent);
		
		mAppIconIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mBannerLayout.getVisibility() != View.VISIBLE) {
					isIconClick = true;
					showBanner();
				}
			}
		});
		
		mInstallBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeBannerAnim(true);
			}
		});
		
		mBannerLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeBannerAnim(true);
			}
		});
		
		mUpCloseLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeBannerAnim(false);
			}
		});
		
		mUpCloseTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeBannerAnim(false);
			}
		});
		
		mUpCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeBannerAnim(false);
			}
		});
	}
	
	private void initDatas() {
		if (null == mAD) {
			return;
		}
		ImageLoader.getInstance().loadImage(mAD.getIcon_url(), AMApplication.instance.displayOption, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				mAppIconIV.setImageBitmap(loadedImage);
				mAppTitleTV.setText(mAD.getTitle());
				mRatingBar.setRating(mAD.getRating());
				mAppFavorsTV.setText(String.format("(%s likes)", CommonUtils.formatNum(mAD.getFavors() + "")));
				mAppIntroTV.setText(mAD.getDesc());
				
				mAnimContainer = new RelativeLayout(mContext);
				mWindowManager.addView(mAnimContainer, lpWindow);  
				
				android.widget.RelativeLayout.LayoutParams lpAnimContainer = new android.widget.RelativeLayout.LayoutParams (-1, -1);
				mAnimContainer.addView(mRootView, lpAnimContainer);
				
				checkClickTask();
				SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
				sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
			}
		});
	}
	
	private void showBanner(){
		mBannerLayout.setVisibility(View.VISIBLE);
		mUpCloseLayout.setVisibility(View.VISIBLE);
		
		AdDisplayUploadRequest request = new AdDisplayUploadRequest(null);
		request.start(mAD.getPackageName());
	}

	@Override
	protected void closeWindow() {
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		mWindowManager.removeView(mAnimContainer);
	}
	
	//点击banner淡出动画
	private void closeBannerAnim(final boolean isCallInstall) {
		AnimationSet animation = new AnimationSet(true);
		animation.setDuration(500);
		Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -mAnimContainer.getHeight());
		Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		animation.addAnimation(translateAnimation);
		animation.addAnimation(alphaAnimation);
		
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(isCallInstall) {
					callInstallGuide(mContext, mAD);
				} else {
					closeWindow();
				}
			}
		});
		mRootView.startAnimation(animation);
	}

	@Override
	public void onResponse(AD ad) {
		mAD = ad;
		displayAD();
	}
}
