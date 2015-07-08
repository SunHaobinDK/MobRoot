package com.mob.root.net.parser;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mob.root.entity.AD;
import com.mob.root.entity.Permission;
import com.mob.root.tools.AMConstants;

public class AdParser {

	public List<AD> parseAD(String json) throws Exception {
		List<AD> ads = new ArrayList<AD>();
		JSONArray rootArray = new JSONArray(json);
		int length = rootArray.length();
		for (int i = 0; i < length; i++) {
			AD ad = new AD();
			JSONObject jsonObject = rootArray.getJSONObject(i);
			String id = jsonObject.getString(AMConstants.NET_AD_ID);
			String title = jsonObject.getString(AMConstants.NET_AD_TITLE);
			String category = jsonObject.getString(AMConstants.NET_AD_CATEGORY);
			String icon = jsonObject.getString(AMConstants.NET_AD_ICON);
			String cover = jsonObject.getString(AMConstants.NET_AD_COVER);
			String desc = jsonObject.getString(AMConstants.NET_AD_DESC);
			String rating = jsonObject.getString(AMConstants.NET_AD_RATING);
			String favors = jsonObject.getString(AMConstants.NET_AD_FAVORS);
			String landingPager = jsonObject.getString(AMConstants.NET_AD_LANDING_PAGE);
			String displayPager = jsonObject.getString(AMConstants.NET_AD_DISPLAY_PAGE);
			String packageName = jsonObject.getString(AMConstants.NET_AD_PACKAGE_NAME);
			JSONArray permissionArray = jsonObject.getJSONArray(AMConstants.NET_AD_PERMISSIONS);
			List<Permission> permissions = new ArrayList<Permission>();
			for (int j = 0; j < permissionArray.length(); j++) {
				JSONObject object = permissionArray.getJSONObject(i);
				String pid = object.getString(AMConstants.NET_AD_PERMISSIONS_ID);
				String pTitle = object.getString(AMConstants.NET_AD_PERMISSIONS_TITLE);
				String pDesc = object.getString(AMConstants.NET_AD_PERMISSIONS_DESC);
				
				Permission permission = new Permission();
				permission.setId(pid);
				permission.setTitle(pTitle);
				permission.setDesc(pDesc);
				
				permissions.add(permission);
			}
			JSONArray picArray = jsonObject.getJSONArray(AMConstants.NET_AD_PICS);
			List<String> pics = new ArrayList<String>();
			for (int j = 0; j < picArray.length(); j++) {
				JSONObject object = picArray.getJSONObject(i);
				String img = object.getString(AMConstants.NET_AD_PICS_IMG);
				pics.add(img);
			}
			String openType = jsonObject.getString(AMConstants.NET_AD_OPEN_TYPE);
			
			ad.setId(id);
			ad.setTitle(title);
			ad.setCategory(category);
			ad.setIcon_url(icon);
			ad.setCover_url(cover);
			ad.setDesc(desc);
			ad.setRating(null == rating ? 0f : Float.parseFloat(rating));
			ad.setFavors(null == favors ? 0 : Integer.parseInt(favors));
			ad.setLandingPager(landingPager);
			ad.setDisplayPager(displayPager);
			ad.setPackageName(packageName);
			ad.setPermissions(permissions);
			ad.setPics(pics);
			ad.setOpenType(null == openType ? 0 : Integer.parseInt(openType));
			
			ads.add(ad);
		}
		return ads;
	}
}
