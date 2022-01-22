package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath


// https://www.scilab.org/ looks nice, but how real use  ?
// https://github.com/openimaj/openimaj image utils
// https://github.com/validator/galimatias url parser
// http://jeuclid.sourceforge.net/ math formala display
// https://github.com/atteo/classindex
// swing table with filter http://www.glazedlists.com/propaganda/features
// https://github.com/bytedeco/javacpp-presets
// db changes applier : https://github.com/antonnazarov/apricot
// https://sourceforge.net/projects/javavirtualkeyb/
// https://github.com/PankajPrakashh/On-Screen-Keyboard
// https://github.com/Aghajari/MathParser
// https://www.tabnine.com/code/java/class-index
// https://www.javadoc.io/
// https://github.com/OpenHFT/Chronicle-Queue#quick-start
// https://maven.apache.org/repository/central-index.html
// java 11 to 8 byte code translation ??: https://github.com/bsideup/jabel
// java11 stream in java 8 https://github.com/stefan-zobel/streamsupport
// https://docs.microsoft.com/en-us/java/openjdk/transition-from-java-8-to-java-11
// https://github.com/google/desugar_jdk_libs : https://developer.android.com/studio/write/java8-support#library-desugaring
// https://github.com/Sciss/DockingFrames
// https://github.com/JFormDesigner/FlatLaf
// stylus pen for java : https://github.com/lectureStudio/stylus
// https://en.wikipedia.org/wiki/Java_backporting_tools
// https://mina.apache.org/sshd-project/index.html
// https://github.com/renaissance-benchmarks/renaissance
// java source code parser https://spoon.gforge.inria.fr/
// https://github.com/openjdk/jdk/blob/master/src/hotspot/share/runtime/reflection.cpp verify_class_access
// JavaToJs converter https://github.com/konsoletyper/teavm
// https://github.com/Konloch/bytecode-viewer
// https://scijava.org/
// gui for git https://git-scm.com/downloads/guis
@CompileStatic
enum LatestMavenIds implements MavenIdContains {


