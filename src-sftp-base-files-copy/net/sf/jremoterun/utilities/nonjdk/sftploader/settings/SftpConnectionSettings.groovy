package net.sf.jremoterun.utilities.nonjdk.sftploader.settings

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.sftploader.CopyAndUnpack
import net.sf.jremoterun.utilities.nonjdk.sftploader.SftpLoader;

import java.util.logging.Logger;

@CompileStatic
class SftpConnectionSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static SftpConnectionSettings settings

    String host;
    String user='jrrinit';
    int port;
    ClRef clRefAfterSshDownload;
    boolean doLogging = false


    File originDir
    File copyDir
    SftpLoader sftpLoader
    CopyAndUnpack copyAndUnpack
}
