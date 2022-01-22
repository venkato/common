package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath


// https://www.scilab.org/ looks nice, but how real use  ?
// https://github.com/openimaj/openimaj image utils
// https://github.com/validator/galimatias url parser
// http://jeuclid.sourceforge.net/ math formala display
// https://github.com/atteo/classindex
// swing table with filter http://www.glazedlists.com/propaganda/features
// https://github.com/bytedeco/javacpp-presets
// db changes appliedr : https://github.com/antonnazarov/apricot
// https://sourceforge.net/projects/javavirtualkeyb/
// https://github.com/Aghajari/MathParser
// https://www.tabnine.com/code/java/class-index
// https://www.javadoc.io/
// https://github.com/OpenHFT/Chronicle-Queue#quick-start
@CompileStatic
enum LatestMavenIds implements MavenIdContains, ToFileRef2 {

    // https://github.com/Sciss/DockingFrames
    log4jOld('log4j:log4j:1.2.17')
    , gnuGetOpt('gnu.getopt:java-getopt:1.0.13')
    ,
    zeroTurnaroundZipUtil(CustObjMavenIds.zeroTurnaroundZipUtil),
    slf4jApi(CustObjMavenIds.slf4jApi),
    commonsLoggingMavenId(CustObjMavenIds.commonsLoggingMavenId),
    xmlApiId(DropshipClasspath.xmlApis),
    commnonsLang(CustObjMavenIds.commnonsLang),
    //why jdom deprecated ?
    jdom('org.jdom:jdom:1.1'),
    junrar1(CustObjMavenIds.junrar1),
    compressAbstraction(CustObjMavenIds.compressAbstraction)

