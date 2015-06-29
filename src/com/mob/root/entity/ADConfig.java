package com.mob.root.entity;

import java.util.List;

public class ADConfig {

	/** 配置文件缓存时间(day) */
	private int cacheDays;
	/** 版本控制地址 */
	private String updateUrl;
	/** gp下载服务器地址 */
	private String gpServer;
	/** gp下载服务器端口 */
	private String gpPort;
	/** 单个广告请求地址 */
	private String singleUrl;
	/** 多个广告请求地址 */
	private String collectionUrl;
	/** 广告规则 */
	private List<ADRule> rules;

	private String placementId;
	/** 数据上报地址 */
	private String datasUploadUrl;
	/** 数据上报时间间隔(hour) */
	private String datasUploadInterval;
	/** 备用服务器地址(请求配置文件) */
	private String failoverServerUrl;
	/** 请求失败时重复次数 */
	private String failoverTryCount;

	public int getCacheControl() {
		return cacheDays;
	}

	public void setCacheControl(int cacheControl) {
		this.cacheDays = cacheControl;
	}

	public String getVersionControlUrl() {
		return updateUrl;
	}

	public void setVersionControlUrl(String versionControlUrl) {
		this.updateUrl = versionControlUrl;
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

	public List<ADRule> getRules() {
		return rules;
	}

	public void setRules(List<ADRule> rules) {
		this.rules = rules;
	}

	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}

	public String getDataUploadUrl() {
		return datasUploadUrl;
	}

	public void setDataUploadUrl(String dataUploadUrl) {
		this.datasUploadUrl = dataUploadUrl;
	}

	public String getDataUploadInterval() {
		return datasUploadInterval;
	}

	public void setDataUploadInterval(String dataUploadInterval) {
		this.datasUploadInterval = dataUploadInterval;
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

	public String getRequestSingleUrl() {
		return singleUrl;
	}

	public void setRequestSingleUrl(String requestSingleUrl) {
		this.singleUrl = requestSingleUrl;
	}

	public String getRequestCollectionUrl() {
		return collectionUrl;
	}

	public void setRequestCollectionUrl(String requestCollectionUrl) {
		this.collectionUrl = requestCollectionUrl;
	}
}
