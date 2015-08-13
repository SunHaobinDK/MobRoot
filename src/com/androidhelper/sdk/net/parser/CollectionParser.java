package com.androidhelper.sdk.net.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.CollectionAD;
import com.androidhelper.sdk.entity.Flavor;
import com.androidhelper.sdk.entity.Permission;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.CommonUtils;

public class CollectionParser {

	public CollectionAD parseAD(String json) throws Exception {
		CollectionAD collectionAD = new CollectionAD();
		List<AD> ads = new ArrayList<AD>();
		JSONObject rootObject = new JSONObject(json);
		int showFlavors = Integer.parseInt(rootObject.getString(AMConstants.NET_SHOW_FLAVORS));
		List<Flavor> flavors = new ArrayList<Flavor>();
		if(!rootObject.isNull(AMConstants.NET_FLAVORS)) {
			JSONArray flavorArray = rootObject.getJSONArray(AMConstants.NET_FLAVORS);
			int flavorLength = flavorArray.length();
			for (int j = 0; j < flavorLength; j++) {
				JSONObject flavorObj = flavorArray.getJSONObject(j);
				String flavorId = flavorObj.getString(AMConstants.NET_FLAVOR_ID);
				String flavorColor = flavorObj.getString(AMConstants.NET_FLAVOR_COLOR);
				String flavorName = flavorObj.getString(AMConstants.NET_FLAVOR_NAME);
				Flavor flavor = new Flavor();
				flavor.setId(flavorId);
				flavor.setColor(flavorColor);
				flavor.setName(flavorName);
				flavors.add(flavor);
			}
		}
		
		String hotGames = rootObject.getString(AMConstants.NET_HOT_GAMES);
		String weekGames = rootObject.getString(AMConstants.NET_WEEK_GAMES);
		
		JSONArray jsonArray = rootObject.getJSONArray(AMConstants.NET_ADS);
		int length = jsonArray.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String id = jsonObject.getString(AMConstants.NET_AD_ID);
			String title = jsonObject.getString(AMConstants.NET_AD_TITLE);
			String category = jsonObject.getString(AMConstants.NET_AD_CATEGORY);
			String icon = jsonObject.getString(AMConstants.NET_AD_ICON);
			String desc = jsonObject.getString(AMConstants.NET_AD_DESC);
			String rating = jsonObject.getString(AMConstants.NET_AD_RATING);
			String favors = jsonObject.getString(AMConstants.NET_AD_FAVORS);
			String packageName = jsonObject.getString(AMConstants.NET_AD_PACKAGE_NAME);
			String landingPage = jsonObject.getString(AMConstants.NET_AD_LANDING_PAGE);
			JSONArray permissionArray = jsonObject.getJSONArray(AMConstants.NET_AD_PERMISSIONS);
			int permissionLength = permissionArray.length();
			List<Permission> permissions = new ArrayList<Permission>();
			for (int j = 0; j < permissionLength; j++) {
				JSONObject permissionObj = permissionArray.getJSONObject(j);
				String pid = permissionObj.getString(AMConstants.NET_AD_PERMISSIONS_ID);
				String ptitle = permissionObj.getString(AMConstants.NET_AD_PERMISSIONS_TITLE);
				String pdesc = permissionObj.getString(AMConstants.NET_AD_PERMISSIONS_DESC);
				Permission permission = new Permission();
				permission.setId(pid);
				permission.setTitle(ptitle);
				permission.setDesc(pdesc);
				permissions.add(permission);
			}
			JSONArray picsArray = jsonObject.getJSONArray(AMConstants.NET_AD_PICS);
			int picsLength = picsArray.length();
			List<String> pics = new ArrayList<String>();
			for (int j = 0; j < picsLength; j++) {
				JSONObject picsObject = picsArray.getJSONObject(j);
				String img = picsObject.getString(AMConstants.NET_AD_PICS_IMG);
				
				pics.add(img);
			}
			
			AD ad = new AD();
			ad.setCategory(category);
			ad.setDesc(desc);
			ad.setFavors(CommonUtils.isEmptyString(favors) ? 0 : Integer.parseInt(favors));
			ad.setIcon_url(icon);
			ad.setId(id);
			ad.setLandingPager(landingPage);
			ad.setPackageName(packageName);
			ad.setPermissions(permissions);
			ad.setPics(pics);
			ad.setRating(CommonUtils.isEmptyString(rating) ? 0 : Float.parseFloat(rating));
			ad.setTitle(title);
			
			ads.add(ad);
		}
		
		collectionAD.setFlavors(flavors);
		collectionAD.setHotGames(hotGames);
		collectionAD.setShowFlavors(showFlavors == 0 ? true : false);
		collectionAD.setWeekGames(weekGames);
		collectionAD.setAds(ads);
		
		return collectionAD;
	}
}
