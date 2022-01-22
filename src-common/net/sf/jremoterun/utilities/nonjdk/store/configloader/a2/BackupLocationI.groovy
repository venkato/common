package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
interface BackupLocationI {

    File getLatestBackupedFile()

    void backupFile(File latestFile)

    ToFileRef2 getSaveStatDir()

}