    , swtWin('org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:3.118.0')
    , eclipseWorkbench('org.eclipse.platform:org.eclipse.ui.workbench:3.124.0')
    , awsS3('com.amazonaws:aws-java-sdk-s3:1.11.275')
    , jnrJffi('com.github.jnr:jffi:1.3.9')
    , jniCodeGenerator('org.fusesource.hawtjni:hawtjni-example:1.18')
    ,
    // use eclipseJavaAstParser
    eclipseJavaCompiler(CustObjMavenIds.eclipseJavaCompiler)
    ,   eclipseJavaAstParser(CustObjMavenIds.eclipseJavaAstParser)
//    eclipseJavaCompiler('org.eclipse.jdt.core.compiler:ecj:4.6.1')
    , logbackClassic('ch.qos.logback:logback-classic:1.2.3')
    , logbackCore('ch.qos.logback:logback-core:1.2.11')
    , icu4j('com.ibm.icu:icu4j:65.1')
    , jodaTime('joda-time:joda-time:2.10.13')
    , junit('junit:junit:4.12')
    , rstaui("com.fifesoft:rstaui:2.6.1")
    , rstaAutoComplete("com.fifesoft:autocomplete:2.6.1")
    , fifeRtext("com.fifesoft.rtext:fife.common:2.6.3")
    , rstaLangSupport("com.fifesoft:languagesupport:2.6.0")
    , rsyntaxtextarea("com.fifesoft:rsyntaxtextarea:2.6.1")
    , jna("net.java.dev.jna:jna:5.10.0")
    , jnaPlatform("net.java.dev.jna:jna-platform:5.10.0")
    , jline2('jline:jline:2.12')
    , jline3('org.jline:jline:3.9.0')
    , zip4j('net.lingala.zip4j:zip4j:2.9.1')
    , mailJavax('javax.mail:mail:1.4.7')
    , mailSun('com.sun.mail:javax.mail:1.6.0')
    , apacheOro('oro:oro:2.0.8')
    , mailCommons('org.apache.commons:commons-email:1.5')
    , mailVertx('io.vertx:vertx-mail-client:3.5.2')
    , httpWinSupport('org.apache.httpcomponents:httpclient-win:4.5.13')
    , httpCore(DropshipClasspath.httpCore)
    , httpCoreNio('org.apache.httpcomponents:httpcore-nio:4.4.15')
    , httpClient("org.apache.httpcomponents:httpclient:4.5.13")
    , commonsHttpOld('commons-httpclient:commons-httpclient:3.1')
    , portForward('net.kanstren.tcptunnel:tcptunnel:1.1.2')
    // old jediterm failed if higher version of guava
    , guavaMavenId('com.google.guava:guava:21.0')
    , guavaMavenIdNew('com.google.guava:guava:26.0-jre')
    , xmlApisExt('xml-apis:xml-apis-ext:1.3.04')
    , xercesImpl('xerces:xercesImpl:2.11.0')
    , jfreeCommon('org.jfree:jcommon:1.0.24')
    , yandexDisk('com.yandex.android:disk-restapi-sdk:1.03')
    , jfreeChart('org.jfree:jfreechart:1.5.3')
    , rhino('org.mozilla:rhino:1.7.14')
    , commnonsLang3('org.apache.commons:commons-lang3:3.12.0')
    , commnonsText('org.apache.commons:commons-text:1.9')
    , commonsCollection('commons-collections:commons-collections:3.2.2')
    , commonsCodec('commons-codec:commons-codec:1.15')
    , servletApi('org.apache.tomcat:servlet-api:6.0.53')
    // visual diff and merge tool
    , jmeld('org.devocative:jmeld:3.2')
    , javaRepl('com.javarepl:javarepl:431')
    , dropbox('com.dropbox.core:dropbox-core-sdk:3.0.10')
    , javaCompiler1('com.github.javaparser:javaparser-core:3.24.0')
    , javaCompiler2Janino('org.codehaus.janino:janino:3.1.6')
    , javaCompilerJaninoCommon('org.codehaus.janino:commons-compiler:3.1.6')
    , webDavClient('com.github.lookfirst:sardine:5.10')
    , eclipseGroovyBatchCompiler('org.codehaus.groovy:groovy-eclipse-batch:2.4.13-01')
    , icePdfCore('org.icepdf.os:icepdf-core:6.2.2')
    , icePdfViewer('org.icepdf.os:icepdf-viewer:6.2.2')
    // comparision : http://ssh-comparison.quendi.de/comparison/cipher.html
    , j2sshMaverick('com.sshtools:j2ssh-maverick:1.5.5')
    , sshjHierynomus('com.hierynomus:sshj:0.32.0')
    ,
    // not much doc
    trileadSsh('com.trilead:trilead-ssh2:1.0.0-build222')
    ,
    // higher version depends on groovy 3
    groovySshShell('me.bazhenov.groovy-shell:groovy-shell-server:2.0.3')
    , commonsNet('commons-net:commons-net:3.6')
    , commonsCollection4('org.apache.commons:commons-collections4:4.4')
    , jansi('org.fusesource.jansi:jansi:1.17.1')
    , jsoup('org.jsoup:jsoup:1.14.3')
    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences#jschDocumentationBinWithSrc
     */
    , jcraft('com.jcraft:jsch:0.1.55')
    , jcraftZlib('com.jcraft:jzlib:1.1.3')
    , jideOss('com.jidesoft:jide-oss:3.6.18')
    , jmdns('javax.jmdns:jmdns:3.4.1')
    , jcifs('jcifs:jcifs:1.3.17')
    , commonsCli('commons-cli:commons-cli:1.4')
    , commonsConfig('commons-configuration:commons-configuration:1.10')
    , jetbrainsAnnotations('org.jetbrains:annotations-java5:16.0.3')
    , mx4j('mx4j:mx4j-tools:3.0.1')
    , cssParser('org.w3c.css:sac:1.3')
    , cssParser2('net.sf.cssbox:jstyleparser:4.0.0')
    , cssParser3('net.sourceforge.cssparser:cssparser:0.9.29')
    , commonsCompress('org.apache.commons:commons-compress:1.21')
    , pureJavaComm('com.github.purejavacomm:purejavacomm:1.0.2.RELEASE')
    , networkTestFramework('com.github.netcrusherorg:netcrusher-core:0.10')
    , kryoSerializer('com.esotericsoftware:kryo-shaded:4.0.2')
    , objenesis('org.objenesis:objenesis:2.6')
    , commonsIo(CustObjMavenIds.commonsIo)
    // http://repo1.maven.org/maven2/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.pom
    , commonsIoBad('org.apache.commons:commons-io:1.3.2')
    , jasperreports('net.sf.jasperreports:jasperreports:6.18.1')
    , quartz('org.quartz-scheduler:quartz:2.3.0')
    , winKillProcessTree('org.jvnet.winp:winp:1.28')
    , hamcrest('org.hamcrest:java-hamcrest:2.0.0.0')
    , svnKit(CustObjMavenIds.svnKit)
    , groovyCodeAnalizer('org.codenarc:CodeNarc:1.5')
    , trove4jIdea('org.jetbrains.intellij.deps:trove4j:1.0.20200330')
    ,
    // add lz4Compressor to classpath
    svnCli('org.tmatesoft.svnkit:svnkit-cli:1.10.4')
    , fernflowerJavaDecompiler('org.jboss.windup.decompiler.fernflower:windup-fernflower:1.0.0.20171018')
    , fernflowerLogger('org.jboss.windup.decompiler:decompiler-fernflower:5.2.1.Final')
    , fernflowerJavaDecompilerApi('org.jboss.windup.decompiler:decompiler-api:5.2.1.Final')
    , cfrJavaDecompilerApi('org.benf:cfr:0.152')
    , fontChooser('io.github.dheid:fontchooser:2.3')
    , opencsv("com.opencsv:opencsv:5.5.2")
    , powerShellWrapper('com.github.tuupertunut:powershell-lib-java:2.0.0'  )
//    , kotlinStd("org.jetbrains.kotlin:kotlin-stdlib:1.3.31")
//    , jasypt('org.jasypt:jasypt:1.9.2')

