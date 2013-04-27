package com.dragonflow;

import android.content.Context;
import android.content.SharedPreferences;

public class GenieGlobalDefines {
	final public static int WIRELESS_REQUESTCODE = 10000;
	final public static int GUESTACCESS_REQUESTCODE = 10001;
	final public static int WIRELESSSETTING_RESULT = 10002;
	
	final public static int SMARTNETWORKLOGIN_REQUESTCODE = 10003;
	final public static int SMARTNETWORKLOGIN_RESULT_SUCEESS = 10004;
	final public static int SMARTNETWORKLOGIN_RESULT_FAILED = 10005;
	final public static int TRAFFICSETTING_RESULT = 10006;
	final public static int TRAFFIC_REQUESTCODE = 10007;	
	final public static int LOCALLOGIN_RESULT_SUCEESS = 10008;
	final public static int LOCALLOGIN_RESULT_FAILED = 10009;
	
	final public static int WIRELESSSETTING_REFRESH = 10010;
	final public static int WIRELESS_CHANGECHANNEL = 10011;
	final public static int WIRELESS_SCAN = 10012;
	
	final public static  String SETTING_INFO = "setting_infos";
	final public static  String PASSWORD = "GENIEPASSWORD771009";
	
	final public static  String SMARTNETWORK = "GENIESMART";
	final public static  String WIFISCANSETTING = "WIFISCANSETTING";
	final public static  String WIFISCANDEFAULTENTRY = "WIFISCANDEFAULTENTRY";
	final public static  String WIFISCANCHANGECHANNELFLAG = "WIFISCANCHANGECHANNELFLAG";
	final public static  String SMARTROUTERUSERRNAME = "GENIESMARTROUTERUSERRNAME";
	final public static  String SMARTROUTERPASSWORD = "GENIESMARTROUTERPASSWORD";
	final public static  String SMARTROUTERREMEMBER = "GENIESMARTROUTERREMEMBER";
	
	final public static  String SMARTUSERRNAME = "GENIESMARTUSERRNAME";
	final public static  String SMARTPASSWORD = "GENIESMARTPASSWORD";
	final public static  String SMARTREMEMBER = "GENIESMARTREMEMBER";
	final public static  String SMARTSWITCH = "GENIESMARTSWITCH";
	final public static  String SMARTNETWORKURL = "SMARTNETWORKURL";
	


	
	final public static String DICTIONARY_KEY_RESPONCE = "ResponseCode";

	final public static String DICTIONARY_KEY_IS_GENIE	= "IsGenie";
	final public static String DICTIONARY_KEY_VERYTI_OK	= "VerityOK";

	final public static String DICTIONARY_KEY_MODE_NAME	= "ModelName";
	final public static String DICTIONARY_KEY_FIRMWARE_VERSION	= "Firmwareversion";

	final public static String DICTIONARY_KEY_New5GSupported = "New5GSupported";
	
	final public static String DICTIONARY_KEY_WLAN_ENABLE = "NewEnable";
	final public static String DICTIONARY_KEY_WLAN_SSID	= "NewSSID";
	final public static String DICTIONARY_KEY_WLAN_REGION = "NewRegion";
	final public static String DICTIONARY_KEY_WLAN_CHANNEL	= "NewChannel";
	final public static String DICTIONARY_KEY_WLAN_WIRE_MODE = "NewWirelessMode";
	final public static String DICTIONARY_KEY_WLAN_WPA_KEY	= "NewWPAEncryptionModes";
	final public static String DICTIONARY_KEY_WLAN_MAC	= "NewWLANMACAddress";
	final public static String DICTIONARY_KEY_WLAN_STATUS	= "NewStatus";
	
	final public static String DICTIONARY_KEY_INTERNET_STATUS	= "InternetConnectionStatus";
	final public static String DICTIONARY_KEY_LPC_SUPPORTED	= "ParentalControlSupported";
	final public static String DICTIONARY_KEY_SOAPVERSION	= "SOAPVersion";
	
	final public static String DICTIONARY_KEY_SMARTNETWORK_SUPPORTED	= "SmartNetworkSupported";
	
