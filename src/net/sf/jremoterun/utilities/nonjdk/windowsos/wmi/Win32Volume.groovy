package net.sf.jremoterun.utilities.nonjdk.windowsos.wmi

import com.sun.jna.platform.win32.COM.WbemcliUtil
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.WinNT
import groovy.transform.CompileStatic

@CompileStatic
enum Win32Volume {


    Name, DriveLetter, Label, FileSystem, Capacity, FreeSpace, DeviceID, Description,;

    static WbemcliUtil.WmiQuery<Win32Volume> createQuery() {
        return new WbemcliUtil.WmiQuery<Win32Volume>("Win32_Volume", Win32Volume);
    }

    public static void main() {
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED);
        System.out.println("CoInitializeEx: " + hr);
        try {
            WbemcliUtil.WmiResult<Win32Volume> result = Win32Volume.createQuery().execute()
            for (int i = 0; i < result.getResultCount(); i++) {
                System.out.println("Drive Letter: " + result.getValue(Win32Volume.DriveLetter, i));
                System.out.println("Label: " + result.getValue(Win32Volume.Label, i));
                System.out.println("File System: " + result.getValue(Win32Volume.FileSystem, i));
                System.out.println("Capacity: " + result.getValue(Win32Volume.Capacity, i));
                System.out.println("Free Space: " + result.getValue(Win32Volume.FreeSpace, i));
                System.out.println("---");
            }
        } finally {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

}
