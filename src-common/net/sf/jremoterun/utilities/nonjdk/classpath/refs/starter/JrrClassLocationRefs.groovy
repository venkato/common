package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JdkLoggerManager
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.java11.sep1.Java11ModuleAccessF
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternative
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternativeRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2

@CompileStatic
enum JrrClassLocationRefs implements ClassWhereLocatedAndAlternativeRef {

    JdkLogFormatter1(JdkLogFormatter, JrrStarterJarRefs.jrrutilitiesOneJar),
    JrrClassUtils1(JrrClassUtils, JrrStarterJarRefs.jrrutilitiesOneJar),
    AddFilesToClassLoaderGroovy1(AddFilesToClassLoaderGroovy, JrrStarterJarRefs.jrrutilitiesOneJar),
    JrrUtilities1(JdkLoggerManager, JrrStarterJarRefs2.jrrassist),
    JavaVMClient1(JavaVMClient, JrrStarterJarRefs2.jrrassist),
    DropshipClasspath1(DropshipClasspath, JrrStarterJarRefs.jrrutilitiesOneJar),
    GroovyObject1(GroovyObject, JrrStarterJarRefs2.groovy),
    JrrUtils1(JrrUtils, JrrStarterJarRefs2.jremoterun),
    Java11ModuleAccessF1(Java11ModuleAccessF, JrrStarterJarRefs2.java11base),
    SetConsoleOut21(SetConsoleOut2, JrrStarterJarRefs.jrrutilitiesOneJar),
    //test1(SetConsoleOut2, JrrStarterJarRefs2.jremoterun),
    ;


    ClassWhereLocatedAndAlternative ref;

    JrrClassLocationRefs(Class aClass, ToFileRef2 alternative) {
        ref = new ClassWhereLocatedAndAlternative(aClass, alternative);
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }
}