    @Deprecated
    log4jOld('log4j:log4j:1.2.17')
    , reload4j('ch.qos.reload4j:reload4j:1.2.26')
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
    , jnrJffi('com.github.jnr:jffi:1.3.12')
    , jniCodeGenerator('org.fusesource.hawtjni:hawtjni-example:1.18')
    ,
    // use eclipseJavaAstParser
    eclipseJavaCompiler(CustObjMavenIds.eclipseJavaCompiler)
    ,   eclipseJavaAstParser(CustObjMavenIds.eclipseJavaAstParser)
//    eclipseJavaCompiler('org.eclipse.jdt.core.compiler:ecj:4.6.1')
    ,// 1.4. need java9+
    logbackClassic('ch.qos.logback:logback-classic:1.3.10')
    , logbackCore('ch.qos.logback:logback-core:1.3.10')
    , icu4j('com.ibm.icu:icu4j:73.2')
    , jodaTime('joda-time:joda-time:2.14.0')
    , junit('junit:junit:4.12')
    , rstaui("com.fifesoft:rstaui:3.3.1")//("com.fifesoft:rstaui:2.6.1")
    , rstaAutoComplete('com.fifesoft:autocomplete:3.3.1')//("com.fifesoft:autocomplete:2.6.1")
    , fifeRtext("com.fifesoft.rtext:fife.common:6.0.3")//("com.fifesoft.rtext:fife.common:2.6.3")
    , rstaLangSupport("com.fifesoft:languagesupport:3.3.0")//("com.fifesoft:languagesupport:2.6.0")
    , rsyntaxtextarea("com.fifesoft:rsyntaxtextarea:3.4.0")//("com.fifesoft:rsyntaxtextarea:2.6.1")
    , jna("net.java.dev.jna:jna:5.15.0")
    , jnaPlatform("net.java.dev.jna:jna-platform:5.15.0")
//    , jansi('org.fusesource.jansi:jansi:2.4.0')
    , jansi('org.fusesource.jansi:jansi:1.18')
//    , jline2('jline:jline:2.12')
    , jline2('jline:jline:2.14.6')
    , jline3('org.jline:jline:3.9.0')
    , zip4j('net.lingala.zip4j:zip4j:2.11.5')
    , mailJavax('javax.mail:mail:1.4.7')
    , mailSun('com.sun.mail:javax.mail:1.6.0')
    , apacheOro('oro:oro:2.0.8')
    , java11ModuleFileReader("org.glavo:jimage:1.0.0")
    , mailCommons('org.apache.commons:commons-email:1.5')
    , mailVertx('io.vertx:vertx-mail-client:4.4.6')
    , httpWinSupport('org.apache.httpcomponents:httpclient-win:4.5.14')
    , windowsAuth( 'com.github.waffle:waffle-jna:3.5.0')
    , httpCore(DropshipClasspath.httpCore)
    , httpCoreNio('org.apache.httpcomponents:httpcore-nio:4.4.16')
    , httpClient("org.apache.httpcomponents:httpclient:4.5.14")
    , urlParser("io.mola.galimatias:galimatias:0.2.1")
    , packageurl("com.github.package-url:packageurl-java:1.5.0")
    , commonsHttpOld('commons-httpclient:commons-httpclient:3.1')
    , googleHttpClientCommon('com.google.http-client:google-http-client:1.42.2')
    , googleHttpClientApache('com.google.http-client:google-http-client-apache-v2:1.42.2')
    ,
    // needed for com.google.http-client:google-http-client:
    opencensusApi('io.opencensus:opencensus-api:0.31.1')
    , portForward('net.kanstren.tcptunnel:tcptunnel:1.1.2')
    // old jediterm failed if higher version of guava
//    ,
//    @Deprecated
//    guavaMavenId('com.google.guava:guava:21.0')
    , // needed for guava 31.1-jre version
    guavaFailureAccess('com.google.guava:failureaccess:1.0.1')
    , guavaMavenIdNew('com.google.guava:guava:26.0-jre')
    , xmlApisExt('xml-apis:xml-apis-ext:1.3.04')
    , xercesImpl('xerces:xercesImpl:2.11.0')
    , jfreeCommon('org.jfree:jcommon:1.0.24')
    , jfreeChart('org.jfree:jfreechart:1.5.5')
    , openjsse('org.openjsse:openjsse:1.1.14')
    //, yandexDisk('com.yandex.android:disk-restapi-sdk:1.03')
    , rhino('org.mozilla:rhino:1.7.14')
    , yamlSnake('org.yaml:snakeyaml:2.2')
    , commnonsLang3('org.apache.commons:commons-lang3:3.14.0')
    , commnonsText('org.apache.commons:commons-text:1.12.0')
    , commonsCollection('commons-collections:commons-collections:3.2.2')
    , flatbuffers('com.google.flatbuffers:flatbuffers-java:25.2.10')
    ,
    commonsCodec('commons-codec:commons-codec:1.17.0')
    , servletApi('org.apache.tomcat:servlet-api:6.0.53')
    // visual diff and merge tool, use netbeans as git browser
    , jmeld('org.devocative:jmeld:3.2')
    , diff1("io.github.java-diff-utils:java-diff-utils:4.15")
    , diff2html("de.cronn:diff-to-html:1.6")
    , gumDiffLangAware('com.github.gumtreediff:core:3.0.0')
    , javaRepl('com.javarepl:javarepl:431')
    //, dropbox('com.dropbox.core:dropbox-core-sdk:3.0.10')
    , javaCompiler1('com.github.javaparser:javaparser-core:3.25.8')
    , javaCompiler1SymbolResolver('com.github.javaparser:javaparser-symbol-solver-core:3.25.8')
    ,
    /**
     * see https://commons.apache.org/proper/commons-jci/
     */
    javaCompiler2Janino('org.codehaus.janino:janino:3.1.11')
    , javaCompilerJaninoCommon('org.codehaus.janino:commons-compiler:3.1.11')
    ,javaCompiler3('fr.inria.gforge.spoon:spoon-core:10.4.2')
    , webDavClient('com.github.lookfirst:sardine:5.12')
    ,
    // https://groovy.jfrog.io/ui/native/plugins-release-local/org/codehaus/groovy/groovy-eclipse-batch
    eclipseGroovyBatchCompiler('org.codehaus.groovy:groovy-eclipse-batch:3.0.8-01')
    , icePdfCore('org.icepdf.os:icepdf-core:6.2.2')
    , icePdfViewer('org.icepdf.os:icepdf-viewer:6.2.2')
    // comparision : http://ssh-comparison.quendi.de/comparison/cipher.html
    // http://www.ganymed.ethz.ch/ssh2/
    , j2sshMaverick('com.sshtools:j2ssh-maverick:1.5.5')
    , sshjHierynomus('com.hierynomus:sshj:0.40.0')
    , sshjEddsa("net.i2p.crypto:eddsa:0.3.0")
    ,
    // not much doc
    trileadSsh('com.trilead:trilead-ssh2:1.0.0-build222')
    ,
    groovySshShell('me.bazhenov.groovy-shell:groovy-shell-server:2.2.3')
    , commonsNet('commons-net:commons-net:3.10.0')
    , commonsCollection4('org.apache.commons:commons-collections4:4.4')
    , jsoup('org.jsoup:jsoup:1.18.1')
    , sqlLiteJdbc( 'org.xerial:sqlite-jdbc:3.43.2.1')
    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences#jschDocumentationBinWithSrc
     */
    ,
    @Deprecated
    jcraft('com.jcraft:jsch:0.1.55')
    , jcraftHostFix('com.github.mwiede:jsch:0.2.25')
    , jcraftZlib('com.jcraft:jzlib:1.1.3')
    ,  pty4j('org.jetbrains.pty4j:pty4j:0.12.13')
    , jideOss('com.jidesoft:jide-oss:3.6.18')
    , dnsResolver('dnsjava:dnsjava:3.6.3')
    , jmdns('javax.jmdns:jmdns:3.4.1')
    , // doesn't work on some devices
    @Deprecated
    jcifs('jcifs:jcifs:1.3.17')
    , jcifsAgno3('eu.agno3.jcifs:jcifs-ng:2.1.10')
    , commonsCli('commons-cli:commons-cli:1.4')
    , commonsConfig('commons-configuration:commons-configuration:1.10')
    , jetbrainsAnnotations('org.jetbrains:annotations-java5:16.0.3')
    , mx4j('mx4j:mx4j-tools:3.0.1')
    , cssParser('org.w3c.css:sac:1.3')
    , cssParser2('net.sf.cssbox:jstyleparser:4.0.0')
    , cssParser3('net.sourceforge.cssparser:cssparser:0.9.29')
    , commonsCompress('org.apache.commons:commons-compress:1.27.1')
    , pureJavaComm('com.github.purejavacomm:purejavacomm:1.0.2.RELEASE')
    , networkTestFramework('com.github.netcrusherorg:netcrusher-core:0.10')
    , kryoSerializer('com.esotericsoftware:kryo-shaded:4.0.2')
    , objenesis('org.objenesis:objenesis:2.6')
    , commonsIo(CustObjMavenIds.commonsIo)
    // http://repo1.maven.org/maven2/org/apache/commons/commons-io/1.3.2/commons-io-1.3.2.pom
    //, commonsIoBad('org.apache.commons:commons-io:1.3.2')
    , jasperreports('net.sf.jasperreports:jasperreports:6.18.1')
    ,
    //2.5.0 need java11+john
    quartz('org.quartz-scheduler:quartz:2.4.0')

