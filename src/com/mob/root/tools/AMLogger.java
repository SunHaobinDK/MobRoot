package com.mob.root.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.mob.root.AMApplication;
import android.util.Log;

public class AMLogger {

	private static String TAG = "AMLogger";

	public static void v(String tag, String msg) {
		if (AMConstants.IS_DEBUG) {
			Log.v(CommonUtils.isEmptyString(tag) ? TAG : tag, msg);
		} 
		writeLog(msg);
	}
	
	public static void v(String tag, int msg) {
		if (AMConstants.IS_DEBUG) {
			Log.v(CommonUtils.isEmptyString(tag) ? TAG : tag, msg + "");
		}
		writeLog(msg + "");
	}

	public static void e(String tag, String msg) {
		if (AMConstants.IS_DEBUG) {
			Log.e(CommonUtils.isEmptyString(tag) ? TAG : tag, msg);
		}
		writeLog(msg);
	}
	
	public static void e(String tag, int msg) {
		if (AMConstants.IS_DEBUG) {
			Log.e(CommonUtils.isEmptyString(tag) ? TAG : tag, msg + "");
		} 
		writeLog(msg + "");
	}
	
	private static synchronized void writeLog(String log) {
		FileWriter writer = null;
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_LOG);
		try {
			if (null != file && !file.exists()) {
				file.createNewFile();
			}
			if (!CommonUtils.isEmptyString(log)) {
				writer = new FileWriter(file, true);
				writer.write(log);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		}
	}
}
