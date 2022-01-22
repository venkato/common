package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

// show last 10 line can be done via std java
@CompileStatic
class DiskUsageCommand extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String arg;
    boolean summary = false;



    DiskUsageCommand(String cmd1,String file) {
        super(cmd1, [])
        this.arg = file
    }

    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        if(summary){
            fullCmd.add('-s')
        }
        fullCmd.add(arg)

    }
}
