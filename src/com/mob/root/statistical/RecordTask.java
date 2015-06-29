package com.mob.root.statistical;

public abstract class RecordTask implements Runnable {

	public void record() {
		new Thread(this).start();
	};
}
