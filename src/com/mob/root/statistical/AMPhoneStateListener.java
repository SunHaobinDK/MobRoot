package com.mob.root.statistical;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.mob.root.AMApplication;
import com.mob.root.net.BSExternalIPRequest;
import com.mob.root.statistical.AMLocation.AMLocationListener;
import com.mob.root.tools.AMConstants;
import com.mob.root.tools.AMLogger;
import com.mob.root.tools.CommonUtils;

public class AMPhoneStateListener extends PhoneStateListener {
	
	private Context mContext;

	public AMPhoneStateListener(Context context) {
		mContext = context;
	}
	
	@Override
	public void onCellLocationChanged(CellLocation location) {
		int phoneType = CommonUtils.getPhoneType(mContext);
		if(-1 == phoneType) {
			return;
		}
		switch (phoneType) {
		case TelephonyManager.PHONE_TYPE_GSM:
		case TelephonyManager.PHONE_TYPE_CDMA:
			int cid = -1;
			if(phoneType == TelephonyManager.PHONE_TYPE_GSM) {
				GsmCellLocation gsmCellLocation = ((GsmCellLocation) location);
				cid = gsmCellLocation.getCid();
			} else if(phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
				CdmaCellLocation cdmaCellLocation = ((CdmaCellLocation) location);
				cid = cdmaCellLocation.getBaseStationId();
			}
			SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
			int lastCID = sp.getInt(AMConstants.SP_LAST_CELL_ID, -1);
			if(lastCID == cid) { //如果当前基站id和上次获取到的基站id相同，则不记录
				return;
			}
			long stamp = System.currentTimeMillis();
			BSRecord record = new BSRecord(cid, stamp);
			record.record();
			
			BSExternalIPRequest request = new BSExternalIPRequest(null);
			request.start(stamp);
			
			AMLocation.getInstance(stamp).getCurrentLocation(new AMLocationListener() {
				
				@Override
				public void onLocationChanged(Location location, long stamp) {
					try {
						if(null == location) {
							return;
						}
						File file = AMApplication.instance.getFileStreamPath(AMConstants.FILE_BS);
						String json = CommonUtils.readFile(file);
						if (CommonUtils.isEmptyString(json)) {
							return;
						}
						JSONArray jsonArray = new JSONArray(json);
						int length = jsonArray.length();
						for (int i = 0; i < length; i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							long jsonStamp = jsonObject.getLong(AMConstants.FILE_BS_STAMP);
							if (stamp != jsonStamp) {
								continue;
							}
							jsonObject.put(AMConstants.FILE_BS_LATITUDE, location.getLatitude());
							jsonObject.put(AMConstants.FILE_BS_LONGITUDE, location.getLongitude());
							jsonArray.put(i, jsonObject);
							break;
						}
						json  = jsonArray.toString();
						CommonUtils.writeFile(json, file);
					} catch (Exception e) {
						AMLogger.e(null, e.getMessage());
					}
				}
			});
			break;
		}
	}
}
