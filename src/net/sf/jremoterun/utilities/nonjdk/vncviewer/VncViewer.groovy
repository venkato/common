package net.sf.jremoterun.utilities.nonjdk.vncviewer;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.MainMethodRunner
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UrlRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.IffUrlRefs
import net.sf.jremoterun.utilities.nonjdk.downloadutils.IffZipRefs
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class VncViewer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static URL urlSrc = new URL("https://www.tightvnc.com/download/2.8.3/tvnjviewer-2.8.3-src-gnugpl.zip")

//    public static UrlRef url = new UrlRef(new URL("https://www.tightvnc.com/download/2.8.3/tvnjviewer-2.8.3-bin-gnugpl.zip"))

    public static ClRef cnr = new ClRef("com.glavsoft.viewer.Viewer")

    static File downloadJar(){
        File jarFile = IffZipRefs.vncviewer.childL("nossh_custom/tightvnc-jviewer.jar").resolveToFile()
        assert jarFile.exists()
        return jarFile
    }

//    static void downloadAndRun(){
//        File file = download()
//
//    }


    static void run(List<String> args){
        MainMethodRunner.run(cnr,args)
    }


}
