package com.androidhelper.sdk.ad;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidhelper.sdk.ADType;
import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.EventType;
import com.androidhelper.sdk.entity.ADConfig;
import com.androidhelper.sdk.entity.ADRule;
import com.androidhelper.sdk.net.ConfigRequest;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.CommonUtils;

public class ADController {

	private static ADController adController;

	private ADController() {
	}

	public static ADController getInstance() {
		if (null == adController) {
			synchronized (ADController.class) {
				if (null == adController) {
					adController = new ADController();
				}
			}
		}
		return adController;
	}
	
	public ADType getDisplayADType(Context context, EventType eventType) throws Exception {
		List<ADRule> adRules = getNativeADRules(context);
		if (null == adRules) {
			SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			long stamp = sp.getLong(AMConstants.SP_LAST_CONFIG_STAMP, -1);
			if(-1 == stamp) {
				ConfigRequest request = new ConfigRequest(null);
				request.start();
			}
			return null;
		}
		
		SharedPreferences sp = context.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long lastStamp = sp.getLong(AMConstants.SP_LAST_AD_STAMP, 0);
		long currentStamp = System.currentTimeMillis();
		
		Random random = new Random(currentStamp);
		float probability = random.nextFloat();
		
		Iterator<ADRule> iterator = adRules.iterator();
		while (null != iterator && iterator.hasNext()) {
			ADRule rule = iterator.next();
			// 匹配事件
			if (rule.getEventType() != eventType) {	
				iterator.remove();	
				continue;
			}
			// 匹配概率
			if (rule.getProbability() < probability) {
				iterator.remove();
				continue;
			}
			// 匹配频次
			int freqSameCat = rule.getFreqSameCat();
			int freqGlobal = rule.getFreqGlobal();
			if (currentStamp - lastStamp < freqSameCat * 1000 || currentStamp - lastStamp < freqGlobal * 1000) {
				iterator.remove();
				continue;
			}
		}
		// 匹配优先级
		Collections.sort(adRules);
		return adRules.size() == 0 ? null : adRules.get(0).getAdtype();
	}
	
	private List<ADRule> getNativeADRules(Context context) throws Exception {
		File file = context.getFileStreamPath(AMConstants.FILE_CONFIG);
		String json = CommonUtils.readFile(file);
		ConfigParser parser = new ConfigParser();
		ADConfig config = parser.parse(json);
		if(null != config) {
			return config.getRules();
		}
		return null;
	}
}
