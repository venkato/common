package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.idea.classpathtester.IdeaClassPathRuntimeTester
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

import java.util.logging.Logger

@CompileStatic
class IdeaInit6 implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        run2()
    }

    static void run2() throws Exception {
        if(InfocationFrameworkStructure.ifDir==null) {
            InfocationFrameworkStructure.ifDir = GitSomeRefs.commonUtil.resolveToFile()
            InfocationFrameworkStructure.ifCommonDir = GitSomeRefs.commonUtil.resolveToFile()
        }
        IdeaClasspathAdd.addCl.addAll IfFrameworkResourceDirs.all
        IdeaClasspathAdd.addCl.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsta()
        IdeaClasspathAdd.addCl.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion()
        IdeaClassPathRuntimeTester.runChecks()
    }



}