	final public static String DICTIONARY_KEY_WLAN_KEY = "NewWPAPassphrase";
	
	final public static String DICTIONARY_KEY_WLAN_WEP_KEY = "NewWEPKey";
	
	final public static String DICTIONARY_KEY_WLAN_BASICENCRYPTIONMODES = "NewBasicEncryptionModes";

	final public static String DICTIONARY_KEY_GUEST_ABLE = "NewGuestAccessEnabled";

	final public static String DICTIONARY_KEY_GUEST_SSID = "NewSSID-Guest";
	final public static String DICTIONARY_KEY_GUEST_MODE = "NewSecurityMode";
	final public static String DICTIONARY_KEY_GUEST_KEY	= "NewKey";

	final public static String DICTIONARY_KEY_TRAFFIC_ABLE = "NewTrafficMeterEnable";
	
	final public static String DICTIONARY_KEY_BLOCKDEVICE_EABLE = "NewBlockDeviceEnable";

	final public static String DICTIONARY_KEY_TRAFFIC_CONTROL = "NewControlOption";
	final public static String DICTIONARY_KEY_TRAFFIC_LIMIT	= "NewMonthlyLimit";
	final public static String DICTIONARY_KEY_TRAFFIC_HOUR	= "RestartHour";
	final public static String DICTIONARY_KEY_TRAFFIC_MINUTE = "RestartMinute";
	final public static String DICTIONARY_KEY_TRAFFIC_DAY = "RestartDay";

	//for set information
	final public static String DICTIONARY_KEY_WLAN_ENABLE_SET = "NewEnableSet";
	final public static String DICTIONARY_KEY_WLAN_SSID_SET	= "NewSSIDSet";
	final public static String DICTIONARY_KEY_WLAN_CHANNEL_SET = "NewChannelSet";
	final public static String DICTIONARY_KEY_WLAN_WPA_KEY_SET = "NewWPAEncryptionModesSet";

	final public static String DICTIONARY_KEY_WLAN_KEY_SET = "NewWPAPassphraseSet";

	final public static String DICTIONARY_KEY_GUEST_ABLE_SET = "NewGuestAccessEnabledSet";

	final public static String DICTIONARY_KEY_GUEST_SSID_SET = "NewSSID-GuestSet";
	final public static String DICTIONARY_KEY_GUEST_MODE_SET = "NewSecurityModeSet";
	final public static String DICTIONARY_KEY_GUEST_KEY_SET	= "NewKeySet";

	final public static String DICTIONARY_KEY_TRAFFIC_ABLE_SET = "NewTrafficMeterEnableSet";

	final public static String DICTIONARY_KEY_TRAFFIC_CONTROL_SET = "NewControlOptionSet";
	final public static String DICTIONARY_KEY_TRAFFIC_LIMIT_SET	= "NewMonthlyLimitSet";
	final public static String DICTIONARY_KEY_TRAFFIC_HOUR_SET = "RestartHourSet";
	final public static String DICTIONARY_KEY_TRAFFIC_MINUTE_SET = "RestartMinuteSet";
	final public static String DICTIONARY_KEY_TRAFFIC_DAY_SET = "RestartDaySet";

	final public static String DICTIONARY_KEY_DEVICEID = "NewDeviceID";
	final public static String DICTIONARY_KEY_MAC_ADDRESS = "NewMACAddress";
	final public static String DICTIONARY_KEY_PC_STATUS = "ParentalControl";
	final public static String DICTIONARY_KEY_PC_ENABLE = "NewEnable";
	final public static String DICTIONARY_KEY_PC_SUPPORT = "ParentalControlSupported=";
	
	final public static String DICTIONARY_KEY_LPC_DeviceID = "GetDNSMasqDeviceID";
	final public static String DICTIONARY_KEY_LPC_SetDeviceID = "SetDNSMasqDeviceID";
	final public static String DICTIONARY_KEY_LPC_DeleteMACAddress = "DeleteMACAddress";
	final public static String DICTIONARY_KEY_LPC_NewDeviceID= "NewDeviceID";
	final public static String DICTIONARY_KEY_LPC_MyNewDeviceID= "NewDeviceID";
	
