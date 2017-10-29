package com.innolux.common;

import com.innolux.dao.JdbcDaoHelper;

public class GlobleVar {

	// DB connection string
	public static final String WISConnectionStr = "jdbc:oracle:thin:@(DESCRIPTION =  (ADDRESS = (PROTOCOL = TCP)(HOST = 172.20.9.32)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = 172.20.9.33)(PORT = 1521))    (CONNECT_DATA =(SERVER = DEDICATED)   (SERVICE_NAME = t2prpt)))";
	public static final String WISUser = "wmsuser";
	public static final String WISPwd = "wmsuser123";
	public static final String T1WMSConnectionStr = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.56.130.74)(PORT = 1521)))(CONNECT_DATA =(SID = t1wms)(SERVER = DEDICATED)))";
	public static final String T1WMSUser = "T1_WMS_CIM";
	public static final String T1WMSPwd = "CIMWMST1MGR";
	public static final String T2WMSConnectionStr = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.56.196.98)(PORT = 1521)))(CONNECT_DATA =(SERVICE_NAME = t2wms)))";
	public static final String T2WMSUser = "T2_WMS_CIM";
	public static final String T2WMSPwd = "CIMWMST2MGR";

	// Subject
	public static final String ListenFromWMS = "INNOLUX.T2.PROD.WMS.DIS.RECEIVE";
	public static final String SendToWMS = "INNOLUX.T2.PROD.WMS.DIS.SEND";
	public static final String SendToAMS = "INNOLUX.T2.PROD.ALM.WIS";
	
	public static JdbcDaoHelper WIS_DB = new JdbcDaoHelper(GlobleVar.WISConnectionStr,GlobleVar.WISUser,GlobleVar.WISPwd,3);
	
	public static JdbcDaoHelper T1WMS_DB = new JdbcDaoHelper(GlobleVar.T1WMSConnectionStr,GlobleVar.T1WMSUser,GlobleVar.T1WMSPwd,3);
	
	public static JdbcDaoHelper T2WMS_DB = new JdbcDaoHelper(GlobleVar.T2WMSConnectionStr,GlobleVar.T2WMSUser,GlobleVar.T2WMSPwd,3);
	
	
	//Tag
		public static final String CylinderTag = "CY";
		public static final String ContainerTag = "C";
		public static final String PalletTag = "P";
		public static final String MarkTag = "M";
		public static final String TruckTag = "T";
		public static final String GuestTag = "G";
		public static final String ForkLiftTag = "F";
		public static final String GodTag = "X";
		
		
		//CarType
		public static final String ContainerStr = "Container";
		public static final String TruckStr = "Truck";
		
		//ANT
		public static final String ANT_Container = "CT";
		public static final String ANT_Pallet = "DN";
		public static final String ANT_Abnormal = "AB";
		public static final String ANT_T1_Dispatch = "T1DP";
		public static final String ANT_T2_Dispatch = "T2DP";
		public static final String ANT_In_CenterParking = "INCP";
		public static final String ANT_Out_CenterParking = "OUTCP";

		public static final String ANT_Big_Stock = "BS";
		public static final String ANT_Small_Stock = "SS";
		public static final String ANT_Big_Use = "BU";
		public static final String ANT_Small_Use = "SU";
		
		
		public static final Long TagMaskInTime = (long) 10000;
		public static final Long TagMaskOutTime = (long) 5000;
		public static final int EffectiveLimit = 4000;
		public static final int DescoverLimit = 4000;
		public static final long PalletDefaultExpire = 4000;
		public static final long IRDefaultExpire = 4000;
		
		//ForkLift
		public static final String ForkLiftIN = "IN";
		public static final String ForkLiftOUT = "OUT";
		public static final long DirectionExpire = 4000;
		public static final int Later = 1;
		public static final int Early = 2;
		public static final int Front = 1;
		public static final int Rear = 2;
		
		//Signal Tower
		public static final String GreenOn = "GreenOn";
		public static final String OrangeOn = "OrangeOn";
		public static final String RedOn = "RedOn"; 
		public static final String GreenOff = "GreenOff";
		public static final String OrangeOff = "OrangeOff";
		public static final String RedOff = "RedOff"; 
		
		public static final long LightExpire = 5000;
		
		//Display
		public static final long DisplayRecoverTime = 30000;
		
		//Pipe Cmmand
		public static final String ClearAllErrorPallet = "ClearAllErrorPallet";
		public static final String DeleteErrorPallet = "DeleteErrorPallet";
		public static final String CancelInContainerPallet = "CancelInContainerPallet";
		public static final String CustCaptionStr = "CustCaptionStr";
		public static final String RemoveNotComplete = "REMOVENOTCOMPLETE";
		public static final String RemoveNonEntryRecord = "REMOVENONENTRYRECORD";
		public static final String GateActionChange = "GateActionChange";
		public static final String BindContainer = "BindContainer";
		public static final String UnBindContainer = "UnBindContainer";
		public static final String IR = "IR";
		public static final String DirectionReport = "DirectionReport";
		public static final String TimeSync = "TimeSync";
		public static final String RawDataReport = "RawDataReport";
		public static final String WMSErrorMsg = "WMSErrorMsg";
		
		//Container Status
		public static final String ASNUnload = "ASNUNLOAD"; //ASN卸貨
		public static final String EmptyWrapUnload = "EMPTYWRAPUNLOAD"; //卸包材
		public static final String TransferOut = "TRANSFEROUT"; //廠移出
		public static final String TransferIn = "TRANSFERIN"; //廠移入
		public static final String DeliveryLoad = "DELIVERYLOAD"; //出貨
		//Container Actions
		public static final String Error = "ERROR"; //碼頭異常
		public static final String InProgress = "INPROGRESS"; //作業中
		public static final String ShutDown = "SHUTDOWN"; //非作業中
		
		public static final String NonOpreation = "NONOPREATION";
		public static final String AnyStatus = "ANYSTATUS";
		public static final String NonBinding = "NONBINDING";
		
		//Gas Status   create/待收貨，stock/庫存(這個是WMS這邊的狀態)，used/使用中,empty/空瓶，destroy/刪除(新增，表示離開氣體室)
		public static final String Gas_Create = "create";
		public static final String Gas_Stock = "stock";
		public static final String Gas_Used = "used";
		public static final String Gas_Empty = "empty";
		public static final String Gas_Destroy = "destroy";
		public static final String Gas_Disappear = "disappear";
		
		//Error Type
		public static final String FailureMode = "FAILUREMODE";
		public static final String DeliveryError = "DELIVERYERROR";
		public static final String ReceiveError = "RECEIVEERROR";
		public static final String NonStatusError = "NONSTATUSERROR";
		public static final String NonEntryRecord = "NONENTRYRECORD";
		public static final String BindingError = "BINDINGERROR";
		
		//Setting Name List
		public static final String PalletLimitTime = "PALLETLIMITTIME";
		public static final String ContainerIn = "CONTAINERIN";
		public static final String ContainerOut = "CONTAINEROUT";
		public static final String LoadNotComplete = "LOADNOTCOMPLETE";
		public static final String ASNError = "ASNERROR";
		public static final String VoicePath = "VOICEPATH";
}
