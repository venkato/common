package net.sf.jremoterun.utilities.nonjdk.classpath.refs2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.downloadutils.IffZipRefs
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink

import java.util.logging.Logger

@CompileStatic
class CutomJarAdd1 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();







    static File downloadIdw() {
        File file = IffZipRefs.idwDockingUrl3.childL("idw-gpl-1.6.1/lib/idw-gpl.jar").resolveToFile()
        return file
    }


}
