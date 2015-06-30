package com.mob.root.ad.task;

import android.content.Context;

import com.mob.root.entity.AD;


public class ADTaskBuilder {

	private ADTask mAdTask;

	public ADTaskBuilder setADType(TaskType adType, Context context, Object... args) {
		switch (adType) {
		case NOTIFICATION_SIMPLE:
			mAdTask = new SimpleNotificationTask(context);
			return this;
		case NOTIFICATION_ADVANCED:
			mAdTask = new AdvancedNotificationTask(context);
			return this;
		case POP_SIMPLE:
			mAdTask = new SimplePopWindowTask(context);
			return this;
		case POP_ADVANCED:
			mAdTask = new AdvancedPopWindowTask(context);
			return this;
		case WINDOW_SOLO:
			mAdTask = new SimpleDialogTask(context);
			return this;
		case WINDOW_COLLECTION:
			mAdTask = new AdvancedDialogTask(context, null);
			return this;
		case WINDOW_INSTALLED:
			mAdTask = new InstalledWindowTask(context, (AD) args[0]);
			return this;
		case WINDOW_GUIDE_INSTALL:
			mAdTask = new InstallGuideWindowTask(context, (AD) args[0]);
			return this;
		case WINDOW_INSTALLED_APP:
			mAdTask = new InstalledAppWindowTask(context, (AD) args[0]);
			return this;
		case WINDOW_INSTALLING:
			mAdTask = new InstallingWindowTask(context, (AD) args[0]);
			return this;
		default:
			return null;
		}
	}

	public ADTask build() {
		return mAdTask;
	}
}
