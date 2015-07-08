package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mob.root.statistical.AppRemovedRecord;

public class RemoveAppReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(!Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			return;
		}
		String packageName = intent.getDataString(); 
		
		AppRemovedRecord record = new AppRemovedRecord(packageName);
		record.record();
	}
}
