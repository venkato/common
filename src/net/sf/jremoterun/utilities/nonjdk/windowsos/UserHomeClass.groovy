package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazyImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.reftype.ToFileRefUnsupported
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNames
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum;

import java.util.logging.Logger;

@CompileStatic
enum UserHomeClass implements  ToFileRefRedirect2 {


    APPDATA(OsEnvNames.APPDATA),
    LOCALAPPDATA(OsEnvNames.LOCALAPPDATA),
    ProgramData(OsEnvNames.ProgramData),
    ProgramFiles(OsEnvNames.ProgramFiles),
    ProgramFilesx86(OsEnvNames.ProgramFilesx86),
    USERPROFILE(OsEnvNames.USERPROFILE),
    HOMEDRIVE(OsEnvNames.HOMEDRIVE),
    HOMEPATH_WINDOWS(false),

    ;

    File file;
    final File2FileRefWithSupportI redirect;

    UserHomeClass(PropsEnum value1) {
        this(value1.getValue())
    }

    UserHomeClass(OsEnvNames value1) {
        this(value1.getValue())
    }

    UserHomeClass(boolean rr) {
        String string1 = OsEnvNames.HOMEDRIVE.getValue()
        String string2 = OsEnvNames.HOMEPATH.getValue()
        if (string1 != null && string2 != null && string1.length() > 0 && string2.length() > 0) {
            this.file = new File(string1 + string2);
            redirect = new FileToFileRef(file)
        } else {
            this.file = new File("nosuchValue_${name()}/nosuchValue_${name()}")
            redirect = new  ToFileRefUnsupported(this)
        }

    }

    UserHomeClass(String value) {
        if (value == null || value.length() == 0) {
            this.file = new File("nosuchValue_${name()}/nosuchValue_${name()}")
            redirect = new  ToFileRefUnsupported(this)
        } else {
            this.file = new File(value)
            redirect = new FileToFileRef(file)
        }

    }

//    @Override
//    File resolveToFile() {
//        return file
//    }
//
//    @Override
//    FileChildLazyRef childL(String child) {
//        return new FileChildLazyRef(this, child);
//    }
//
//    @Override
//    FileChildLazyRef childP(ChildPattern child) {
//        return new FileChildLazyRef(this, child)
//    }
}
