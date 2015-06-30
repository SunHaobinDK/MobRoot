package com.mob.root.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mob.root.net.UploadDatasRequest;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;

public class STCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!CommonUtils.isNetworkAvailable(context)) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long currentTimeMillis = System.currentTimeMillis();
		Long dayDely = sp.getLong(AMConstants.SP_NEXT_UPLOAD_STAMP, 0);
		if (currentTimeMillis >= dayDely) { // 当前时间已经超过了需要每天上传统计信息的时间点，则开始执行任务
			UploadDatasRequest request = new UploadDatasRequest(null);
			request.start();
		}
	}
}
