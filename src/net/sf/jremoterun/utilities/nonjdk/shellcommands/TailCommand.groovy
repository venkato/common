package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

// show last 10 line can be done via std java
@CompileStatic
class TailCommand extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File file;

    boolean watchLive = true;


    TailCommand(File file) {
        super('tail', [])
        assert file.exists()
        this.file = file
    }

    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        if(watchLive) {
            fullCmd.add('-f')
        }
        fullCmd.add(file.getAbsolutePath())

    }
}
