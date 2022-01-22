package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommandBuilder

@CompileStatic
public class OpenTwoFilesTotalCmdHandler extends  NativeCommandBuilder  {


    OpenTwoFilesTotalCmdHandler(File totalCmd) {
        super([totalCmd.getAbsolutePath()])
        fullCmd.add('/O')
        consumeOutStreamSysoutF = true
    }

    NativeCommand openFiles(File left,File right) {
        assert left.exists()
        assert right.exists()
        NativeCommand args = buildCustomArgs3(["/L=${left.getAbsolutePath()}".toString(),"/R=${right.getAbsolutePath()}".toString()])
        return args
    }
}
