package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommandBuilder

@CompileStatic
public class OpenFileTotalCmdHandler extends  NativeCommandBuilder implements OpenFileHandler {


    OpenFileTotalCmdHandler(File totalCmd) {
        super([totalCmd.getAbsolutePath()])
        fullCmd.add('/O')
        consumeOutStreamSysoutF = true
    }

    @Override
    void openFile(File file) {
        assert file.exists()
        NativeCommand args = buildCustomArgs2(file.getAbsolutePath())
        //return args
    }
}
