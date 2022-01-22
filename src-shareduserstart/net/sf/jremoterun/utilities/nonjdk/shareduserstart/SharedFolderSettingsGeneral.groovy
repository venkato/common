package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.FileScriptSource;

import java.util.logging.Logger;

@CompileStatic
class SharedFolderSettingsGeneral {


    public static File baseDir
    public static ClRef runAfter1
    public static ClRef runAfter2
    public static ClRef runAfter3
    public static String addiffS = 'addiff'
    public static boolean doDebugLogging = false
    public static FileScriptSource callerInfo;
}
