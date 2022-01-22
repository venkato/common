package net.sf.jremoterun.utilities.nonjdk.compiler3;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AddGroovyToParentCl {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile AddGroovyToParentCl defaultAddtoParentCl = new AddGroovyToParentCl();


    void addGroovyJarToParentClassLoader(AddFilesToClassLoaderCommon adderParent, CopyFilesTmp copyFilesTmp) {
        File file = UrlCLassLoaderUtils.getClassLocation(ShortTypeHandling)
        if (file.isFile()) {
            adderParent.add file
        } else {
            log.warn("ShortTypeHandling strange : ${file}")
        }
        adderParent.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.groovy.gitOriginRef())
    }


}
