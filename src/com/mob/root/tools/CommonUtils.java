package com.mob.root.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebView;

import com.mob.root.AMApplication;

public class CommonUtils {

	public static boolean isEmptyString(String value) {
		if (null == value || "".equals(value.trim()))
			return true;
		return false;
	}
	
	public static void getDeviceInfo() {
    	SharedPreferences sp = AMApplication.instance.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
    	Editor edit = sp.edit();
		//ua
		WebView webView = new WebView(AMApplication.instance);
		String userAgent = webView.getSettings().getUserAgentString();
		edit.putString(AMConstants.SP_USER_AGENT, userAgent);
		//imei
		TelephonyManager tm = (TelephonyManager) AMApplication.instance.getSystemService(Context.TELEPHONY_SERVICE);
		edit.putString(AMConstants.SP_IMEI, tm.getDeviceId());
		//imsi
		edit.putString(AMConstants.SP_IMSI, tm.getSubscriberId());
		//inet_mac
		WifiManager wifi = (WifiManager) AMApplication.instance.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		String macAddress = wifiInfo.getMacAddress();
		edit.putString(AMConstants.SP_MAC, macAddress);
		//device_model
		edit.putString(AMConstants.SP_MODEL, Build.MODEL);
		//os_version
		edit.putInt(AMConstants.SP_SDK_VERSION, Build.VERSION.SDK_INT);
		//os_kernel
		edit.putString(AMConstants.SP_KERNEL_VERSION, CommonUtils.getKernelVersion());
		//screen_width
		//screen_height
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager wm = (WindowManager) AMApplication.instance.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;  
        int screenHeight = metric.heightPixels; 
        edit.putInt(AMConstants.SP_SCREEN_WIDTH, screenWidth);
        edit.putInt(AMConstants.SP_SCREEN_HEIGHT, screenHeight);
        
        //language
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        edit.putString(AMConstants.SP_LANGUAGE, language + "-" + country);
        
        //android_id
        String androidId = android.provider.Settings.Secure.getString(AMApplication.instance.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        edit.putString(AMConstants.SP_ANDROID_ID, androidId);
        
        //mcc  mnc
        String networkOperator = tm.getNetworkOperator();

        if (!CommonUtils.isEmptyString(networkOperator)) {
            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
            int mnc = Integer.parseInt(networkOperator.substring(3));
            
            edit.putInt(AMConstants.SP_MCC, mcc);
            edit.putInt(AMConstants.SP_MNC, mnc);
        }
        edit.commit();
	}

	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (FileNotFoundException e) {
			AMLogger.e(null, e.getMessage());
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (IOException e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			try {
				bufferedReader.close();
				inputStream.close();
			} catch (IOException e) {
				AMLogger.e(null, e.getMessage());
			}
		}
		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (IndexOutOfBoundsException e) {
			AMLogger.e(null, e.getMessage());
		}
		return kernelVersion;
	}
	
	public static synchronized void wirteFile(String datas, File file) {
		FileWriter writer = null;
		try {
			if (null != file && !file.exists()) {
				file.createNewFile();
			}
			String aesJson = AESUtil.encrypt(datas);
			if (!CommonUtils.isEmptyString(aesJson)) {
				writer = new FileWriter(file);
				writer.write(aesJson);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		}
	}
	
	public static synchronized String readFile(File file) {
		String data = null;
		FileReader reader = null;
		try {
			if(null != file && !file.exists()) {
				file.createNewFile();
			}
			reader = new FileReader(file);
			int len = -1;
			char[] buffer = new char[1024];
			StringBuilder sb = new StringBuilder();
			while ((len = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
			reader.close();
			data = sb.toString();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					AMLogger.e(null, e.getMessage());
				}
			}
		}
		String aesJson = AESUtil.desEncrypt(data);
		return aesJson;
	}
	
	public static String formatGateway(int gateway) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf((int) (gateway & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((gateway >> 8) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((gateway >> 16) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((gateway >> 24) & 0xff)));
		return sb.toString();
	}
	
	public static String replaceTab(String data) {
		Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
		Matcher matcher = pattern.matcher(data);
		return matcher.replaceAll("");
	}
	
	public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); 
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } 
        return "";
    }
}
