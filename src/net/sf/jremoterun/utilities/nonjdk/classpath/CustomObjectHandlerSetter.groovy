package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.JavaVersionChecker
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds

import java.util.logging.Logger

@CompileStatic
class CustomObjectHandlerSetter extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClRef actualSupportAdder = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerSetter2')

    @Override
    Object get(Object key) {
        List list = key as List
        assert list.size() == 2
        AddFilesToClassLoaderCommon adder = list[0] as AddFilesToClassLoaderCommon;
        assert adder != null
        File f = list[1] as File
        add1(adder, f)
        return null
    }


    void add1(AddFilesToClassLoaderCommon adder, File f) {
        JavaVersionChecker.checkJavaVersion();
        // adder.add DropshipClasspath.commonsIo
        adder.add CustObjMavenIds.commonsIo
        adder.add CustObjMavenIds.commnonsLang
        adder.add CustObjMavenIds.git
        adder.add CustObjMavenIds.compressAbstraction
        adder.add CustObjMavenIds.zeroTurnaroundZipUtil
        adder.add CustObjMavenIds.junrar1
        adder.add CustObjMavenIds.slf4jApi
        adder.add CustObjMavenIds.slf4jJdkLogger
        adder.add CustObjMavenIds.commonsLoggingMavenId
        adder.add CustObjMavenIds.eclipseJavaCompiler
        adder.add CustObjMavenIds.eclipseJavaAstParser
        //adder.addAll CustObjMavenIds.all


        RunnableWithParamsFactory.fromClass4(actualSupportAdder, f)
    }


}