	final public static String DICTIONARY_KEY_LPC_ChildDeviceIDUserName = "ChildDeviceIDUserName";
	final public static String DICTIONARY_KEY_LPC_DeviceIDUserName = "DeviceIDUserName";
	final public static String DICTIONARY_KEY_LPC_LoginBypassAccount = "LoginBypassAccount";
	
	
	final public static String DICTIONARY_KEY_BUNDLE = "BUNDLE";
	
	final public static String DICTIONARY_KEY_ROUTER_IP	= "RouterIP";

	final public static String ROUTER_TYPE_NOT_NETGEAR = "NOT_NETGEAR_ROUTER";
	final public static String ROUTER_TYPE_OLD_NETGEAR = "OLD_NETGEAR";
	final public static String ROUTER_TYPE_NEW_NETGEAR = "NEW_NETGEAR";
	
	final public static String LIST_TYPE_INDEX = "LIST_TYPE";
	
	final public static String WIRELESS_5G_fLAG = "WIRELESS_5G_fLAG";
	
	final public static String CLICK_ID = "CLICK_ID";
	
	final public static String SLIDE_ACTION = "SLIDE_ACTION";
	
	final public static String SLIDE_ACTION_BROADCAST = "SLIDE_ACTION_BROADCAST";
	
	final public static String SLIDE_ACTION_RET = "SLIDE_ACTION_RET";
	
	
	final public static String DLNA_ACTION = "DLNA_ACTION";
	
	final public static String DLNA_ACTION_BROADCAST = "DLNA_ACTION_BROADCAST";
	
	final public static String DLNA_ACTION_RET = "DLNA_ACTION_RET";
	
	final public static String USER_INFO_FILENAME = "userinfo";
	final public static String USER_MAP_FILENAME = "mapinfo";
	
	final public static String DEVICE_IP = "DEVICE_IP";
	final public static String DEVICE_NAME = "DEVICE_NAME";
	final public static String DEVICE_MAC = "DEVICE_MAC";
	final public static String DEVICE_CONNECTTYPE = "CONNECT_TYPE";
	final public static String DEVICE_SPEED = "CONNECT_SPEED"; // 
	final public static String DEVICE_INTENSITY = "CONNECT_INTENSITY"; // 
	final public static String DEVICE_BLOCK = "DEVICE_BLOCK"; //
	
	final public static String DICTIONARY_KEY_TRAFFIC = "NewTrafficMeter";  //////
	
	final public static String DICTIONARY_KEY_OPENDNS_STATUS= "status";
	final public static String DICTIONARY_KEY_OPENDNS_ERROR= "error";
	final public static String DICTIONARY_KEY_OPENDNS_ERROR_MESSAGE =  "error_message";
	final public static String DICTIONARY_KEY_OPENDNS_USERNAME= "username";
	final public static String DICTIONARY_KEY_OPENDNS_PASSWORD= "password";
	final public static String DICTIONARY_KEY_OPENDNS_CREATEUSERNAME= "createusername";
	final public static String DICTIONARY_KEY_OPENDNS_CREATEPASSWORD= "createpassword";
	final public static String DICTIONARY_KEY_OPENDNS_CREATEPASSWORD2= "createpassword2";
	final public static String DICTIONARY_KEY_OPENDNS_CREATEEMAIL= "createemail";
	final public static String DICTIONARY_KEY_OPENDNS_CREATEEMAIL2= "createemail2";
	final public static String DICTIONARY_KEY_OPENDNS_RESPONSE= "response";
	final public static String DICTIONARY_KEY_OPENDNS_TOKEN= "token";
	final public static String DICTIONARY_KEY_OPENDNS_DEVICEID= "device_id";
	final public static String DICTIONARY_KEY_OPENDNS_DEVICEKEY= "device_key";
	final public static String DICTIONARY_KEY_OPENDNS_BUNDLE= "bundle";
	final public static String DICTIONARY_KEY_OPENDNS_CATEGORIES= "categories";
	final public static String DICTIONARY_KEY_OPENDNS_AVAILABLE= "available";
	final public static String DICTIONARY_KEY_OPENDNS_RELAY_TOKEN= "relay_token";
	
