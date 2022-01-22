package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.nativeprocess.NativeProcessResult

import java.util.logging.Logger

@CompileStatic
class ShellCommands {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static NativeCommand grep(String searchText, File file) {
        assert file.exists()
        return new NativeCommand('grep', [searchText, file.getAbsolutePath()]);
    }

    static NativeCommand gunzip(File f) {
        assert f.exists()
        return new NativeCommand('gunzip', ['-f', f.getAbsolutePath()]);

    }

}
