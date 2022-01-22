package net.sf.jremoterun.utilities.nonjdk.linux

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class LinuxEolTranslation {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String translate(String text1,EolConversion toLinux){
        if(toLinux==EolConversion.linux){
            return text1.replace(EolConversion.windows.eol, toLinux.eol)
        }
        assert toLinux== EolConversion.windows
//        return text1.replace(EolConversion.linux.eol,toLinux.eol)
        return text1
    }

}
