package com.mob.root.net;

import org.json.JSONObject;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;

public class AdDisplayUploadRequest extends AMRequest<Object> {

	public AdDisplayUploadRequest(IResponseListener<Object> listener) {
		super(listener);
	}

	@Override
	public void start(Object... args) {
		this.args = args;
		try {
			if(null == args) {
				return;
			}
			JSONObject jsonObject = new JSONObject();
			if(args.length >= 1 && null != args[0]) {
				jsonObject.put(AMConstants.NET_DP_PM, args[0].toString());
			}
			if(args.length >= 2 && null != args[1]) {
				jsonObject.put(AMConstants.NET_DT, args[1].toString());
			}
			if(args.length >= 3 && null != args[2]) {
				jsonObject.put(AMConstants.NET_DP_INDEX, args[2].toString());
			}
			doPost(AMConstants.AD_DISPLAY_UPLOAD_URI, jsonObject, true);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
