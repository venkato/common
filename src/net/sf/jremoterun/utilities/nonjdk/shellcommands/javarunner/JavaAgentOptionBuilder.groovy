package net.sf.jremoterun.utilities.nonjdk.shellcommands.javarunner

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions;

import java.util.logging.Logger;

@CompileStatic
class JavaAgentOptionBuilder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String createJvmAgentS(ToFileRef2 file) {
        return createAgentArg(JavaCmdOptions.javaagent, file);
    }

    static String createAgentArg(JavaCmdOptions cmdOption, ToFileRef2 file) {
        return createAgentArg(cmdOption, file.resolveToFile())
    }

    static String createAgentArg(JavaCmdOptions cmdOption, File file) {
        JrrUtilitiesFile.checkFileExist(file)
        boolean endWithJar = file.getName().endsWith('.jar')
        if (cmdOption == JavaCmdOptions.javaagent) {
            if (!endWithJar) {
                throw new IllegalArgumentException('need jar file : ' + file.getAbsolutePath())
            }
        }
        if (cmdOption == JavaCmdOptions.agentpath) {
            if (endWithJar) {
                throw new IllegalArgumentException('need native lib : ' + file.getAbsolutePath())
            }
        }
        String replace1 = file.getAbsolutePath().replace('\\', '/')
        return "${cmdOption.customName}:${replace1}".toString()
    }


    static String enableDebugMode1(int port, boolean suspend) {
        String suspend2 = suspend ? 'y' : 'n'
        // for java 9+
        // -agentlib:jdwp=transport=dt_socket,server=y,address=*:8000,suspend=n
        return "${JavaCmdOptions.agentlib.customName}:jdwp=transport=dt_socket,server=y,suspend=${suspend2},address=${port}"
    }

    static String enableDebugMode2(int port, boolean suspend) {
        String suspend2 = suspend ? 'y' : 'n'
        return "${JavaCmdOptions.Xrunjdwp.customName}:server=y,transport=dt_socket,address=${port},suspend=${suspend2}"
//        javaArgs.add "-Xrunjdwp:server=y,transport=dt_socket,address=${port},suspend=${suspend2}".toString()
    }


}
