package net.sf.jremoterun.utilities.nonjdk.downloadutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UrlRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.IffUrlRefs

import java.util.logging.Logger


@CompileStatic
class LessDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


//    private static UrlRef url = new UrlRef(new URL("https://steve.fi/Software/less/less-332.zip"))

    public static FileChildLazyRef lessCycle = GitSomeRefs.sshConsole.childL("lesscycle.bat")

    // https://sourceforge.net/projects/gnuwin32/files/less/394/  -  doesn't work
    public static FileChildLazyRef lessExec = IffZipRefs.lessExec.childL("less-332/Binaries/less.exe")


    static volatile File lessCmd

    public static File lessViewer;


    static FileChildLazyRef getWinLessViewer(){
        return lessCycle
    }


    private static File downloadLess() {
        if (lessCmd==null) {
            lessCmd=init2();
        } else {
        }
        return lessCmd
    }

    private static File init2() {
        //        File unzip = UrlDownloadUtils3.getUrlDownloadUtils().downloadUrlAndUnzip(url)
        File lessCmd = lessExec.resolveToFile()
        assert lessCmd.exists()
        return lessCmd
    }


}
