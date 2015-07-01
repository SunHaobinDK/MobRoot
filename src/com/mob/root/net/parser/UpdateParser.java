package com.mob.root.net.parser;

import org.json.JSONObject;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.mob.root.AMApplication;
import com.mob.root.entity.Version;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;

public class UpdateParser {

	public Version parse(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		String versionStr = jsonObject.getString(AMConstants.NET_UPDATE_VERSION_CODE);
		if(CommonUtils.isEmptyString(versionStr)) {
			return null;
		}
		int versionCode = Integer.parseInt(versionStr);
		PackageManager packageManager = AMApplication.instance.getPackageManager();
		PackageInfo info = packageManager.getPackageInfo(AMApplication.instance.getPackageName(), 0);
		int code = info.versionCode;
		if(code >= versionCode) {
			return null;
		}
		String url = jsonObject.getString(AMConstants.NET_UPDATE_URL);
		Version version = new Version();
		version.setUrl(url);
		version.setVersionCode(versionCode);
		
		return version;
	}
}
