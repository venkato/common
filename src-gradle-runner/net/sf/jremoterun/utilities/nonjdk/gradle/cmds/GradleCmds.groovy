package net.sf.jremoterun.utilities.nonjdk.gradle.cmds

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleEnvsUnsafe
import org.gradle.StartParameter

import java.util.logging.Logger

/**
 * gradle.properties:
 * org.gradle.java.home=pathToJava
 */
@CompileStatic
class GradleCmds {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathDumperGradleCmd dmps = new ClassPathDumperGradleCmd();

    public Map<String,GradleCmdI> cmndsMap =[:];

    String cmdPrefix = net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleConstants.cmdPrefix

    void addCmds() {
        StartParameter parameter1 = GradleEnvsUnsafe.gradle.getStartParameter()
        Map<String, String> args3 = parameter1.getSystemPropertiesArgs()
        Map<String,String> cmnds = args3.findAll {
            it.key.startsWith(cmdPrefix)
        }

        if(cmnds.size()>0){
            initCmds()
            List cmnds2 = cmnds.keySet().collect {it.substring(cmdPrefix.length())}
            cmnds2.each {

                GradleCmdI geti = cmndsMap.get(it)
                if(geti==null){
                    throw new Exception("failed find cmd ${it}")
                }
                geti.addMe()
            }
        }

    }

    void addCMd(GradleCmdI i){
        cmndsMap.put(i.getAcronym(),i)
    }


    void initCmds(){
        addCMd(dmps);
    }
}
