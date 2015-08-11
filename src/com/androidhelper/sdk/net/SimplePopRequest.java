package com.androidhelper.sdk.net;

import java.util.List;

import org.apache.http.Header;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.net.parser.AdParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;

public class SimplePopRequest extends AMRequest<List<AD>> {
	
	public SimplePopRequest(IResponseListener<List<AD>> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
//			ConfigParser parser = new ConfigParser();
//			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_COLLECTION_REQUEST_URL);
//			if (CommonUtils.isEmptyString(url)) {
//				return;
//			}
//			doPost(url, null, true);
			doAdPost(8, AMConstants.placement_id_collection, this, null);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
//			CollectionParser parser = new CollectionParser();
//			CollectionAD collectionAD = parser.parseAD(resultDatas);
//			if(null != listener) {
//				listener.onResponse(collectionAD);
//			}
			AdParser parser = new AdParser();
			List<AD> ads = parser.parseAD(datas);
			if(null != ads && null != listener) {
				listener.onResponse(ads);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
