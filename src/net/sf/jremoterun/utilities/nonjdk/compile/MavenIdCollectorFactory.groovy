package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.MavenIdsCollector;

import java.util.logging.Logger;

@CompileStatic
class MavenIdCollectorFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static MavenIdCollectorFactory factory1 = new MavenIdCollectorFactory()

    MavenIdsCollector getCollector(){
        return new MavenIdsCollector();
    }



}
