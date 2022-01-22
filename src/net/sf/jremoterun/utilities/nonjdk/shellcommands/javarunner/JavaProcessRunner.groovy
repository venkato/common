package net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptionsNotStd2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import org.apache.commons.lang3.SystemUtils

import java.lang.reflect.Field
import java.util.logging.Logger

//TODO add debug option like : -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:1166,suspend=y,server=n
/**
 * https://jacoline.dev/inspect
 */
@CompileStatic
class JavaProcessRunner extends NativeCommand {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Deprecated
    public static final String allUnnamedModule = 'ALL-UNNAMED';
    @Deprecated
    public static final String addAllUnnamedModule = net.sf.jremoterun.utilities.nonjdk.shellcommands.JavaOptions.openJavaBase;
    public static final int javaUsed = 2;
    public static final int javawUsed = 3;
    public static String javaBinaryDefault = 'java';
    public static List<String> javaArgsDefault = [];
    public static List<File> javaClasspathDefault = [];
    public static ClRef groovyMainRunner = new ClRef(groovy.ui.GroovyMain);

    public JavaCmdOptionsNotStd2 cmdOptionsNotStd2 = new JavaCmdOptionsNotStd2()
    public String pathSeparator = File.pathSeparator;
    Properties javaProps = new Properties();
//    String javaBinary = javaBinaryDefault;
    List<String> javaArgs = new ArrayList<>(javaArgsDefault);
    List<String> javaMainArgs = [];
    HashSet<String> xxAdd = new HashSet<>();
    HashSet<String> xxMinus = new HashSet<>();
    Map<String, String> xxSet = [:];
    File jarRunner;
    int xMxInMg = -1;
    ClRefRef mainClass;

    AddFileToClassloaderDummy javaClasspath = new AddFileToClassloaderDummy();
    AddFileToClassloaderDummy bootClasspath = new AddFileToClassloaderDummy();
    String bootClasspathSuffix = '';

    MbeanConnectionCreator mbeanConnectionCreator;

    JavaProcessRunner() {
        super(javaBinaryDefault, [])
    }

    JavaProcessRunner(File javaPath) {
        super(javaPath, [])
    }

