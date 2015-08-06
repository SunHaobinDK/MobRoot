package com.androidhelper.sdk.net;

import java.util.List;

import org.apache.http.Header;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.net.parser.AdParser;
import com.androidhelper.sdk.net.parser.SingleADParser;
import com.androidhelper.sdk.tools.AMLogger;

public class AdvancedNotificationRequest extends AMRequest<AD> {

	public AdvancedNotificationRequest(IResponseListener<AD> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
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
