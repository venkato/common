package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.reftype.ToFileRefUnsupported
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2

/**
 * @see org.apache.commons.lang3.SystemUtils
 */
@CompileStatic
enum OsEnvNamesFile implements EnumNameProvider, ToFileRefRedirect2 {


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
    //noSuchProp8,
    ;


    String customName;

    OsEnvNamesFile(String customName1) {
        customName = customName1
    }

    OsEnvNamesFile() {
        customName = name()
    }


    String getValue() {
        return System.getenv().get(customName)
    }

//    @Override
//    File resolveToFile() {
//        String value1 = getValue()
//        if (value1 == null || value1.length() == 0) {
//            return null
//        }
//        return new File(value1)
//    }

    @Override
    File2FileRefWithSupportI getRedirect() {
        String value1 = getValue()
        if (value1 == null || value1.length() == 0) {
            return new ToFileRefUnsupported(this)
        }
        return new FileToFileRef(new File(value1))
    }
}
