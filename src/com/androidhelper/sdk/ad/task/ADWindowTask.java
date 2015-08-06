package com.androidhelper.sdk.ad.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;

import com.androidhelper.sdk.entity.AD;

abstract class ADWindowTask extends ADTask {
	
	protected Context mContext;
	protected WindowManager mWindowManager;
	protected View mRootView;
	private KeyReceiver mKeyReceiver;
	protected boolean selfBack;

	protected ADWindowTask(Context context) {
		mContext = context;
	}
	
	@Override
	protected void displayAD() {
		registHomeKeyListener();
		mRootView.setFocusable(true);
		mRootView.setFocusableInTouchMode(true);
		mRootView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && selfBack) {
					if(event.getAction() == KeyEvent.ACTION_UP) {
						keyBackPressed();
					}
					return true;
				} 
				if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU){
					closeWindow();
					return true;
				}
				return false;
			}
		});
	}
	
	protected void keyBackPressed() { }
	
	private void registHomeKeyListener() {
		mKeyReceiver = new KeyReceiver();
		IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		if(null != mContext) {
			mContext.registerReceiver(mKeyReceiver, homeFilter);
		}
	}
	
	private void unRegistKeyListener() {
		if(null != mKeyReceiver && null != mContext) {
			mContext.unregisterReceiver(mKeyReceiver);
		}
	}

	protected void callInstallGuide(Context context, AD ad) {
		closeWindow();
		ADTaskBuilder builder = new ADTaskBuilder();
		ADTask adTask = builder.setADType(TaskType.WINDOW_GUIDE_INSTALL, mContext, ad).build();
		adTask.start();
	}

	protected void closeWindow() {
		unRegistKeyListener();
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		mWindowManager.removeView(mRootView);
	}
	
	private class KeyReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            if (null != action && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null && (reason.equals("homekey") || reason.equals("recentapps"))) {
                	closeWindow();
                }
            }
		}
	}
}