    ,hutoolAllRandomUtils('cn.hutool:hutool-all:5.8.33')
    ,
    @Deprecated
    osInfo(  'com.github.oshi:oshi-core:6.6.5')
    ,oshiCoreNativeUtils('com.github.oshi:oshi-core:6.6.5')
    //,oshiDemoNativeUtils('com.github.oshi:oshi-demo:6.4.5')
    , winKillProcessTree('org.jvnet.winp:winp:1.28')
    , mslink('com.github.vatbub:mslinks:1.0.5') // next version are java11
    , hamcrest('org.hamcrest:java-hamcrest:2.0.0.0')
    , svnKit('org.tmatesoft.svnkit:svnkit:1.10.11')
    , sequenceLibrary('de.regnis.q.sequence:sequence-library:1.0.4')
    , sqljet('org.tmatesoft.sqljet:sqljet:1.1.15')
    ,
    // add lz4Compressor to classpath
    svnCli('org.tmatesoft.svnkit:svnkit-cli:1.10.11')
    , groovyCodeAnalizer('org.codenarc:CodeNarc:1.5')
    , trove4jIdea('org.jetbrains.intellij.deps:trove4j:1.0.20200330')
    , fernflowerJavaDecompiler('org.jboss.windup.decompiler.fernflower:windup-fernflower:1.0.0.20171018')
    // 5.3 need java9+
    , fernflowerLogger('org.jboss.windup.decompiler:decompiler-fernflower:5.2.1.Final')
    , fernflowerJavaDecompilerApi('org.jboss.windup.decompiler:decompiler-api:5.2.1.Final')
    , cfrJavaDecompilerApi('org.benf:cfr:0.152')
    , fontChooser('io.github.dheid:fontchooser:2.3')
    , opencsv("com.opencsv:opencsv:5.9")
    , powerShellWrapper('com.github.tuupertunut:powershell-lib-java:2.0.0'  )
//    , kotlinStd("org.jetbrains.kotlin:kotlin-stdlib:1.3.31")
//    , jasypt('org.jasypt:jasypt:1.9.2')