	final public static String DICTIONARY_KEY_OPENDNS_CUSTOM= "Custom";
	
	
	final public static String EFunctionWireless_Result = "Wireless_Result";
	final public static String EFunctionGuestAccess_Result = "GuestAccess_Result";
	final public static String EFunctionMap_Result = "Map_Result";
	final public static String EFunctionTraffic_Result = "Traffic_Result";
	final public static String EFunctionSpeedTest_Result = "SpeedTest_Result";
	final public static String EFunctionParental_Result = "Parental_Result";
	final public static String EFunctionReadyShare_Result = "ReadyShare_Result";	

	final public static String NULL = "N/A";
	
	
	final public static String REQUEST_ACTION_RET_BROADCAST = "REQUEST_ACTION_RET_BROADCAST";
	final public static String REQUEST_ACTION_RET_LABLE = "REQUEST_ACTION_RET_LABLE";
	final public static String REQUEST_ACTION_RET_TYPE = "REQUEST_ACTION_RET_TYPE";
	final public static String REQUEST_ACTION_RET_SERVER = "REQUEST_ACTION_RET_SERVER";
	final public static String REQUEST_ACTION_RET_METHOD = "REQUEST_ACTION_RET_METHOD";
	final public static String REQUEST_ACTION_RET_RESULTTYPE = "REQUEST_ACTION_RET_RESULTTYPE";
	final public static String REQUEST_ACTION_RET_RESPONSECODE = "REQUEST_ACTION_RET_RESPONSECODE";
	final public static String REQUEST_ACTION_RET_HTTPRESPONSECODE = "REQUEST_ACTION_RET_HTTPRESPONSECODE";
	final public static String REQUEST_ACTION_RET_RESPONSE = "REQUEST_ACTION_RET_RESPONSE";
	final public static String REQUEST_ACTION_RET_HTTP_TYPE = "REQUEST_ACTION_RET_HTTP_TYPE";
	final public static String REQUEST_ACTION_RET_SOAP_TYPE = "REQUEST_ACTION_RET_SOAP_TYPE";
	
	final public static String REQUEST_ACTION_RET_SMART_TYPE = "REQUEST_ACTION_RET_SMART_TYPE";
	
	final public static String REQUEST_ACTION_RET_OPENDNS_TYPE = "REQUEST_ACTION_RET_OPENDNS_TYPE";
	final public static String REQUEST_ACTION_RET_ACTION_LABLE = "REQUEST_ACTION_RET_ACTION_LABLE";
	final public static String REQUEST_ACTION_RET_ERROR_CODE = "REQUEST_ACTION_RET_ERROR_CODE";
	
	public enum RequestActionType{
		Http,Soap,OpenDNS,SmartNetWork,
	}
	
	public enum RequestResultType{
		Succes,failed,Exception,error,Unauthorized,
	}
	
	
	final public static String SMARTNETWORK_KEY_AUTHENTICATED = "authenticated";
	
	
	
	
	final public static int LOGIN_ROUTER = -1;
	
	final public static int ESoapRequestIsGenie = 0;
	final public static int ESoapRequestVerityUser = 1;
	final public static int ESoapRequestRouterInfo = 2;
	final public static int ESoapRequestWLanInfo = 3;
	final public static int ESoapRequestWLanWEPKey = 4;
	final public static int ESoapRequestGuestEnable = 5;
	final public static int ESoapRequestGuestInfo = 6;
	final public static int ESoapRequestRouterMap = 7;
	final public static int ESoapReqiestTrafficEnable = 8;
	final public static int ESoapRequestTrafficMeter = 9;
	final public static int ESoapRequestTrafficOptions = 10;
	final public static int ESoapRequestDeviceID = 11;
	final public static int ESoapRequestPCStatus = 12;
	final public static int ESoapRequestSetDNSMasqDeviceID = 13;
	final public static int ESoapRequestfailure = 14;
	final public static int ESoapRequestsuccess = 15;
	final public static int ESoapRequestnotsupport = 16;	
	
	final public static int ESoapRequestWEPKey = 17;
	
	final public static int ESoapAuthenticate= 18;
	