    void init() {
        javaClasspath.isLogFileAlreadyAdded = false
        if (javaClasspathDefault.size() > 0) {
            javaClasspath.addAll javaClasspathDefault
        }
    }


    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        buildCmd()
    }

    void parseNotStdProps() {
        Class clazz = cmdOptionsNotStd2.getClass()
        Field[] fields = clazz.getFields()
        fields.toList().each {
            if ((!it.getName().startsWith('__')) && it.getDeclaringClass() != Object && it.getDeclaringClass() != GroovyObject) {
                parserNonStdField(it)
            }
        }
    }

    void parserNonStdField(Field f) {
        Object get1 = f.get(cmdOptionsNotStd2)
        if (get1 != null) {
            String name123 = f.getName()
            if (f.getType() == Boolean) {
                if (get1 == true) {
                    xxAdd.add(name123)
                } else {
                    xxMinus.add(name123)
                }
            } else {
                xxSet.put(name123, get1.toString())
            }
        }
    }

    void enableDebugMode1(int port, boolean suspend) {
        String suspend2 = suspend ? 'y' : 'n'
        // for java 9+
        // -agentlib:jdwp=transport=dt_socket,server=y,address=*:8000,suspend=n
        javaArgs.add net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.JavaAgentOptionBuilder.enableDebugMode1(port,suspend)
    }

    void enableDebugMode2(int port, boolean suspend) {
        String suspend2 = suspend ? 'y' : 'n'
        javaArgs.add JavaCmdOptions.Xdebug.customName
//        javaArgs.add '-Xdebug'
        javaArgs.add net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.JavaAgentOptionBuilder.enableDebugMode2(port,suspend)
//        javaArgs.add "-Xrunjdwp:server=y,transport=dt_socket,address=${port},suspend=${suspend2}".toString()
    }

    void buildCmd() {
        parseNotStdProps()
        xxAdd.each {
            assert !xxMinus.contains(it)
            assert !xxSet.containsKey(it)
        }
        xxMinus.each {
            assert !xxAdd.contains(it)
            assert !xxSet.containsKey(it)
        }
        xxSet.keySet().each {
            assert !xxAdd.contains(it)
            assert !xxMinus.contains(it)
        }
//        fullCmd.add javaBinary
        fullCmd.addAll xxAdd.collect { "-XX:+${it}".toString() }
        fullCmd.addAll xxMinus.collect { "-XX:-${it}".toString() }
        fullCmd.addAll xxSet.collect { "-XX:${it.key}=${it.value}".toString() }
        fullCmd.addAll javaArgs
        if (xMxInMg > 0) {
            String xmxAlreadyAdded = javaArgs.find { it.startsWith('-Xmx') }
            if (xmxAlreadyAdded != null) {
                throw new Exception("-Xmx was added in another way : ${xmxAlreadyAdded} vs ${xMxInMg}m")
            }
            fullCmd.add("-Xmx${xMxInMg}m".toString())
        }
        fullCmd.addAll javaProps.collect { "-D${it.key}=${it.value}".toString() }
        if (javaClasspath.addedFilesWithOrder.size() > 0) {
            javaClassPathBuilt = buildClassPath(javaClasspath, pathSeparator)
            if (addJavaClasspathAsArg) {
                fullCmd.add JavaCmdOptions.classpath.customName
                fullCmd.add javaClassPathBuilt
            }
        }
        if (bootClasspath.addedFilesWithOrder.size() > 0) {
            String bootCp = JavaCmdOptions.Xbootclasspath.customName + bootClasspathSuffix + ':' + buildClassPath(bootClasspath, pathSeparator)
            fullCmd.add bootCp
        }
        if (jarRunner == null) {
            assert mainClass != null
            fullCmd.add mainClass.clRef.className
        } else {
            assert mainClass == null
            fullCmd.add '-jar'
            fullCmd.add jarRunner.getAbsoluteFile().getCanonicalFile().getAbsolutePathUnix()
        }

        fullCmd.addAll javaMainArgs
    }

    public boolean addJavaClasspathAsArg = true
    public String javaClassPathBuilt;


    /**
     * https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/run.htm
     */
    void recordEvents(int durationInSec, File fileName) {
        cmdOptionsNotStd2.UnlockCommercialFeatures = true;
        cmdOptionsNotStd2.FlightRecorder = true
        cmdOptionsNotStd2.StartFlightRecording = "duration=${durationInSec},filename=${fileName}"
    }

    void logGc(File file) {
        File parentFile = file.getParentFile()
        if (!parentFile.exists()) {
            throw new FileNotFoundException("parent path not found : ${parentFile}")
        }
        javaArgs.add "${JavaCmdOptions.Xloggc.customName}:${file}".toString()
    }

    @Deprecated
    void setHeapDumpPath(File pathToHeapDump) {
        File parentFile = pathToHeapDump.getParentFile()
        if (!parentFile.exists()) {
            throw new FileNotFoundException("heap dump parent path not found : ${parentFile}")
        }
        cmdOptionsNotStd2.HeapDumpPath = pathToHeapDump
        //javaArgs.add "-XX:${JavaCmdOptionsNonStd.HeapDumpPath}=${pathToHeapDump.getAbsolutePath()}".toString()
    }

    void addJmxOpts() {
        javaProps.setProperty(PropsEnum.com_sun_management_jmxremote.customName, 'true')
        javaProps.setProperty(PropsEnum.com_sun_management_jmxremote_ssl.customName, 'false')
        javaProps.setProperty(PropsEnum.com_sun_management_jmxremote_authenticate.customName, 'false')
    }

    void setJrrConfig2Dir(File dir) {
        assert dir.exists()
        javaProps.setProperty(JrrStarterConstatnts.jrrConfig2DirSystemProperty, dir.getAbsolutePath())
    }


    void addJmxOpts2(int port) {
        javaProps.setProperty(PropsEnum.com_sun_management_jmxremote_port.customName, "${port}")
        addJmxOpts()
        mbeanConnectionCreator = new MbeanConnectionCreator(port)
    }

    void setJrrRunner2(int type) {
        if (SystemUtils.IS_OS_WINDOWS) {
            if (type in [javaUsed, javawUsed]) {

            } else {
                throw new IllegalStateException("Invalid type : ${type}, allowed : ${javaUsed} or ${javawUsed}")
            }
        } else {
            type = javaUsed
        }
        setJrrRunner6(type)
    }

    void setJrrRunner6(int type) {
        setJrrRunner7(type, JrrStarterOsSpecificFiles.copyDir.calcGitRef(), JrrStarterOsSpecificFilesFirstDownload.groovyrunner_dot_groovy.calcGitRef())
    }

    void setJrrRunner(int runnerType, File jrrStarterLibsDir, File groovyRunnerScript) {
        setJrrRunner7(runnerType, new FileToFileRef(jrrStarterLibsDir), new FileToFileRef(groovyRunnerScript))
    }

    void setJrrRunner7(int runnerType, File2FileRefWithSupportI jrrStarterLibsDir, ToFileRef2 groovyRunnerScript) {
        // for runnerType see
        new ClRef('net.sf.jremoterun.utilities.init.commonrunner.JrrRunnerProperties')
        File groovyRunnerScript2 = groovyRunnerScript.resolveToFile()
        assert groovyRunnerScript2.isFile()
        javaArgs.add JavaAgentOptionBuilder.createAgentArg(JavaCmdOptions.javaagent, jrrStarterLibsDir.childL(JrrStarterJarRefs2.jremoterun.getJarName()))
        javaArgs.add JavaAgentOptionBuilder.createAgentArg(JavaCmdOptions.javaagent, jrrStarterLibsDir.childL(JrrStarterJarRefs2.java11base.getJarName()))
        javaClasspath.add jrrStarterLibsDir.childL(JrrStarterJarRefs2.groovy_custom.getJarName())
        javaClasspath.add jrrStarterLibsDir.childL(JrrStarterJarRefs2.groovy.getJarName())
        mainClass = groovyMainRunner
        javaMainArgs.add(0, groovyRunnerScript2.getAbsolutePath())
        javaMainArgs.add(1, runnerType as String)
    }


    void addJvmAgentS(JrrStarterJarRefs2 ref) {
        javaArgs.add JavaAgentOptionBuilder.createJvmAgentS(ref)
    }


}
