package com.androidhelper.sdk.net;

import java.util.List;

import org.apache.http.Header;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.net.parser.AdParser;
import com.androidhelper.sdk.tools.AMLogger;

public class AdvancedPopRequest extends AMRequest<AD> {

	public AdvancedPopRequest(IResponseListener<AD> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
//			ConfigParser parser = new ConfigParser();
//			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_SINGLE_REQUEST_URL);
//			if (CommonUtils.isEmptyString(url)) {
//				return;
//			}
//			doPost(url, null, true);
			doAdPost(1, this, null);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
//			SingleADParser parser = new SingleADParser();
//			AD ad = parser.parseAD(resultDatas);
//			if(null != listener) {
//				listener.onResponse(ad);
//			}
			AdParser parser = new AdParser();
			List<AD> ads = parser.parseAD(datas);
			if(null != ads && null != listener) {
				listener.onResponse(ads.get(0));
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
