package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
class NetStatCommand extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean isResolveHost = false;
    boolean showAllPorts = false;
    boolean showPid = false;

    NetStatCommand(File file) {
        super('netstat', [])
        assert file.exists()
    }

    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        if (!isResolveHost) {
            fullCmd.add('-n')
        }
        if (showAllPorts) {
            fullCmd.add('-a')
        }

        if (showPid) {
            if(SystemUtils.IS_OS_WINDOWS) {
                fullCmd.add('-o')
            }else {
                fullCmd.add('-p')
            }
        }

    }
}