	final public static int ESoapRequestBlockDeviceEnableStatus= 19;
	final public static int ESoapRequestSetBlockDeviceByMac= 20;
	final public static int ESoapRequestSetBlockDeviceEnable= 21;
	final public static int ESoapRequestEnableBlockDeviceForAll = 22;
	final public static int EHttpGetCurrentSetting = 23;
	final public static int EHttpCheckLpcHost = 24;
	
	final public static int ESoapDNSMasqDeviceID= 25;
	final public static int ESoapDNSMasqNewDeviceID= 26;
	
	final public static int ESoapMyDNSMasqDeviceID= 27;
	final public static int ESoapMyDNSMasqNewDeviceID= 28;
	
	final public static int ESoapDeviceChildUserName= 29;
	final public static int ESoapDeviceUserName= 30;
	final public static int ESoapLoginBypassAccount= 31;
	final public static int ESoapSetDNSMasqDeviceID= 32;
	final public static int ESoapDeleteMacAddress= 33;
	
	final public static int ESoapIs5GSupported = 34;
	final public static int ESoapRequest5GWLanInfo = 35;
	final public static int ESoapRequestWLan5GWPAKey = 36;
	final public static int ESoapRequest5GWEPKey = 37;
	
	final public static int ESoapRequest5GGuestEnable = 38;
	final public static int ESoapRequest5GGuestInfo = 39;
	final public static int ESoapRequestConfig5GGuestEnable = 40;
	
	final public static int ESoapRequestConfig5GGuestAccessNetwork = 41;
	final public static int ESoapRequestConfig5GGuestEnable2 = 42;
	
	
	final public static int ESMARTNETWORKAUTHENTICATE = 50;
	final public static int ESMARTNETWORK_INIT = 51;
	final public static int ESMARTNETWORK_GETROUTERLIST = 52;
	final public static int ESMARTNETWORK_StartRouterSession = 53;
	final public static int ESMARTNETWORK_SoapRequest = 54;
	final public static int ESMARTNETWORK_EndRouterSession = 55;
	

	final public static int ESoapRequestConfigGuestAccessNetwork = 98;
	final public static int ESoapRequestConfigWan = 99;
	final public static int ESoapRequestConfigStart = 100;
	final public static int ESoapRequestConfigWLan = 101;
	final public static int ESoapRequestConfigGuestEnable = 102;
	final public static int ESoapRequestConfigGuestEnable2 = 103;
	final public static int ESoapRequestConfigGuest = 104;
	final public static int ESoapRequestConfigTrafficEnable = 105;
	final public static int ESoapRequestConfigTraffic = 106;
	final public static int ESoapRequestConfigDeviceID = 107;
	final public static int ESoapRequestConfigPCStatus = 108;
	final public static int ESoapRequestConfigFinish = 109;
	final public static int ESoapRequestConfigNoSecurity = 110;
	
	final public static int ESoapRequestConfig5GNoSecurity = 111;
	final public static int ESoapRequestConfig5GWLan = 112;
	
	final public static int IS_RESPNCE_VALUE = 2;

	final public static int ROUTER_INFO_ROUTER_NAME = 100;
	final public static int ROUTER_INFO_FIRMWAREVERSIN = 101;

	final public static int WLAN_INFO_ENABLE = 110;
	final public static int WLAN_INFO_SSID = 111;
	final public static int WLAN_INFO_CHANNEL = 112;
	final public static int WLAN_INFO_REGION = 113;
	final public static int WLAN_INFO_WIRELESS_MODES = 114;
	final public static int WLAN_INFO_WPA_MODES	= 115;
	final public static int WLAN_INFO_MAC_ADDRESS = 116;
	final public static int WLAN_INFO_NET_STATUS = 117;
	final public static int WLAN_INFO_BASIC_MODES = 118;
	
	final public static int WLAN_INFO_New5GSupported = 119;

	final public static int GUEST_INFO_SSID	= 120;
	final public static int GUEST_INFO_SECURITY_MODE = 121;
	final public static int GUEST_INFO_KEY = 122;

