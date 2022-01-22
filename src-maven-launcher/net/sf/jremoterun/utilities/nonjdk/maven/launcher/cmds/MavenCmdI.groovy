package net.sf.jremoterun.utilities.nonjdk.maven.launcher.cmds

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface MavenCmdI {


    String getAcronym();

    void addMe()

}
