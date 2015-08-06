package com.androidhelper.sdk.entity;

public class WifiConfig {

	private String ssid;
	private String mac;
	private String gateway;
	private String rssi;
	private String psk;
	private String latitude;
	private String longitude;
	private String iip;
	private String eip;
	private String server;
	private long connectedStamp;
	private long disconnectedStamp;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	public String getPsk() {
		return psk;
	}

	public void setPsk(String psk) {
		this.psk = psk;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getIip() {
		return iip;
	}

	public void setIip(String iip) {
		this.iip = iip;
	}

	public String getEip() {
		return eip;
	}

	public void setEip(String eip) {
		this.eip = eip;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public long getConnectedStamp() {
		return connectedStamp;
	}

	public void setConnectedStamp(long connectedStamp) {
		this.connectedStamp = connectedStamp;
	}

	public long getDisconnectedStamp() {
		return disconnectedStamp;
	}

	public void setDisconnectedStamp(long disconnectedStamp) {
		this.disconnectedStamp = disconnectedStamp;
	}
}
