package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand

@CompileStatic
public interface OpenFileHandler {

    abstract NativeCommand openFile(File file)


}
