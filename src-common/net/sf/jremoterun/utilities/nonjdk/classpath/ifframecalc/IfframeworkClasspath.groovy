package net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.ClassPathMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.*

import java.util.logging.Logger

@CompileStatic
class IfframeworkClasspath extends ClassPathMavenIds {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.logbackClassic,
            LatestMavenIds.logbackCore,
            LatestMavenIds.guavaMavenIdNew,
            //LatestMavenIds.rsyntaxtextarea,
//            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaui(),
//            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion(),
            LatestMavenIds.oshiCoreNativeUtils,
            LatestMavenIds.dnsResolver,
            LatestMavenIds.log4jOld,
            Log4j2MavenIds.slf4j_impl,
            Log4j2MavenIds.to_jul,
            Log4j2MavenIds.jul,
            Log4j2MavenIds.jcl,
            Log4j2MavenIds.core,
            Log4j2MavenIds.api,
            SshdMavenIds.core,
            SshdMavenIds.netty,
            SshdMavenIds.scp,
            SshdMavenIds.sftp,
            SshdMavenIds.common,
            GitMavenIds.jgit_ssh_jsch,
            GitMavenIds.jgit_lfs,
            LatestMavenIds.svnKit,
            LatestMavenIds.sequenceLibrary,
            LatestMavenIds.sqljet,
            LatestMavenIds.python,
            LatestMavenIds.jansi,
            LatestMavenIds.jline2,
//            LatestMavenIds.jline3,
//            LatestMavenIds.jcifs,
            LatestMavenIds.jcifsAgno3,
            LatestMavenIds.googleHttpClientApache,
            LatestMavenIds.googleHttpClientCommon,
            LatestMavenIds.opencensusApi,
            LatestMavenIds.dockerJibCore,
            LatestMavenIds.java11ModuleFileReader,
            LatestMavenIds.sshjHierynomus,
            LatestMavenIds.dockerJibBuildPlan,
            CustObjMavenIds.commonsIo,
            LatestMavenIds.quickfixj,
            LatestMavenIds.minaCore,
            LatestMavenIds.urlParser,
            BouncyCastleMavenIds.bctls_jdk15on,
            BouncyCastleMavenIds.bcpkix_jdk15on,
            GitlabLibsMavenIds.coreLib,
            GitlabLibsMavenIds.jakartaWsRsApi,
            // janino used in idea plugin only
            LatestMavenIds.javaCompiler2Janino,
            LatestMavenIds.javaCompilerJaninoCommon,
            LatestMavenIds.plexusClassworlds,
            LatestMavenIds.jodaTime,
            LatestMavenIds.svnNativeClintWrapper,
            LatestMavenIds.svnClientAdapterJavahlUseless,
            LatestMavenIds.svnClientAdapterMainUseless,
            LatestMavenIds.maven_resolver_provider,
            LatestMavenIds.plexusSisu,
            LatestMavenIds.plexus_utils,
            LatestMavenIds.commonsCli,
            LatestMavenIds.httpClient,
            LatestMavenIds.ajpClientApacheTomcat,
            GroovyMavenIds.sql,
            TomcatMavenIds.embed_core,
            Okhttp3MavenIds.okhttp,
            MavenMavenIdRandom.sharedUtils,
            MavenMavenIdRandom.wagonProviderApi,
            new MavenId('com.google.inject:guice:4.2.1'),
            new MavenId('org.eclipse.sisu:org.eclipse.sisu.inject:0.3.3'),
            new MavenId('org.sonatype.plexus:plexus-sec-dispatcher:1.4'),

//            MavenMavenIds.embedder,
//            MavenMavenIds.compat,
//            MavenMavenIds.model,
//            LatestMavenIds.jsoup,
    ]



    @Override
    List<MavenIdContains> getMavenIdsCustom(){

        List<MavenIdContains> list11= []
        list11.addAll DropshipClasspath.allLibsWithGroovy
        list11.addAll GroovyMavenIds.all
        list11.addAll MavenResolverMavenIds.all
        list11.addAll MavenMavenIds.all
        list11.addAll mavenIds
        list11.addAll GitlabLibsMavenIds.fastXmls
        list11.addAll GitlabLibsMavenIds.glassfish
        list11.addAll NexusSearchMavenIds.all
        list11.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        list11.addAll AsmOw.all
        list11.addAll EclipsePlatformMavenIds.all
        list11.addAll MaryDependentMavenIds.values().toList()
        return list11
    }
}
