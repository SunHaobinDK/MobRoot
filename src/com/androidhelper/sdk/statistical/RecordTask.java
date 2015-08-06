package com.androidhelper.sdk.statistical;

public abstract class RecordTask implements Runnable {

	public void record() {
		new Thread(this).start();
	};
}
