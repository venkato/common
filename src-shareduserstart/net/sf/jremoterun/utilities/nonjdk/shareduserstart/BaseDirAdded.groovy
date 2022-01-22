package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class BaseDirAdded {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static File buildFile(SharedFolderSettingsEnum s) {
        assert SharedFolderSettingsGeneral.baseDir != null
        return new File(SharedFolderSettingsGeneral.baseDir, s.childPath)
    }
}
