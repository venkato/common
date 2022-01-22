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

    public String host;
    public String user='jrrinit';
    public String password
    public int port;
    public ClRef clRefAfterSshDownload;
    public boolean doLogging = false


    public File originDir
    public File copyDir
    public SftpLoader sftpLoader
    public CopyAndUnpack copyAndUnpack

    public List<String> unzipedEntries = [];
}
