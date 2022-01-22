package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Java8Files   implements ChildRedirect,EnumNameProvider{
    javaExeLinux('bin/java'),
    javaExe('bin/java.exe'),
    javawExe('bin/javaw.exe'),
    bin('bin'),
    rt_jar('lib/rt.jar'),
    include('include'),
    certificatesWhite('lib/security/cacerts'),
    certificatesBlack('lib/security/blacklisted.certs'),
    src('src.jar'),
    toolsJar('lib/tools.jar'),
    ;

    String customName;
    ExactChildPattern ref;

    Java8Files(String customName) {
        this.customName = customName
        ref = new ExactChildPattern (customName)
    }

}