	final public static int TRAFFIC_OPTION_CONTROL = 130;
	final public static int TRAFFIC_OPTION_LIMIT = 131;
	final public static int TRAFFIC_OPTION_HOUR	= 132;
	final public static int TRAFFIC_OPTION_MINUTE = 133;
	final public static int TRAFFIC_OPTION_DAY = 134;

	final public static int ACTIVE_FUNCTION_WIRELESS = 100;
	final public static int ACTIVE_FUNCTION_GUESTACESS = 101;
	final public static int ACTIVE_FUNCTION_MAP = 102;
	final public static int ACTIVE_FUNCTION_TRAFFIC = 103;
	final public static int ACTIVE_FUNCTION_WIRELESS_EMPTY = 104;
	final public static int ACTIVE_FUNCTION_PARENT_CONTROL = 106;
	final public static int ACTIVE_FUNCTION_MYMEDIA = 107;
	
	final public static int EFunctionWireless = 0;
	final public static int EFunctionGuestAccess = 1;
	final public static int EFunctionMap = 2;
	final public static int EFunctionParental = 3;	
	final public static int EFunctionTraffic = 4;
	final public static int EFunctionMyMedia = 5;	
	final public static int EFunctionQRCode = 6;	
	final public static int EFunctionWifiAnalyzer=8;
	final public static int EFunctionGenieAppStaging =7;
	final public static int EFunctionFileBrowse = 9;
	final public static int EFunctionFileTransfer=10;
	
	

	final public static int EFunctionSignalStrength = 13;
	final public static int EFunctionReadyShare = 11;
	final public static int EFunctionSpeedTest = 12;	
	final public static int EFunctionMax = 8;
	
	
	final public static int EFunctionParental_manage = 60;
	final public static int EFunctionParental_intro = 61;
	final public static int EFunctionParental_presignln = 62;
	final public static int EFunctionParental_signln = 63;
	final public static int EFunctionParental_CreateAccount = 64;
	final public static int EFunctionParental_Settings = 65;
	final public static int EFunctionParental_Done = 66;
	final public static int EFunctionParental_Firmware = 67;
	final public static int EFunctionParental_NetError = 68;
	final public static int EFunctionParental_Success = 69;
	final public static int EFunctionParental_failure = 70;
	final public static int EFunctionParental_manageerror = 71;
	
	final public static int EOpenDNSRequest_Login = 200;
	final public static int EOpenDNSRequest_CheckNameAvailable = 201;
	final public static int EOpenDNSRequest_CreateAccount = 202;
	final public static int EOpenDNSRequest_Success = 203;
	final public static int EOpenDNSRequest_GetDevice = 204;
	final public static int EOpenDNSRequest_CreateDevice = 205;
	final public static int EOpenDNSRequest_GetFilters = 206;
	final public static int EOpenDNSRequest_SetFilters = 207;
	final public static int EOpenDNSRequest_GetLabel = 208;
	final public static int EOpenDNSRequest_AccountRelay = 209;
	final public static int EOpenDNSRequest_GetSetDeviceId = 210;	
	
	
	
	final public static int EFunctionParental_CreatePassWordNotEquals = 220;
	final public static int EFunctionParental_CreateEmailNotEquals = 221;
	
	final public static int GenieView_MainView = 300;
	final public static int GenieView_ListView = 301;
	final public static int GenieView_LPCView = 302;
	final public static int GenieView_WifiModifyView = 303;
	final public static int GenieView_TrafficStting = 304;
	final public static int GenieView_Map = 305;
	
	
	final public static int EFunctionResult_Success = 500;
	final public static int EFunctionResult_failure = 501;
	
	final public static int PopupWindow = 600;
	

	final public static int Device_Internet = 700; //internetnormal
	final public static int Device_Genie = 701;		//hdm
		
