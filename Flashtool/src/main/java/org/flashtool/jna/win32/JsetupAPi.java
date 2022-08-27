package org.flashtool.jna.win32;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.jna.linux.JUsb;
import org.flashtool.jna.win32.SetupApi.HDEVINFO;
import org.flashtool.jna.win32.SetupApi.SP_DEVICE_INTERFACE_DETAIL_DATA;
import org.flashtool.jna.win32.SetupApi.SP_DRVINFO_DATA;

import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsetupAPi {
	
	static SetupApi setupapi = (SetupApi) Native.loadLibrary("setupapi", SetupApi.class, W32APIOptions.UNICODE_OPTIONS);
    public static GUID USBGuid = new GUID();
    private static SP_DEVINFO_DATA DeviceInfoData = new SP_DEVINFO_DATA();
    private static SP_DRVINFO_DATA DriverInfoData = new SP_DRVINFO_DATA();
    static final Logger logger = LogManager.getLogger(JsetupAPi.class);
    
    static {
	    USBGuid.Data1=0xA5DCBF10;
	    USBGuid.Data2=0x6530;
	    USBGuid.Data3=0x11D2;
	    USBGuid.Data4=new byte[8];
	    USBGuid.Data4[0]=(byte)0x90;
	    USBGuid.Data4[1]=(byte)0x1F;
	    USBGuid.Data4[2]=(byte)0x00;
	    USBGuid.Data4[3]=(byte)0xC0;
	    USBGuid.Data4[4]=(byte)0x4F;
	    USBGuid.Data4[5]=(byte)0xB9;
	    USBGuid.Data4[6]=(byte)0x51;
	    USBGuid.Data4[7]=(byte)0xED;
	    DeviceInfoData.cbSize = DeviceInfoData.size();
	    DriverInfoData.cbSize = DriverInfoData.size();
    }
	
	public static String getClassName(GUID guid) {
		char[] ClassName = new char[100];
		boolean result = setupapi.SetupDiClassNameFromGuid(guid, ClassName, 100, null);
		if (result) {
			String name = new String();
			for (int i=0;i<100;i++) {
				if (ClassName[i]!=0)
					name = name+ClassName[i];
			}
			return name;
		}
		else {
			logger.error("Error calling SetupDiClassNameFromGuid");
		}
		return "";
	}

	public static GUID getGUID(String classname) {
		GUID[] ClassGuidList= new GUID[100];
		IntByReference size = new IntByReference();
		boolean result = setupapi.SetupDiClassGuidsFromName(classname, ClassGuidList, 100, size);
		if (result && (size.getValue()==1)) {
			return ClassGuidList[0];
		}
		else {
			logger.error("Error calling SetupDiClassNameFromGuid for "+classname);
		}
		return null;
	}

	public static void destroyHandle(HDEVINFO hDevInfo) {
		setupapi.SetupDiDestroyDeviceInfoList(hDevInfo);
	}
	
	public static SP_DEVINFO_DATA enumDevInfo(HDEVINFO hDevInfo, int index) {
		int result = setupapi.SetupDiEnumDeviceInfo(hDevInfo, index, DeviceInfoData);
		if (result == 0) {
			return null;
		}
		return DeviceInfoData;
	}

	public static boolean buildDriverList(HDEVINFO hDevInfo, SP_DEVINFO_DATA DeviceInfoData) {
		int buildresult = setupapi.SetupDiBuildDriverInfoList(hDevInfo, DeviceInfoData, SetupApi.SPDIT_COMPATDRIVER);
		if (buildresult == 0) {
			return false;
		}
		return true;
	}

	public static SP_DRVINFO_DATA enumDriverInfo(HDEVINFO hDevInfo, SP_DEVINFO_DATA DeviceInfoData, int index) {
		int result = setupapi.SetupDiEnumDriverInfo(hDevInfo, DeviceInfoData, SetupApi.SPDIT_COMPATDRIVER, index, DriverInfoData);
		if (result == 0) {
			return null;
		}
		return DriverInfoData;
	}

	public static SP_DRVINFO_DATA getDriver(HDEVINFO hDevInfo, SP_DEVINFO_DATA DeviceInfoData) {
	    SP_DRVINFO_DATA DriverInfoData = new SP_DRVINFO_DATA();
	    DriverInfoData.cbSize=DriverInfoData.size();
		int result = setupapi.SetupDiGetSelectedDriver(hDevInfo, DeviceInfoData, DriverInfoData);
		if (result == 0) {
			return null;
		}
		return DriverInfoData;
	}
	
	public static HDEVINFO getHandleForConnectedInterfaces() {
		return setupapi.SetupDiGetClassDevs(USBGuid, null, null, SetupApi.DIGCF_PRESENT|SetupApi.DIGCF_DEVICEINTERFACE);
	}
	
	public static HDEVINFO getHandleForConnectedDevices() {
		return setupapi.SetupDiGetClassDevs(null, null, null, SetupApi.DIGCF_PRESENT|SetupApi.DIGCF_ALLCLASSES);
	}

	public static boolean isInstalled(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData) {
		byte[] res = {'a','a','a','a'};
		setupapi.SetupDiGetDeviceRegistryProperty(DeviceInfoSet, DeviceInfoData, SetupApi.SPDRP_INSTALL_STATE,null,res,4,null);
		return (res[0]==0);
	}
	
	public static String getDevId(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData) {
		char[] DeviceId = new char[100];
		boolean result = setupapi.SetupDiGetDeviceInstanceId(DeviceInfoSet,	DeviceInfoData,	DeviceId, 100,null);
		if (result) {
			String name = new String();
			for (int i=0;i<100;i++) {
				if (DeviceId[i]!=0)
					name = name+DeviceId[i];
			}
			return name;
		}
		return "";
	}

	public static String getDevicePath(HDEVINFO hDevInfo, SP_DEVINFO_DATA DeviceInfoData) {
		String devpath = "";
        SP_DEVICE_INTERFACE_DATA DeviceInterfaceData = new SP_DEVICE_INTERFACE_DATA();
        DeviceInterfaceData.cbSize = DeviceInterfaceData.size();
        /* Query the device using the index to get the interface data */
        int index=0;
        do {
        	int result = setupapi.SetupDiEnumDeviceInterfaces(hDevInfo, DeviceInfoData, USBGuid,index, DeviceInterfaceData);
        	if (result == 0) {
        		break;        		
        	}
    		/* A successful query was made, use it to get the detailed data of the device */
    	    IntByReference reqlength = new IntByReference();
    	    /* Obtain the length of the detailed data structure, and then allocate space and retrieve it */
    	    result = setupapi.SetupDiGetDeviceInterfaceDetail(hDevInfo, DeviceInterfaceData, null, 0, reqlength, null);
    	    // Create SP_DEVICE_INTERFACE_DETAIL_DATA structure and set appropriate length for device Path */
    	    SP_DEVICE_INTERFACE_DETAIL_DATA DeviceInterfaceDetailData      = new SP_DEVICE_INTERFACE_DETAIL_DATA(reqlength.getValue());
    	    result = setupapi.SetupDiGetDeviceInterfaceDetail(hDevInfo, DeviceInterfaceData, DeviceInterfaceDetailData, reqlength.getValue(), reqlength, null);
    	    devpath = Native.toString(DeviceInterfaceDetailData.devicePath);
    	    if (devpath.length()==0) {
        	    SP_DEVICE_INTERFACE_DETAIL_DATA DeviceInterfaceDetailDataDummy      = new SP_DEVICE_INTERFACE_DETAIL_DATA();
        	    DeviceInterfaceDetailData.cbSize=DeviceInterfaceDetailDataDummy.size();
        	    result = setupapi.SetupDiGetDeviceInterfaceDetail(hDevInfo, DeviceInterfaceData, DeviceInterfaceDetailData, reqlength.getValue(), reqlength, null);
        	    devpath = Native.toString(DeviceInterfaceDetailData.devicePath);
    	    }
            index++;
        } while (true);
        return devpath;
	}
	
}