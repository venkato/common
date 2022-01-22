package net.sf.jremoterun.utilities.nonjdk.compile.auxh

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compiler3.AddGroovyToParentCl
import net.sf.jremoterun.utilities.nonjdk.compiler3.CopyFilesTmp;

import java.util.logging.Logger;

@CompileStatic
class AddGroovyToParentClResolver extends AddGroovyToParentCl{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void addGroovyJarToParentClassLoader(AddFilesToClassLoaderCommon adderParent, CopyFilesTmp copyFilesTmp) {
        //File dir = GitReferences.groovyClasspathDir.resolveToFile()
        adderParent.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.groovy_custom.gitOriginRef())
        adderParent.add copyFilesTmp.addClassPathCopy(JrrStarterJarRefs2.groovy.gitOriginRef())
        log.info "tmp cp1"
    }

    static void setRef(){
        AddGroovyToParentCl.defaultAddtoParentCl = new  AddGroovyToParentClResolver()
    }

}
