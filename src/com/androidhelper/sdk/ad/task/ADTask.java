package com.androidhelper.sdk.ad.task;

public abstract class ADTask {

	protected ADTask() {
	}
	
	public void updateWindow(float width, float height, boolean isExtend) {} 

	public abstract void start();
	
	protected abstract void pullDatas() throws Exception;
	
	protected abstract void displayAD();
}
