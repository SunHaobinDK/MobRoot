package com.mob.root;

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
		switch (v.getId()) {
		case R.id.simpleNotify:
			break;
		case R.id.advanceNotify:
			break;
		case R.id.simpleDialog:
			break;
		case R.id.simpleBanner:
			break;
		case R.id.banner:
			break;
		}
	}
}
