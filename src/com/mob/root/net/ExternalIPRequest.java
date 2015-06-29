package com.mob.root.net;

import org.apache.http.Header;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class ExternalIPRequest extends AMRequest {

	@Override
	public void start() {
		doGet(AMConstants.EXTERNAL_IP_URI, null, this);
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		AMLogger.e(null, statusCode);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		try {
			if (CommonUtils.isEmptyString(datas)) {
				return;
			}
			
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
