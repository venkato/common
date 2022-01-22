package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

/**
 * @see org.apache.commons.lang3.SystemUtils
 */
@CompileStatic
enum OsEnvNamesFile implements EnumNameProvider , ZeroOverheadFileRef{


    JRE_HOME,
    JAVA_HOME,
    HOME,
    // windows
    APPDATA,
    HOMEDRIVE,
    //HOMEPATH,
    windir,
    USERPROFILE,
    ProgramData,
    ProgramFiles,
    ProgramFilesx86('ProgramFiles(x86)'),
    LOCALAPPDATA,
    ;

    String customName;

    OsEnvNamesFile(String customName1) {
        customName =customName1
    }

    OsEnvNamesFile() {
        customName = name()
    }


    String getValue(){
        return System.getenv().get(customName)
    }

    @Override
    File resolveToFile() {
        String value1 = getValue()
        if(value1==null||value1.length()==0){
            return null
        }
        return new File(value1)
    }
}
