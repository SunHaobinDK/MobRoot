package com.mob.root.ad.task;

import com.mob.root.AMApplication;

public class ADTaskController {

	private static ADTaskController sADTaskController;
	public ADTask currentTask;

	private ADTaskController() {
	}
	
	public static ADTaskController getInstance() {
		if (null == sADTaskController) {
			synchronized (ADTaskController.class) {
				if (null == sADTaskController) {
					sADTaskController = new ADTaskController();
				}
			}
		}
		return sADTaskController;
	}

	public void immediateADTask(TaskType adType, Object... args) {
		if (null == adType) {
			return;
		}
		ADTaskBuilder builder = new ADTaskBuilder();
		ADTask adTask = builder.setADType(adType, AMApplication.instance, args).build();
		currentTask = adTask;
		adTask.start();
	}
}