    ,
    @Deprecated
    eclipseGitHubApi(MavenIdAndRepoCustom.eclipseGitHubApi.m)
    ,    python('org.python:jython-standalone:2.7.3')
    , args4j('args4j:args4j:2.33')
    , quickfixj('org.quickfixj:quickfixj-all:2.1.1')
    , minaCore('org.apache.mina:mina-core:2.2.3')
    , javaApiCompare('com.github.siom79.japicmp:japicmp:0.15.3')
    ,
    jcabiGithub('com.jcabi:jcabi-github:1.1.2')
    , lz4Compressor('org.lz4:lz4-java:1.8.0')
    ,
    // maven launcher
    plexusClassworlds('org.codehaus.plexus:plexus-classworlds:2.6.0')
    ,plexusSisu( 'org.eclipse.sisu:org.eclipse.sisu.plexus:0.3.3')
    ,plexus_utils('org.codehaus.plexus:plexus-utils:3.4.1')
    ,maven_resolver_provider('org.apache.maven:maven-resolver-provider:3.8.5'),


    // Class org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
    annotationHell('org.springframework.boot:spring-boot-test-autoconfigure:2.2.0.RELEASE')
    ,
    // org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions
    kotlinHell('org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10'),
    ajpClientApacheTomcat('com.github.jrialland:ajpclient:1.11'),
    tomcatJasper('org.apache.tomcat:tomcat-jasper:8.5.23'),
    dockerJibCore('com.google.cloud.tools:jib-core:0.27.1'),
    dockerJibBuildPlan('com.google.cloud.tools:jib-build-plan:0.4.0'),
    gsonClassSerialisation('io.gsonfire:gson-fire:1.8.5'),
    gsonGoogle('com.google.code.gson:gson:2.9.0'),
    charDetector('com.github.albfernandez:juniversalchardet:2.5.0'),
    primitiveCollections('it.unimi.dsi:fastutil:8.5.2'),
    // TODO add connection killer
    oracleJdbc('com.oracle.database.jdbc.debug:ojdbc8_g:21.18.0.0'),
    // qrcode generator
    zxingCore('com.google.zxing:core:3.5.2'),
//    binaryCompatibilityValidator( 'org.jetbrains.kotlinx.binary-compatibility-validator:org.jetbrains.kotlinx.binary-compatibility-validator.gradle.plugin:0.13.2'),
    //below 3 svn stiff needed only for compile
    svnNativeClintWrapper('org.tmatesoft.svnkit:svnkit-javahl16:1.10.4'),
    svnClientAdapterJavahlUseless( 'org.netbeans.external:svnClientAdapter-javahl:RELEASE126'),
    svnClientAdapterMainUseless( 'org.netbeans.external:svnClientAdapter-main:RELEASE126'),
    // also see https://github.com/aragozin/heaplib
    heapAnalizerUi('org.netbeans.modules:org-netbeans-modules-profiler-heapwalker:RELEASE180'),
    // com.intellij.diagnostic.hprof.Analyzer
    // https://github.com/vlsi/mat-calcite-plugin
    heapAnalizerCore('org.netbeans.modules:org-netbeans-lib-profiler:RELEASE180'),
    // org.eclipse.mat.snapshot.SnapshotFactory
    heapAnalizerMat('com.github.cormoran-io.pepper:pepper-mat:2.2'),
    heapDumpRemovePasswords('com.paypal:heap-dump-tool:1.1.6'),
    javaObjectLoyout1('org.openjdk.jol:jol-core:0.17'),
    javaObjectLoyout2("com.github.jbellis:jamm:0.4.0"),
    javaObjectLoyout3("org.ehcache:sizeof:0.4.3"),
    httpdNano('org.nanohttpd:nanohttpd:2.3.1'),

