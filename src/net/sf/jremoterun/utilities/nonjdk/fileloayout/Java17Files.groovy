package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Java17Files  implements ChildRedirect,EnumNameProvider{
    bin('bin'),
    javaExeLinux('bin/java'),
    javaExe('bin/java.exe'),
    javawExe('bin/javaw.exe'),
    rt_jar('lib/rt.jar'),
    certificatesWhite('lib/security/cacerts'),
    certificatesBlack('lib/security/blocked.certs'),
    src('lib/src.jar'),
    modulesFile('lib/modules'),
    jmods('jmods'),
    include('include'),
    ;

    String customName;
    ExactChildPattern ref;

    Java17Files(String customName) {
        this.customName = customName
        ref = new ExactChildPattern(customName)
    }
}
