package com.mob.root.net.parser;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mob.root.ADType;
import com.mob.root.EventType;
import com.mob.root.entity.ADConfig;
import com.mob.root.entity.ADRule;
import com.mob.root.tools.AMConstants;

public class ConfigParser extends Parser {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(String datas) throws Exception {
		ADConfig config = new ADConfig();
		JSONObject rootObject = new JSONObject(datas);
		int cache = rootObject.getInt(AMConstants.NET_CACHE_CONTROL);
		String updateUrl = rootObject.getString(AMConstants.NET_VERSION_CONTROL_URL);
		String downloadServer = rootObject.getString(AMConstants.NET_GP_SERVER);
		String port = rootObject.getString(AMConstants.NET_GP_SERVER_PORT);
		String requestSingleADUrl = rootObject.getString(AMConstants.NET_AD_SINGLE_REQUEST_URL);
		String requestCollectionSingleADUrl = rootObject.getString(AMConstants.NET_AD_COLLECTION_REQUEST_URL);
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
		String placementId = rootObject.getString(AMConstants.NET_AD_FAN_PLACEMENTID);
		String dataUploadUrl = rootObject.getString(AMConstants.NET_DATA_UPLOAD_URL);
		String dataUploadInterval = rootObject.getString(AMConstants.NET_DATA_UPLOAD_INTERVAL);
		String failoverServerUrl = rootObject.getString(AMConstants.NET_FAILOVER_SERVER_URL);
		String failoverTryCount = rootObject.getString(AMConstants.NET_FAILOVER_TRY_COUNT);
		
		config.setCacheControl(cache);
		config.setVersionControlUrl(updateUrl);
		config.setGpServer(downloadServer);
		config.setGpPort(port);
		config.setRequestSingleUrl(requestSingleADUrl);
		config.setRequestCollectionUrl(requestCollectionSingleADUrl);
		config.setRules(rules);
		config.setPlacementId(placementId);
		config.setDataUploadUrl(dataUploadUrl);
		config.setDataUploadInterval(dataUploadInterval);
		config.setFailoverServerUrl(failoverServerUrl);
		config.setFailoverTryCount(failoverTryCount);
		return (T) config;
	}
}
