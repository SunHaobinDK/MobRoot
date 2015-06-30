package com.mob.root.ad.task;

import android.content.Context;
import com.mob.root.entity.AD;
import com.mob.root.entity.CollectionAD;

public class ADTaskBuilder {

	private ADTask mAdTask;

	public <T> ADTaskBuilder setADType(TaskType adType, Context context, T t) {
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
			mAdTask = new AdvancedDialogTask(context, (CollectionAD) t);
			return this;
		case WINDOW_INSTALLED:
			mAdTask = new InstalledWindowTask(context, (AD) t);
			return this;
		case WINDOW_GUIDE_INSTALL:
			mAdTask = new InstallGuideWindowTask(context, (AD) t);
			return this;
		case WINDOW_INSTALLED_APP:
			mAdTask = new InstalledAppWindowTask(context, (AD) t);
			return this;
		case WINDOW_INSTALLING:
			mAdTask = new InstallingWindowTask(context, (AD) t);
			return this;
		default:
			return null;
		}
	}

	public ADTask build() {
		return mAdTask;
	}
}
