package com.mob.root.net;

import java.util.List;
import org.apache.http.Header;
import com.mob.root.entity.AD;
import com.mob.root.net.parser.AdParser;
import com.mob.root.tools.AMLogger;

public class CollectionRequest<T> extends AMRequest<List<AD>> {
	
	public CollectionRequest(IResponseListener<List<AD>> listener) {
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
			doAdPost(1, this, null);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
//		CollectionParser parser = new CollectionParser();
		try {
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
