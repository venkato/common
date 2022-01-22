package net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.ClassPathMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.*

import java.util.logging.Logger

@CompileStatic
class SshConsoleClasspath extends ClassPathMavenIds {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.logbackClassic,
            LatestMavenIds.logbackCore,
            LatestMavenIds.guavaMavenIdNew,
            LatestMavenIds.junit,
            SshdMavenIds.core,
            RstaJars.rsyntaxtextarea(),
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaui(),
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion(),
//            LatestMavenIds.rstaLangSupport,
            LatestMavenIds.commonsCollection,
            //Log4j2MavenIds.slf4j_impl,
            LatestMavenIds.jansi,
            LatestMavenIds.jline2,
//            LatestMavenIds.jline3,
            CustObjMavenIds.commonsIo,
    ]



    @Override
    List<MavenIdContains> getMavenIdsCustom(){

        List<MavenIdContains> list11= []
        list11.addAll GroovyMavenIds.all
        list11.addAll DropshipClasspath.allLibsWithGroovy
        list11.addAll NexusSearchMavenIds.all
        list11.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest


        return list11
    }


}
