package com.androidhelper.sdk.net.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Permission;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.CommonUtils;

public class SingleADParser {
	
	public AD parseAD(String json) throws Exception {
		AD ad = new AD();
		JSONObject rootObject = new JSONObject(json);
		String id = rootObject.getString(AMConstants.NET_SINGLE_AD_ID);
		String title = rootObject.getString(AMConstants.NET_SINGLE_AD_TITLE);
		String category = rootObject.getString(AMConstants.NET_SINGLE_AD_CATEGORY);
		String icon = rootObject.getString(AMConstants.NET_SINGLE_AD_ICON);
		String cover = rootObject.getString(AMConstants.NET_SINGLE_AD_COVER);
		String desc = rootObject.getString(AMConstants.NET_SINGLE_AD_DESC);
		String rating = rootObject.getString(AMConstants.NET_SINGLE_AD_RATING);
		String favors = rootObject.getString(AMConstants.NET_SINGLE_AD_FAVORS);
		String packageName = rootObject.getString(AMConstants.NET_SINGLE_AD_PACKAGE_NAME);
		String landingPage = rootObject.getString(AMConstants.NET_SINGLE_AD_LANDING_PAGE);
		int openType = Integer.parseInt( rootObject.getString(AMConstants.NET_SINGLE_AD_OPEN_TYPE));
		JSONArray jsonArray = rootObject.getJSONArray(AMConstants.NET_SINGLE_AD_PERMISSIONS);
		int length = jsonArray.length();
		List<Permission> permissions = new ArrayList<Permission>();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String pid = jsonObject.getString(AMConstants.NET_SINGLE_AD_PERMISSIONS_ID);
			String ptitle = jsonObject.getString(AMConstants.NET_SINGLE_AD_PERMISSIONS_TITLE);
			String pdesc = jsonObject.getString(AMConstants.NET_SINGLE_AD_PERMISSIONS_DESC);
			
			Permission permission = new Permission();
			permission.setId(pid);
			permission.setTitle(ptitle);
			permission.setDesc(pdesc);
			
			permissions.add(permission);
		}
		length = jsonArray.length();
		jsonArray = rootObject.getJSONArray(AMConstants.NET_SINGLE_AD_PICS);
		List<String> pics = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String img = jsonObject.getString(AMConstants.NET_SINGLE_AD_PICS_IMG);
			
			pics.add(img);
		}
		String downloadUrl = rootObject.getString(AMConstants.NET_SINGLE_AD_DOWNLOAD_URL);
		ad.setId(id);
		ad.setCover_url(cover);
		ad.setDesc(desc);
		ad.setFavors(CommonUtils.isEmptyString(favors) ? 0 : Integer.parseInt(favors));
		ad.setIcon_url(icon);
		ad.setLandingPager(landingPage);
		ad.setPackageName(packageName);
		ad.setPermissions(permissions);
		ad.setPics(pics);
		ad.setRating(CommonUtils.isEmptyString(rating) ? 0 : Float.parseFloat(rating));
		ad.setTitle(title);
		ad.setCategory(category);
		ad.setOpenType(openType);
		ad.setDownloadUrl(downloadUrl);
		return ad;
	}
}
