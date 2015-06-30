package com.mob.root.net;

import org.apache.http.Header;
import com.mob.root.AMApplication;
import com.mob.root.entity.AD;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.net.parser.SingleADParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class SimpleNotificationRequest extends AMRequest<AD> {
	
	public SimpleNotificationRequest(IResponseListener<AD> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
			ConfigParser parser = new ConfigParser();
			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_SINGLE_REQUEST_URL);
			if (CommonUtils.isEmptyString(url)) {
				return;
			}
			doPost(url, null, true);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
			SingleADParser parser = new SingleADParser();
			AD ad = parser.parseAD(resultDatas);
			if(null != listener) {
				listener.onResponse(ad);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
