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
    COMPUTERNAME,
    HOSTNAME,
    USERPROFILE,
    USERDOMAIN,
    ProgramData,
    ProgramFiles,
    APPDATA,
    HOMEDRIVE,
    HOMEPATH,
    JRE_HOME,
    JAVA_HOME,
    // java pick this value by default
    _JAVA_OPTIONS,
    CLASSPATH,
    LOCALAPPDATA,
    LOGONSERVER,
    NUMBER_OF_PROCESSORS,
    user,
    HOME,
    INCLUDE,
    // windows
    USERNAME,
    PROMPT,
    ;

    String customName;

    OsEnvNames() {
        customName = name()
    }
}
