package idea.plugins.thirdparty.filecompletion.share.Ideasettings

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner.JavaAgentOptionBuilder
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions


@CompileStatic
class IdeaJavaRunner2Settings {


    public static File baseDir

    public static File libs
    public static File runners
    public static File jars

    public static volatile boolean suggestGrodupMavenIds = true

    public static List<String> jvmOptions = []
    public static List<String> jvmJava8Options = []
    public static List<String> jvmJava11Options = [net.sf.jremoterun.utilities.nonjdk.shellcommands.JavaOptions.openJavaBase]

    static void initIfExist(){
        if(SettingsRef.config.libBaseDir==null) {
        }else{
            init3()

        }
    }

    static void init3(){
        assert SettingsRef.config.libBaseDir!=null
        setBaseDir(SettingsRef.config.libBaseDir)
    }

    @Deprecated
    static void setBaseDir(File baseDir3){
        baseDir = baseDir3
        libs = new File(baseDir3 ,'libraries')
        runners = new File(baseDir3 ,'perrunner')
        jars = new File(baseDir3 ,'jars')

    }

    static String buildJavaAgentPath(){
        return JavaAgentOptionBuilder. createAgentArg(JavaCmdOptions.javaagent,JrrStarterJarRefs2.jremoterun)

    }

    static String buildJava11ModuleDisable(){
        return JavaAgentOptionBuilder. createAgentArg(JavaCmdOptions.javaagent,JrrStarterJarRefs2.java11base)

    }

    static List<String> createDefaultArgs() {
        List<String> list1 = [
                buildJavaAgentPath(),
                buildJava11ModuleDisable(),
                net.sf.jremoterun.utilities.nonjdk.classpath.refs.nonclasses.NpeFixAgent.buildPathString(),
        ]
        return list1
    }

}
