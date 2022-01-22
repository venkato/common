package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class IdeaBuilderEnvDump {

    public static char sep ='\n';

    public static void dumpEnv(){
        System.out.println(collectData());
    }

    public static String collectData(){
        StringBuilder sb=new StringBuilder();
        String javaHome= System.getProperty("java.home");
        String userDir= System.getProperty("user.dir");
        String classPath= System.getProperty("java.class.path");
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = compilationMXBean.getInputArguments();
        String name = compilationMXBean.getName();
        int i = name.indexOf('@');
        if(i==-1){
            sb.append("Can't detect pid in :"+name);
        }else {
            String substring = name.substring(0, i);
            sb.append("pid="+substring);
        }
        sb.append(sep);
        dumpProp("java.home",sb);
        dumpProp("user.dir",sb);
        dumpProp("java.class.path",sb);
        sb.append("inputArguments=").append(inputArguments).append(sep);
        sb.append("mainArguments=").append(IdeaBuildRunnerSettings.argsPv2).append(sep);
        return sb.toString();
    }


    static void dumpProp(String propName,StringBuilder sb){
        String javaHome= System.getProperty(propName);
        sb.append(propName).append('=').append(javaHome).append(sep);
    }
}
