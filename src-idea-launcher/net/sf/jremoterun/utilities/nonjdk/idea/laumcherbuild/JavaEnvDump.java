package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class JavaEnvDump {

    public static char sep = '\n';

    public static void main(String[] args) {
        dumpEnv1();
    }


    public static void dumpEnv1() {
        System.out.println(new JavaEnvDump().collectData());
    }


    public StringBuilder sb = new StringBuilder();

    public String collectData() {
        buildData();
        return sb.toString();
    }

    public void buildData() {
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = compilationMXBean.getInputArguments();
        String name = compilationMXBean.getName();
        int i = name.indexOf('@');
        if (i == -1) {
            addLine("Can't detect pid in :" + name);
        } else {
            String substring = name.substring(0, i);
            addLine("pid=" + substring);
        }
        dumpProp("java.home");
        dumpProp("user.dir");
        dumpProp("java.class.path");
        addLine("inputArguments=" + inputArguments);
    }


    void dumpProp(String propName) {
        String javaHome = System.getProperty(propName);
        addLine(propName + '=' + javaHome);
    }

    void addLine(String line) {
        sb.append(line).append(sep);
    }


}
