package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger

@CompileStatic
class InfocationFrameworkAndGitAdder extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef gitAdderSupport = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassPathInit3')


    @Override
    Object get(Object o) {
        JavaVersionChecker.checkJavaVersion()
        List list = (List) o;
        if (list == null) {
            throw new IllegalArgumentException("need set adder and InfocationFramework base dir")
        }
        if (list.size() != 3) {
            throw new IllegalArgumentException("need set adder and InfocationFramework base dir, but got : ${list}")
        }
        AddFilesToClassLoaderCommon adder = list[0] as AddFilesToClassLoaderCommon;
        File ifDir = list[1] as File
        File gitDir = list[2] as File
       addStuff(adder,ifDir,gitDir)
        return null;
    }


    static void addStuffGit(AddFilesToClassLoaderCommon adder,File gitDir ){
        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }


        if (gitDir == null) {
            throw new IllegalArgumentException("Git dir is null")
        }
        assert gitDir.exists()
        RunnableWithParamsFactory.runClRef(gitAdderSupport,[adder,gitDir])
    }

    static void addStuff(AddFilesToClassLoaderCommon adder,File ifDir,File gitDir ){
        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }

        if (ifDir == null) {
            throw new IllegalArgumentException("InfocationFramework base dir is null")
        }


        if (gitDir == null) {
            throw new IllegalArgumentException("Git dir is null")
        }
        assert gitDir.exists()
        InfocationFrameworkStructure.addRefs(adder,ifDir)
        addStuffGit(adder,gitDir)
    }


}
