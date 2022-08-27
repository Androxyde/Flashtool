package org.flashtool.jna.win32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;

/** 
 * Declare a Java interface that holds the native "setupapi.dll" library methods by extending the W32API interface. 
 */
public interface SetupApi extends Library {

	public static class HDEVINFO extends WinNT.HANDLE {}

	public static class SP_DRVINFO_DATA extends Structure {
    	public SP_DRVINFO_DATA() {
    		DriverDate = new WinBase.FILETIME();
    	}
        public int     cbSize;
        public int     DriverType;
        public Pointer Reserved;
        public char[]  Description = new char[255];
        public char[]  MfgName = new char[255];
        public char[]  ProviderName= new char[255];
        public WinBase.FILETIME  DriverDate;
        public long DriverVersion;
        
        protected List getFieldOrder() {
        	return Arrays.asList("cbSize",
        				     "DriverType",
        				     "Reserved",
        				     "Description",
        				     "MfgName",
        				     "ProviderName",
        				     "DriverDate",
        				     "DriverVersion");
        }
    }

    
    public static class SP_DEVICE_INTERFACE_DETAIL_DATA extends Structure {

        public static class ByReference extends SP_DEVINFO_DATA implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public SP_DEVICE_INTERFACE_DETAIL_DATA() {
            cbSize = size();
            setAlignType(Structure.ALIGN_NONE);
        }
        
        public SP_DEVICE_INTERFACE_DETAIL_DATA(int size) {
        	cbSize = size();
        	devicePath = new char[size];
        	setAlignType(Structure.ALIGN_NONE);
        }

        public SP_DEVICE_INTERFACE_DETAIL_DATA(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * The size, in bytes, of the SP_DEVINFO_DATA structure.
         */
        public int cbSize;

        public char[] devicePath = new char[1];

        protected List getFieldOrder() {
        	return Arrays.asList("cbSize",
        				     "devicePath");
        }
    }


    public static int DIGCF_DEFAULT         = 0x00000001; 
    public static int DIGCF_PRESENT         = 0x00000002;
    public static int DIGCF_ALLCLASSES      = 0x00000004; 
    public static int DIGCF_PROFILE         = 0x00000008;
    public static int DIGCF_DEVICEINTERFACE = 0x00000010; 
    public static int SPDRP_DRIVER          = 0x00000009;
    public static int SPDRP_INSTALL_STATE   = 0x00000022;
    public static int SPDIT_COMPATDRIVER    = 0x00000002;
    

    HDEVINFO SetupDiGetClassDevs(GUID Guid, String Enumerator, WinDef.HWND Parent, int Flags);

    int SetupDiBuildDriverInfoList(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData, int DriverType);

    int SetupDiEnumDeviceInfo(HDEVINFO DeviceInfoSet, int MemberIndex, SP_DEVINFO_DATA DeviceInfoData);
    
    int SetupDiEnumDriverInfo(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData, int DriverType, int MemberIndex, SP_DRVINFO_DATA DriverInfoData);

    int SetupDiEnumDeviceInterfaces(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData, GUID Guid, int MemberIndex, SP_DEVICE_INTERFACE_DATA DeviceInterfaceData);

    int SetupDiGetDeviceInterfaceDetail(HDEVINFO DeviceInfoSet, SP_DEVICE_INTERFACE_DATA DeviceInterfaceData, SP_DEVICE_INTERFACE_DETAIL_DATA DeviceInterfaceDetailData, int DeviceInterfaceDetailDataSize, IntByReference RequiredSize, SP_DEVINFO_DATA DeviceInfoData);

    int SetupDiDestroyDeviceInfoList(HDEVINFO  DeviceInfoSet);
    
    boolean SetupDiGetDeviceInstanceId(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData,	char[] DeviceId, int DeviceInstanceIdSize,IntByReference RequiredSize);
    
    boolean SetupDiClassNameFromGuid(GUID Guid, char[] ClassName, int ClassNameSize, IntByReference RequiredSize);
    
    int SetupDiGetSelectedDriver(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData, SP_DRVINFO_DATA DriverInfoData);

    boolean SetupDiClassGuidsFromName(String ClassName, GUID[] ClassGuidList, int ClassGuidListSize, IntByReference RequiredSize);
    
    boolean SetupDiGetDeviceRegistryProperty(HDEVINFO DeviceInfoSet, SP_DEVINFO_DATA DeviceInfoData, int Property, IntByReference PropertyRegDataType, byte[] PropertyBuffer, int PropertyBufferSize, IntByReference RequiredSize);

}
