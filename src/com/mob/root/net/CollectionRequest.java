package com.mob.root.net;

import org.apache.http.Header;
import com.mob.root.AMApplication;
import com.mob.root.entity.CollectionAD;
import com.mob.root.net.parser.CollectionParser;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class CollectionRequest<T> extends AMRequest<CollectionAD> {
	
	public CollectionRequest(IResponseListener<CollectionAD> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
			ConfigParser parser = new ConfigParser();
			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_COLLECTION_REQUEST_URL);
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
		CollectionParser parser = new CollectionParser();
		try {
			CollectionAD collectionAD = parser.parseAD(resultDatas);
			if(null != listener) {
				listener.onResponse(collectionAD);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
