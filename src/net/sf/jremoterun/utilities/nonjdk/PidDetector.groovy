package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.util.logging.Logger

@CompileStatic
class PidDetector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static String cmdLineProp = PropsEnum.sun_java_command.customName

    @Deprecated
    public static String javaNativeAgentProp = JavaCmdOptions.agentpath.customName+':'

    static void printPid(){
        int pid1 = detectPid()
        log.info "pid = ${pid1}"
    }

    static int detectPid(){
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        String name = compilationMXBean.getName()
        int i = name.indexOf('@')
        if(i==-1){
            throw new Exception("Can't detect pid in : ${name}")
        }
        String substring = name.substring(0, i)
        return Integer.parseInt(substring)
    }

    static List<String> getCommandLine2(){
        return getCommandLine().tokenize(' ')
    }

    static String getCommandLine(){
        String cmdLine = PropsEnum.sun_java_command.getValue()
        if(cmdLine==null){
            throw new IllegalStateException("prop not found : ${PropsEnum.sun_java_command.customName}")
        }
        return cmdLine
    }

    static List<File> getAgents2(String agent){
        return getAgents(agent).collect {it as File}
    }

    static List<File> getNativeAgents3(){
        return getAgents4(JavaCmdOptions.agentpath)
    }

    static List<File> getAgents4(JavaCmdOptions agent){
        return getAgents2(agent.customName+':')
    }

    static List<String> getAgents(String agent){
        RuntimeMXBean compilationMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = compilationMXBean.getInputArguments()
        List<String> agentProps = inputArguments.findAll {it.startsWith(agent)}
        List<String> result = agentProps.collect { it.substring(agent.length()) }
        return result
    }

    static List<String> getInputArgs(){
        return ManagementFactory.getRuntimeMXBean().getInputArguments()
    }

}