	final public static int Device_imacdev = 702;    //R.drawable.imacdev,
	final public static int Device_ipad = 703;		//R.drawable.ipad,
	final public static int Device_iphone = 704;    //R.drawable.iphone,
	final public static int Device_ipodtouch = 705;	//R.drawable.ipodtouch,
	final public static int Device_androiddevice = 706;
	final public static int Device_androidphone = 707;
	final public static int Device_androitablet = 708;
	final public static int Device_blurayplayer = 709;
	final public static int Device_bridge = 710;    //R.drawable.bridge,
	final public static int Device_cablestb = 711;  //R.drawable.cablestb,
	final public static int Device_cameradev = 712;  //R.drawable.cameradev,
	final public static int Device_dvr = 713;  //R.drawable.cameradev,
	final public static int Device_gamedev = 714;    //R.drawable.gamedev,
	final public static int Device_linuxpc = 715;	//R.drawable.linuxpc,
	final public static int Device_macminidev = 716;	//R.drawable.macminidev,
	final public static int Device_macprodev = 717;		//R.drawable.macprodev,
	final public static int Device_macbookdev = 718;	//R.drawable.macbookdev,
	final public static int Device_mediadev = 719;		//R.drawable.mediadev,
	final public static int Device_networkdev = 720;  //networkdev
	final public static int Device_stb = 721;			//R.drawable.stb,
	final public static int Device_printerdev = 722;	//R.drawable.printerdev,
	final public static int Device_repeater = 723;		//R.drawable.repeater,
	final public static int Device_Route = 724;   //gatewaydev
	final public static int Device_satellitestb = 725;	//R.drawable.satellitestb,
	final public static int Device_scannerdev = 726;	//R.drawable.scannerdev,
	final public static int Device_slingbox = 727;		//R.drawable.slingbox,
	final public static int Device_netstoragedev = 728;	//R.drawable.netstoragedev,
	final public static int Device_mobiledev = 729;		//R.drawable.mobiledev,
	final public static int Device_Switch = 730;	//R.drawable.networkdev,
	final public static int Device_tv = 731;			//R.drawable.tv,
	final public static int Device_tablepc = 732;		//R.drawable.tablepc,
	final public static int Device_unixpc = 733;		//R.drawable.unixpc,
	final public static int Device_Pc = 734;	    //R.drawable.windowspc,
		
	final public static int Device_Route_CG3300 = 735;
	final public static int Device_Route_CGD24G = 736;
	final public static int Device_Route_DG834GT = 737;
	final public static int Device_Route_DG834GV = 738;
	final public static int Device_Route_DG834G = 739;
	final public static int Device_Route_DG834N = 740;
	final public static int Device_Route_DG834PN = 741;
	final public static int Device_Route_DG834 = 742;
	final public static int Device_Route_DGN1000_RN = 743;
	final public static int Device_Route_DGN2200M = 744;
	final public static int Device_Route_DGN2200 = 745;
	final public static int Device_Route_DGN2000 = 746;
	final public static int Device_Route_DGN3500 = 747;
	final public static int Device_Route_DGNB2100 = 748;
	final public static int Device_Route_DGND3300 = 749;
	final public static int Device_Route_DGND3700 = 750;
	final public static int Device_Route_DM111PSP = 751;
	final public static int Device_Route_DM111P = 752;
	final public static int Device_Route_JWNR2000t = 753;
	final public static int Device_Route_MBM621 = 754;
	final public static int Device_Route_MBR624GU = 755;
	final public static int Device_Route_MBR1210_1BMCNS = 756;
	final public static int Device_Route_MBRN3000 = 757;
	final public static int Device_Route_RP614 = 758;
	final public static int Device_Route_WGR612 = 759;
	final public static int Device_Route_WGR614L = 760;
	final public static int Device_Route_WGR614 = 761;
	final public static int Device_Route_WGT624 = 762;
	final public static int Device_Route_WNB2100 = 763;
	final public static int Device_Route_WNDR37AV = 764;
	final public static int Device_Route_WNDR3300 = 765;
	final public static int Device_Route_WNDR3400 = 766;
	final public static int Device_Route_WNDR3700 = 767;
	final public static int Device_Route_WNDR3800 = 768;
	final public static int Device_Route_WNDR4000 = 769;
	final public static int Device_Route_WNDRMAC = 770;
	final public static int Device_Route_WNR612 = 771;
	final public static int Device_Route_WNR834B = 772;
	final public static int Device_Route_WNR834M = 773;
	final public static int Device_Route_WNR854T = 774;
	final public static int Device_Route_WNR1000 = 775;
	final public static int Device_Route_WNR2000 = 776;
	final public static int Device_Route_WNR2200 = 777;
	final public static int Device_Route_WNR3500L = 778;
	final public static int Device_Route_WNR3500 = 779;
	final public static int Device_Route_WNXR2000 = 780;
	final public static int Device_Route_WPN824EXT = 781;
	final public static int Device_Route_WPN824N = 782;
	final public static int Device_Route_WPN824 = 783;
	final public static int Device_Route_WNDR4500 = 784;
	final public static int Device_Route_WNDR4700 = 785;
	final public static int Device_Route_DGND4000 = 786;
	final public static int Device_Route_WNR500 = 787;
	final public static int Device_Route_JNR3000 = 788;
	final public static int Device_Route_JNR3210 = 789;
	final public static int Device_Route_JWNR2000 = 790;
	final public static int Device_Route_R6300 = 791;
	final public static int Device_Route_R6200 = 792;
	final public static int Device_Route_MAX = 793;
	