    ,
    @Deprecated
    eclipseGitHubApi(MavenIdAndRepoCustom.eclipseGitHubApi.m)
    , python('org.python:jython-standalone:2.7.2')
    , args4j('args4j:args4j:2.33')
    , quickfixj('org.quickfixj:quickfixj-all:2.1.1')
    , minaCore('org.apache.mina:mina-core:2.1.3')
    , javaApiCompare('com.github.siom79.japicmp:japicmp:0.15.3')
    , jcabiGithub('com.jcabi:jcabi-github:1.1.2')
    , lz4Compressor('org.lz4:lz4-java:1.8.0')
    ,
    // maven launcher
    plexusClassworlds('org.codehaus.plexus:plexus-classworlds:2.6.0')
    ,maven_resolver_provider('org.apache.maven:maven-resolver-provider:3.8.5')
    ,plexus_utils('org.codehaus.plexus:plexus-utils:3.4.1'),

    // Class org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
    annotationHell('org.springframework.boot:spring-boot-test-autoconfigure:2.2.0.RELEASE')
    ,
    dockerJibCore('com.google.cloud.tools:jib-core:0.20.0')
    ,
    dockerJava('com.github.docker-java:docker-java:3.2.12'),
    primitiveCollections('it.unimi.dsi:fastutil:8.5.2'),
    // TODO add connection killer
    oracleJdbc('com.oracle.database.jdbc.debug:ojdbc8_g:21.5.0.0'),
    zxingCore('com.google.zxing:core:3.4.1'),
    //below 3 svn stiff needed only for compile
    svnNativeClintWrapper('org.tmatesoft.svnkit:svnkit-javahl16:1.10.4'),
    svnClientAdapterJavahlUseless( 'org.netbeans.external:svnClientAdapter-javahl:RELEASE126'),
    svnClientAdapterMainUseless( 'org.netbeans.external:svnClientAdapter-main:RELEASE126'),
    heapAnalizerUi('org.netbeans.modules:org-netbeans-modules-profiler-heapwalker:RELEASE126'),
    // com.intellij.diagnostic.hprof.Analyzer
    heapAnalizerCore('org.netbeans.modules:org-netbeans-lib-profiler:RELEASE126'),
    // org.eclipse.mat.snapshot.SnapshotFactory
    heapAnalizerMat('com.github.cormoran-io.pepper:pepper-mat:2.1'),
    javaObjectLoyout1('org.openjdk.jol:jol-core:0.16'),
    javaObjectLoyout2("com.github.jbellis:jamm:0.3.3"),
    javaObjectLoyout3("org.ehcache:sizeof:0.4.0"),

