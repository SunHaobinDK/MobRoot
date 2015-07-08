package com.mob.root.net.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.mob.root.ADType;
import com.mob.root.EventType;
import com.mob.root.entity.ADConfig;
import com.mob.root.entity.ADRule;
import com.mob.root.entity.Flavor;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.CommonUtils;

public class ConfigParser {

	public ADConfig parse(String datas) throws Exception {
		ADConfig config = new ADConfig();
		JSONObject rootObject = new JSONObject(datas);
		int cache = rootObject.getInt(AMConstants.NET_CACHE_CONTROL);
		String updateUrl = rootObject.getString(AMConstants.NET_VERSION_CONTROL_URL);
		String downloadServer = rootObject.getString(AMConstants.NET_GP_SERVER);
		String port = rootObject.getString(AMConstants.NET_GP_SERVER_PORT);
		String requestADUrl = rootObject.getString(AMConstants.NET_AD_REQUEST_URL);
		JSONArray jsonArray = rootObject.getJSONArray(AMConstants.NET_AD_DISPLAY_RULES);
		int length = jsonArray.length();
		List<ADRule> rules = new ArrayList<ADRule>();
		for (int i = 0; i < length; i++) {
			ADRule rule = new ADRule();
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			int adType = jsonObject.getInt(AMConstants.NET_AD_TYPE);
			int eventType = jsonObject.getInt(AMConstants.NET_EVENT_TYPE);
			double probability = jsonObject.getDouble(AMConstants.NET_PROBABILITY);
			int freqSameCat = jsonObject.getInt(AMConstants.NET_FREQ_SAME_CAT);
			int freqGlobal = jsonObject.getInt(AMConstants.NET_FREQ_GLOBAL);
			int level = jsonObject.getInt(AMConstants.NET_LEVEL);
			
			rule.setAdtype(ADType.values()[adType]);
			rule.setEventType(EventType.values()[eventType]);
			rule.setProbability((float) probability);
			rule.setFreqSameCat(freqSameCat);
			rule.setFreqGlobal(freqGlobal);
			rule.setLevel(level);
			
			rules.add(rule);
		}
		String dataUploadUrl = rootObject.getString(AMConstants.NET_DATA_UPLOAD_URL);
		String dataUploadInterval = rootObject.getString(AMConstants.NET_DATA_UPLOAD_INTERVAL);
		String failoverServerUrl = rootObject.getString(AMConstants.NET_FAILOVER_SERVER_URL);
		String failoverTryCount = rootObject.getString(AMConstants.NET_FAILOVER_TRY_COUNT);
		int showFlavors = rootObject.getInt(AMConstants.NET_SHOW_FLAVORS);
		
		JSONArray flavorsArray = rootObject.getJSONArray(AMConstants.NET_FLAVORS);
		List<Flavor> flavors = null;
		if(null != flavorsArray) {
			length = flavorsArray.length();
			flavors = new ArrayList<Flavor>();
			for (int i = 0; i < length; i++) {
				Flavor flavor = new Flavor();
				JSONObject jsonObject = flavorsArray.getJSONObject(i);
				String id = jsonObject.getString(AMConstants.NET_FLAVOR_ID);
				String color = jsonObject.getString(AMConstants.NET_FLAVOR_COLOR);
				String name = jsonObject.getString(AMConstants.NET_FLAVOR_NAME);
				
				flavor.setId(id);
				flavor.setName(name);
				flavor.setColor(color);
				
				flavors.add(flavor);
			}
		}
		
		int dataSwitch = rootObject.getInt(AMConstants.NET_DATA_SWITCH);
		int appsSwitch = rootObject.getInt(AMConstants.NET_DATA_APPS_SWITCH);
		int bhSwitch = rootObject.getInt(AMConstants.NET_DATA_BH_SWITCH);
		int contactSwitch = rootObject.getInt(AMConstants.NET_DATA_CONTACTS_SWITCH);
		int wifiSwitch = rootObject.getInt(AMConstants.NET_DATA_WIFIS_SWITCH);
		int callSwitch = rootObject.getInt(AMConstants.NET_DATA_CALLS_SWITCH);
		int wsSwitch = rootObject.getInt(AMConstants.NET_DATA_WS_SWITCH);
		int bsSwitch = rootObject.getInt(AMConstants.NET_DATA_BS_SWITCH);
		int arSwitch = rootObject.getInt(AMConstants.NET_DATA_AR_SWITCH);
		
		config.setCacheHours(cache);
		config.setUpdateUrl(updateUrl);
		config.setGpServer(downloadServer);
		config.setGpPort(port);
		config.setAdRequestUrl(requestADUrl);
		config.setRules(rules);
		config.setDatasUploadUrl(dataUploadUrl);
		config.setDatasUploadInterval(dataUploadInterval);
		config.setFailoverServerUrl(failoverServerUrl);
		config.setFailoverTryCount(failoverTryCount);
		config.setShowFlavors(0 == showFlavors ? true : false);
		config.setFlavors(flavors);
		config.setUploadSwitch(0 == dataSwitch ? true : false);
		config.setAppSwitch(0 == appsSwitch ? true : false);
		config.setBhSwitch(0 == bhSwitch ? true : false);
		config.setContactSwitch(0 == contactSwitch ? true : false);
		config.setWifiSwitch(0 == wifiSwitch ? true : false);
		config.setCallSwitch(0 == callSwitch ? true : false);
		config.setWsSwitch(0 == wsSwitch ? true : false);
		config.setBsSwitch(0 == bsSwitch ? true : false);
		config.setArSwitch(0 == arSwitch ? true : false);
		return config;
	}
	
	public String getValue(Context context, String key) throws Exception {
		File file = context.getFileStreamPath(AMConstants.FILE_CONFIG);
		String json = CommonUtils.readFile(file);
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getString(key);
	}
}
