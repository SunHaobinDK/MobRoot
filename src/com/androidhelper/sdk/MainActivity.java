package com.androidhelper.sdk;

import com.androidhelper.sdk.ad.task.ADTaskBuilder;
import com.androidhelper.sdk.ad.task.TaskType;
import com.androidhelper.sdk.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button mSimpleNotify, mAdvanceNotify, mSimpleDialog, mSimpleBannerBanner, mBanner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mSimpleNotify = (Button) findViewById(R.id.simpleNotify);
		mAdvanceNotify = (Button) findViewById(R.id.advanceNotify);
		mSimpleDialog = (Button) findViewById(R.id.simpleDialog);
		mSimpleBannerBanner = (Button) findViewById(R.id.simpleBanner);
		mBanner = (Button) findViewById(R.id.banner);
		mSimpleNotify.setOnClickListener(this);
		mAdvanceNotify.setOnClickListener(this);
		mSimpleDialog.setOnClickListener(this);
		mSimpleBannerBanner.setOnClickListener(this);
		mBanner.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		ADTaskBuilder builder = new ADTaskBuilder();
		switch (v.getId()) {
		case R.id.simpleNotify:
			builder.setADType(TaskType.NOTIFICATION_SIMPLE, this, null);
			break;
		case R.id.advanceNotify:
			builder.setADType(TaskType.NOTIFICATION_ADVANCED, this, null);
			break;
		case R.id.simpleDialog:
			builder.setADType(TaskType.WINDOW_SOLO, this, null);
			break;
		case R.id.simpleBanner:
			builder.setADType(TaskType.POP_SIMPLE, this, null);
			break;
		case R.id.banner:
			builder.setADType(TaskType.POP_ADVANCED, this, null);
			break;
		}
		builder.build().start();
	}
}