    // math libs : need to check all
    mathCommon3('org.apache.commons:commons-math3:3.6.1'),
    mathEjml('org.ejml:ejml-all:0.41'),
    mathOjAlgo('org.ojalgo:ojalgo:51.3.0'),
    mathNd4j( 'org.nd4j:nd4j:1.0.0-M2'),

// org.apache.httpcomponents:httpcore:4.4.10 see in DropshipClasspath
    // xmlApis('xml-apis:xml-apis:1.4.01')   see in DropshipClasspath
    // antlrRuntime('org.antlr:antlr-runtime:3.5.2') in CustObjMavenIds
    ;


    MavenId m;


    LatestMavenIds(String m2) {
        this.m = new MavenId(m2)
    }

    LatestMavenIds(MavenIdContains m) {
        this.m = m.getM()
    }


    @Override
    File resolveToFile() {
        return m.resolveToFile()
    }

    static MavenCommonUtils mcu = new MavenCommonUtils()

//    public static List<? extends MavenIdContains> jol = [
//            new MavenId('org.openjdk.jol:jol-samples:0.9'),
//            new MavenId('org.openjdk.jol:jol-cli:0.9'),
//            new MavenId('org.openjdk.jol:jol-core:0.9'),
//    ]

//            'com.google.http-client:google-http-client:1.21.0'),
//            'com.google.oauth-client:google-oauth-client:1.21.0'),

    public
    static List<? extends MavenIdContains> loggingPrefix = [CustObjMavenIds.commonsLoggingMavenId, log4jOld]


    public
    static List<? extends MavenIdContains> specific = [GitMavenIds.jgit, junit, CustObjMavenIds.slf4jApi, jnrJffi, portForward, jna, jnaPlatform, gnuGetOpt, jodaTime, rstaui,//
                                                       CustObjMavenIds.eclipseJavaCompiler, CustObjMavenIds.eclipseJavaAstParser, rsyntaxtextarea, jfreeCommon, jfreeChart, CustObjMavenIds.commnonsLang, commonsCollection,//
                                                       commnonsLang3, commnonsText, commonsCodec, groovySshShell, javaCompiler1, icePdfCore, icePdfViewer, j2sshMaverick, commonsNet, commonsCollection4,//
                                                       jansi, jsoup, jcraft, httpClient, httpCore,httpCoreNio, jideOss, jmdns, jcifs, mx4j, trileadSsh, cssParser, commonsHttpOld, cssParser3, CustObjMavenIds.compressAbstraction,//
                                                       commonsCompress, jasperreports, quartz, winKillProcessTree, svnKit, fernflowerJavaDecompiler, fernflowerLogger, fontChooser, opencsv,svnCli,
                                                       powerShellWrapper,zxingCore,apacheOro,
    ]


    public
    static List<? extends MavenIdContains> usefulMavenIdSafeToUseLatest = DropshipClasspath.allLibsWithoutGroovy + loggingPrefix + (List) AntMavenIds.all + (List) Log4j2MavenIds.all + (List) specific + (List) CustObjMavenIds.all


    static MavenIdContains filterOnMavenId(MavenId mavenId) {
        String group = mavenId.groupId;
        String artifact = mavenId.artifactId
        List<? extends MavenIdContains> lastest = usefulMavenIdSafeToUseLatest
        List<String> lastestGrousp = lastest.collect { it.m.groupId }.unique()
        switch (mavenId) {
        // TODO delete goory from here
            case { group == 'org.codehaus.groovy' }:
            case { artifact == 'guava-jdk5' }:
            case { group == 'ch.qos.logback' }:
            case { group == 'asm' }:
            case { artifact == 'languagesupport' }:
            case { artifact == 'jcl-over-slf4j' }:
            case { artifact == 'google-collections' }:
                return null
            case { artifact == 'commons-logging-api' }:
                return CustObjMavenIds.commonsLoggingMavenId
            case { mavenId.isGroupAndArtifact(jline2) }:
                return jline2
            case { mavenId.isGroupAndArtifact(xmlApiId) }:
                return xmlApiId
            case { group == CustObjMavenIds.commonsLoggingMavenId.m.groupId }:
                return CustObjMavenIds.commonsLoggingMavenId
            case { mavenId.isGroupAndArtifact(commonsIoBad) }:
                commonsIo
            case { mavenId.isGroupAndArtifact(guavaMavenId) }:
                return guavaMavenId
            case { lastestGrousp.contains(group) }:
                return mcu.findLatestMavenOrGradleVersion2(mavenId)
            default:
                return mavenId;
        }
    }

}