    // math libs : need to check all
    mathCommon3('org.apache.commons:commons-math3:3.6.1'),
    // next is java9+
    mathEjml('org.ejml:ejml-all:0.41'),
    mathOjAlgo('org.ojalgo:ojalgo:51.3.0'),
    //mathNd4j( 'org.nd4j:nd4j:1.0.0-M2'),
    mp4Decoder(  'de.sfuhrm:jaad:0.8.7'),
    videoPlayerForSwing(  'uk.co.caprica:vlcj:4.8.3'),
    // chrome integration into swing, next is java9+
    chromiumJcefMaven(  'me.friwi:jcefmaven:110.0.25'),
    chromiumJcefApi(  'me.friwi:jcef-api:jcef-87476e9+cef-110.0.25+g75b1c96+chromium-110.0.5481.78'),
    cveCheck(  'org.owasp:dependency-check-core:10.0.4'),
    passwordWeakCheck(  'org.passay:passay:1.6.5'),
    // opengl wrapper
    gdx('com.badlogicgames.gdx:gdx:1.13.1'),
    antlrRuntime('org.antlr:antlr-runtime:3.5.3'),

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


    public static MavenCommonUtils mcu = new MavenCommonUtils()


    public
    static List<? extends MavenIdContains> loggingPrefix = [CustObjMavenIds.commonsLoggingMavenId, ]


    public
    static List<? extends MavenIdContains> specific = [GitMavenIds.jgit, junit, CustObjMavenIds.slf4jApi, jnrJffi, portForward, jna, jnaPlatform, gnuGetOpt, jodaTime, //
                                                       CustObjMavenIds.eclipseJavaCompiler, CustObjMavenIds.eclipseJavaAstParser,  jfreeCommon, jfreeChart, CustObjMavenIds.commnonsLang, commonsCollection,//
                                                       commnonsLang3, commnonsText, commonsCodec, groovySshShell, javaCompiler1, javaCompiler1SymbolResolver,icePdfCore, icePdfViewer, j2sshMaverick, commonsNet, commonsCollection4,//
                                                        jsoup, jcraftHostFix, httpClient, httpCore,httpCoreNio, jideOss, jmdns,  mx4j, trileadSsh, cssParser, commonsHttpOld, cssParser3, CustObjMavenIds.compressAbstraction,//
                                                       commonsCompress, jasperreports, quartz, winKillProcessTree, svnKit, fernflowerJavaDecompiler, fernflowerLogger, fontChooser, opencsv,svnCli,
                                                       powerShellWrapper,zxingCore,apacheOro,
    ]


    public
    static List<? extends MavenIdContains> usefulMavenIdSafeToUseLatest = DropshipClasspath.allLibsWithoutGroovy + loggingPrefix + (List) AntMavenIds.all +  (List) specific + (List) CustObjMavenIds.all


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
            case { mavenId.isGroupAndArtifact(new MavenId('org.apache.commons:commons-io:1.3.2')) }:
                commonsIo
            case { mavenId.isGroupAndArtifact(guavaMavenIdNew) }:
                return guavaMavenIdNew
            case { lastestGrousp.contains(group) }:
                return mcu.findLatestMavenOrGradleVersion2(mavenId)
            default:
                return mavenId;
        }
    }

}