	final public static int ESoapRequestNotGenie = 800;
	final public static int checkAvailabelPort = 801;
	
	final public static int ESoapRequestNotNETGEARRouter = 810;
	final public static int ESoapRequestLoginRouterFailure = 811;

	
	final public static int BIGACTIVITYTITLEBARNOSEARCHSIZE = 854;
	
	final public static int BLOCKDEVICES_NOSUPPORT = 2000;
	final public static int BLOCKDEVICES_ENABLE = 2001;
	final public static int BLOCKDEVICES_DISABLE = 2002;
	
	
	
	public static int GetSmartNetworkFlag(Context context)
    {
  	  GenieDebug.error("debug","GetSmartNetworkFlag ");
		SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0); 
		if(settings != null)
		{
		     int smartswitch = settings.getInt(GenieGlobalDefines.DICTIONARY_KEY_SMARTNETWORK_SUPPORTED,0);
		     return smartswitch;
		}else
		{
			return 0;
		}
    }
	public static void  SaveSmartNetworkFlag(Context context,int flag)
    {
  	  GenieDebug.error("debug", "SaveSmartNetworkFlag flag="+flag);
  	   
  	  
  	  	SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.SMARTNETWORK, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putInt(GenieGlobalDefines.DICTIONARY_KEY_SMARTNETWORK_SUPPORTED,flag).commit();
	      }

    }
	
	public static int GetWiFiScanDefaultEntry(Context context)
    {
  	  GenieDebug.error("debug","GetWiFiScanDefaultEntry ");
		SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.WIFISCANSETTING, 0); 
		if(settings != null)
		{
		     int smartswitch = settings.getInt(GenieGlobalDefines.WIFISCANDEFAULTENTRY,0);
		     return smartswitch;
		}else
		{
			return 0;
		}
    }
	public static void  SaveWiFiScanDefaultEntry(Context context,int entry)
    {
  	  GenieDebug.error("debug", "SaveWiFiScanDefaultEntry entry="+entry);
  	   
  	  
  	  	SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.WIFISCANSETTING, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putInt(GenieGlobalDefines.WIFISCANDEFAULTENTRY,entry).commit();
	      }

    }
	
	public static int GetWiFiScanChangeChannelFlag(Context context)
    {
  	  GenieDebug.error("debug","GetWiFiScanChangeChannelFlag ");
		SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.WIFISCANSETTING, 0); 
		if(settings != null)
		{
		     int smartswitch = settings.getInt(GenieGlobalDefines.WIFISCANCHANGECHANNELFLAG,0);
		     return smartswitch;
		}else
		{
			return 0;
		}
    }
	public static void  SaveWiFiScanChangeChannelFlag(Context context,int flag)
    {
  	  GenieDebug.error("debug", "SaveWiFiScanChangeChannelFlag flag="+flag);
  	   
  	  
  	  	SharedPreferences settings = context.getSharedPreferences(GenieGlobalDefines.WIFISCANSETTING, 0);
	      if(null != settings)
	      {
	    	  settings.edit().putInt(GenieGlobalDefines.WIFISCANCHANGECHANNELFLAG,flag).commit();
	      }

    }
	
}
