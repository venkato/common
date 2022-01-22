package net.sf.jremoterun.utilities.nonjdk.maven.launcher.cmds

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper;

import java.util.logging.Logger;

@CompileStatic
class MavenCmds {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathDumperCmd dmps = new ClassPathDumperCmd();

    public Map<String,MavenCmdI> cmndsMap =[:];

    String cmdPrefix = '-Djrr'

    void addCmds() {
        List<String> args1 = JrrMavenCliWrapper.args1
        List<String> cmnds = args1.findAll {
            it.startsWith(cmdPrefix)
        }

        if(cmnds.size()>0){
            initCmds()
            cmnds = cmnds.collect {it.substring(cmdPrefix.length())}
            cmnds.each {

                MavenCmdI geti = cmndsMap.get(it)
                if(geti==null){
                    throw new Exception("failed find cmd ${it}")
                }
                geti.addMe()
            }
        }

    }

    void addCMd(MavenCmdI i){
        cmndsMap.put(i.getAcronym(),i)
    }


    void initCmds(){
        addCMd(dmps);
    }
}
