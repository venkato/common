package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptionsNonStd
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptionsNotStd2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import org.apache.commons.lang3.SystemUtils

import java.lang.reflect.Field
import java.util.logging.Logger

//TODO add debug option like : -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:1166,suspend=y,server=n
@CompileStatic
class JavaProcessRunner extends NativeCommand{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static final int javaUsed = 2;
    public static final int javawUsed = 3;
    public static String javaBinaryDefault = 'java';
    public static List<String> javaArgsDefault = [];
    public static List<File> javaClasspathDefault = [];
    public static ClRef groovyMainRunner = new ClRef(groovy.ui.GroovyMain);

    public JavaCmdOptionsNotStd2 cmdOptionsNotStd2 = new JavaCmdOptionsNotStd2()
    Properties javaProps = new Properties();
    String javaBinary = javaBinaryDefault;
    List<String> javaArgs = new ArrayList<>(javaArgsDefault);
    List<String> javaMainArgs = [];
    HashSet<String> xxAdd = new HashSet<>();
    HashSet<String> xxMinus = new HashSet<>();
    Map<String, String> xxSet = [:];
    int xMxInMg = -1;
    ClRefRef mainClass;
    //List<String> fullCmd = []
//    Map<String, String> envHuman = [:];
    //List<String> env = [];
    //NativeProcessResult process = new NativeProcessResult();
    //Date startTime;

    AddFileToClassloaderDummy javaClasspath = new AddFileToClassloaderDummy();
    AddFileToClassloaderDummy bootClasspath = new AddFileToClassloaderDummy();
    String bootClasspathSuffix = '';

    MbeanConnectionCreator mbeanConnectionCreator;

    JavaProcessRunner() {
        super(javaBinaryDefault,[])
    }

    void init() {
        javaClasspath.isLogFileAlreadyAdded = false
        if (javaClasspathDefault.size() > 0) {
            javaClasspath.addAll javaClasspathDefault
        }
        File javaHome = SystemUtils.getJavaHome()
        assert javaHome.exists()
        File javaExec = javaHome.child('bin/java')
        if (!javaExec.exists()) {
            javaExec = new File(javaExec.getAbsolutePath() + '.exe')
        }
        assert javaExec.exists()
        javaBinary = javaExec.absolutePath
    }

//    void checkNoOptionInJavaArgs(String optionName) {
//        String find1 = javaArgs.find { it.contains(optionName) }
//        if (find1 != null) {
//            throw new Exception("dup option ${optionName} ${find1}")
//        }
//    }
    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        buildCmd()
    }

    void parseNotStdProps(){
        Class clazz = cmdOptionsNotStd2.getClass()
        Field[] fields = clazz.getFields()
        fields.toList().each {
            if( (!it.getName().startsWith('__')) && it.getDeclaringClass() != Object && it.getDeclaringClass() != GroovyObject) {
                parserNonStdField(it)
            }
        }
    }

    void parserNonStdField(Field f){
        Object get1 = f.get(cmdOptionsNotStd2)
        if(get1!=null){
            String name123 = f.getName()
            if(f.getType() == Boolean){
                if(get1 == true){
                    xxAdd.add(name123)
                }else{
                    xxMinus.add(name123)
                }
            }else{
                xxSet.put(name123,get1.toString())
            }
        }
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
                throw new Exception("-Xmx was added in another way : ${xmxAlreadyAdded} ")
            }
            fullCmd.add("-Xmx${xMxInMg}m".toString())
        }
        fullCmd.addAll javaProps.collect { "-D${it.key}=${it.value}".toString() }
        if (javaClasspath.addedFilesWithOrder.size() > 0) {
            fullCmd.add JavaCmdOptions.classpath.customName
            fullCmd.add buildClassPath(javaClasspath,File.pathSeparator)
        }
        if (bootClasspath.addedFilesWithOrder.size() > 0) {
            String bootCp = JavaCmdOptions.Xbootclasspath.customName + bootClasspathSuffix + ':' + buildClassPath(bootClasspath,File.pathSeparator)
            fullCmd.add bootCp
        }
        fullCmd.add mainClass.clRef.className
        fullCmd.addAll javaMainArgs
    }

    /**
     * https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/run.htm
     */
    void recordEvents(int durationInSec,File fileName){
        cmdOptionsNotStd2.UnlockCommercialFeatures = true;
        cmdOptionsNotStd2.FlightRecorder = true
        cmdOptionsNotStd2.StartFlightRecording = "duration=${durationInSec},filename=${fileName}"
    }

    void logGc(File file){
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
                throw new IllegalStateException("Invalid type : ${type}, allowed : 2 or 3")
            }
        } else {
            type = javaUsed
        }
        File jrrStarterLibsDir = JrrStarterJarRefs.groovyClasspathDir.resolveToFile()
        setJrrRunner(type, jrrStarterLibsDir, JrrStarterJarRefs.groovyRunner.resolveToFile())
    }

    void setJrrRunner(int runnerType, File jrrStarterLibsDir, File groovyRunnerScript) {
        // for runnerType see
        new ClRef('net.sf.jremoterun.utilities.init.commonrunner.JrrRunnerProperties')
        assert groovyRunnerScript.isFile()
        File jrrFile = jrrStarterLibsDir.child(JrrStarterJarRefs2.jremoterun.getJarName())
        assert jrrFile.exists()
        javaArgs.add "${JavaCmdOptions.javaagent.customName}:${jrrFile.getAbsolutePath()}".toString()
        javaClasspath.addF jrrStarterLibsDir.child(JrrStarterJarRefs2.groovy_custom.getJarName())
        javaClasspath.addF jrrStarterLibsDir.child(JrrStarterJarRefs2.groovy.getJarName())
        mainClass = groovyMainRunner
        javaMainArgs.add(0, groovyRunnerScript.getAbsolutePath())
        javaMainArgs.add(1, runnerType as String)
    }



}
