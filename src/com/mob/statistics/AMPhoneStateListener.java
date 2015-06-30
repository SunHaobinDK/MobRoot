package com.mob.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.mob.root.entity.BSSwitch;
import com.mob.root.statistical.AMLocation;
import com.mob.root.tools.AMConstants;
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
			Editor edit = sp.edit();
			edit.putInt(AMConstants.SP_LAST_CELL_ID, cid).commit();
			String iip = CommonUtils.getIPAddress(true);
			long stamp = System.currentTimeMillis();
			Location lastLocation = AMLocation.getInstance(stamp).getLastLocation();
			
			final BSSwitch bsSwitch = new BSSwitch();
			bsSwitch.setCid(cid);
			bsSwitch.setStamp(stamp);
			bsSwitch.setIip(iip);
			bsSwitch.setEip("");
			bsSwitch.setLatitude(null == lastLocation ? "" : lastLocation.getLatitude() + "");
			bsSwitch.setLongitude(null == lastLocation ? "" : lastLocation.getLongitude() + "");
			
			break;
		}
	}
}
