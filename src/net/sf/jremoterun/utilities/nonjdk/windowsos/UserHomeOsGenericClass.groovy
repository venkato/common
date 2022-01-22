package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.*
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNames
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum

@CompileStatic
enum UserHomeOsGenericClass implements File2FileRefWithSupportI, ZeroOverheadFileRef, ToFileRefRedirect2 {

    userHome(PropsEnum.user_home),
    javaHome(PropsEnum.java_home),
    HOME(OsEnvNames.HOME),

    ;

    File file;
    final FileToFileRef redirect;

    UserHomeOsGenericClass(PropsEnum value1) {
        this(value1.getValue())
    }

    UserHomeOsGenericClass(OsEnvNames value1) {
        this(value1.getValue())
    }

    UserHomeOsGenericClass(boolean rr) {
        String string1 = OsEnvNames.HOMEDRIVE.getValue()
        String string2 = OsEnvNames.HOMEPATH.getValue()
        if (string1 != null && string2 != null && string1.length() > 0 && string2.length() > 0) {
            this.file = new File(string1 + string2);
        } else {
            this.file = new File("nosuchValue_${name()}/nosuchValue_${name()}")
        }
        redirect = new FileToFileRef(file)
    }

    UserHomeOsGenericClass(String value) {
        if (value == null || value.length() == 0) {
            this.file = new File("nosuchValue_${name()}/nosuchValue_${name()}")
        } else {
            this.file = new File(value)
        }
        redirect = new FileToFileRef(file)
    }

    @Override
    File resolveToFile() {
        return file
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this, child)
    }
}
