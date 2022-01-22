package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrArchiveSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String archivePath = 'META-INF/info.groovy';

}
