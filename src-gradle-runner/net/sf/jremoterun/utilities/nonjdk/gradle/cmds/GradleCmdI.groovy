package net.sf.jremoterun.utilities.nonjdk.gradle.cmds
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface GradleCmdI {


    String getAcronym();

    void addMe()

}
