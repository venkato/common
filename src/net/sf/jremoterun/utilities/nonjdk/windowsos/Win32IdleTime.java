package net.sf.jremoterun.utilities.nonjdk.windowsos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sun.jna.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.*;

public class Win32IdleTime {

    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

}