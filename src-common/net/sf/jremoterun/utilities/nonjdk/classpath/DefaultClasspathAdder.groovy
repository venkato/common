package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SshdMavenIds
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSwitch

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds.getMcu

@CompileStatic
class DefaultClasspathAdder extends InjectedCode {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean addLatestFlag =false;

    public static List findLatest = []
    static {
        List l2 = findLatest
        l2.addAll((List) LatestMavenIds.usefulMavenIdSafeToUseLatest)
        l2.add LatestMavenIds.reload4j
//        l2.add Log4j2MavenIds.api
//        l2.add Log4j2MavenIds.to_jul
        l2.add CustObjMavenIds.slf4jApi
//        if(!JdkLoggerSwitch.useJdkLogger) {
//            l2.add Log4j2MavenIds.slf4j_impl
//        }
    }

    @Override
    Object get(Object o) {
        AddFilesToClassLoaderCommon adder = o as AddFilesToClassLoaderCommon;
        if (adder == null) {
            throw new IllegalArgumentException("adder is null")
        }
        addRefs(adder);
        return null;
    }

    static void addRefs(AddFilesToClassLoaderCommon adder) {
        adder.addM LatestMavenIds.guavaMavenIdNew;
        adder.addM LatestMavenIds.junit;
        // jansi may not work :
//        adder.addM LatestMavenIds.jansi;
        adder.addAll GroovyMavenIds.all;
        if(addLatestFlag) {
            List<MavenIdContains> findLatest3 = findLatest
            findLatest3.each {
                adder.addM(LatestMavenIds.mcu.findLatestMavenOrGradleVersion2(it.m))
            };
        }else{
            adder.addAll(findLatest)
        }
//        adder.addMWithDependeciesDownload LatestMavenIds.jline3
//        adder.addMWithDependeciesDownload LatestMavenIds.jansi
//        adder.addMWithDependeciesDownload LatestMavenIds.jline2
        adder.addMWithDependeciesDownload SshdMavenIds.core
    }
}
