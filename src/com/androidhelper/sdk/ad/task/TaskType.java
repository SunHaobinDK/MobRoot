package com.androidhelper.sdk.ad.task;

public enum TaskType {

	/** 简单通知栏广告 */
	NOTIFICATION_SIMPLE,

	/** 复杂通知栏广告 */
	NOTIFICATION_ADVANCED,

	/** 简单浮动pop广告 */
	POP_SIMPLE,

	/** 复杂浮动banner广告 */
	POP_ADVANCED,

	/** 简单窗口广告 */
	WINDOW_SOLO,

	/** 复杂窗口广告 */
	WINDOW_COLLECTION,
	
	/** 安装引导窗口 */
	WINDOW_GUIDE_INSTALL,
	
	/** 正在安装app窗口 */
	WINDOW_INSTALLING,

	/** 安装完成窗口 */
	WINDOW_INSTALLED,

	/** 已安装第三方app窗口 */
	WINDOW_INSTALLED_APP,
}
