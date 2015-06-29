package com.mob.root.tools;

import android.util.Log;

public class AMLogger {

	private static String TAG = "ATLogger";

	public static void v(String tag, String msg) {
		if (AMConstants.IS_DEBUG) {
			Log.v(CommonUtils.isEmptyString(tag) ? TAG : tag, msg);
		}
	}
	
	public static void v(String tag, int msg) {
		if (AMConstants.IS_DEBUG) {
			Log.v(CommonUtils.isEmptyString(tag) ? TAG : tag, msg + "");
		}
	}

	public static void e(String tag, String msg) {
		if (AMConstants.IS_DEBUG) {
			Log.e(CommonUtils.isEmptyString(tag) ? TAG : tag, msg);
		}
	}
	
	public static void e(String tag, int msg) {
		if (AMConstants.IS_DEBUG) {
			Log.e(CommonUtils.isEmptyString(tag) ? TAG : tag, msg + "");
		}
	}
}
