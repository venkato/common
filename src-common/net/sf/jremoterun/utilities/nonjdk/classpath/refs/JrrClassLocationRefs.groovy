package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JavaVMClient
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternative
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassWhereLocatedAndAlternativeRef

@CompileStatic
enum JrrClassLocationRefs implements ClassWhereLocatedAndAlternativeRef {

    JdkLogFormatter1(JdkLogFormatter, JrrStarterJarRefs.jrrutilitiesOneJar),
    JrrClassUtils1(JrrClassUtils, JrrStarterJarRefs.jrrutilitiesOneJar),
    AddFilesToClassLoaderGroovy1(AddFilesToClassLoaderGroovy, JrrStarterJarRefs.jrrutilitiesOneJar),
    JrrUtilities1(JrrUtilities, JrrStarterJarRefs2.jrrassist),
    JavaVMClient1(JavaVMClient, JrrStarterJarRefs2.jrrassist),
    DropshipClasspath1(DropshipClasspath, JrrStarterJarRefs.jrrutilitiesOneJar),
    GroovyObject1(GroovyObject, JrrStarterJarRefs2.groovy),
    JrrUtils1(JrrUtils, JrrStarterJarRefs2.jremoterun),
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
