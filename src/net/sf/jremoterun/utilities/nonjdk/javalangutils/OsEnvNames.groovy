package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;


/**
 * @see org.apache.commons.lang3.SystemUtils
 */
@CompileStatic
enum OsEnvNames implements EnumNameProvider{

    LD_LIBRARY_PATH,
    PATH,
    Path,

    JRE_HOME,
    JAVA_HOME,
    // java pick this value by default
    _JAVA_OPTIONS,
    CLASSPATH,
    NUMBER_OF_PROCESSORS,
    user,
    HOME,
    INCLUDE,
    // windows
    APPDATA,
    HOMEDRIVE,
    HOMEPATH,
    USERNAME,
    PROMPT,
    windir,
    COMPUTERNAME,
    HOSTNAME,
    USERPROFILE,
    USERDOMAIN,
    ProgramData,
    ProgramFiles,
    ProgramFilesx86('ProgramFiles(x86)'),
    LOCALAPPDATA,
    LOGONSERVER,
    ;

    String customName;

    OsEnvNames(String customName1) {
        customName =customName1
    }
    OsEnvNames() {
        customName = name()
    }


    String getValue(){
        return System.getenv().get(customName)
    }
}
