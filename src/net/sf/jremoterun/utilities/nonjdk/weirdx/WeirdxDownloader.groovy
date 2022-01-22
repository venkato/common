package net.sf.jremoterun.utilities.nonjdk.weirdx;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.MainMethodRunner
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.BinaryWithSource3
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UrlRef
import net.sf.jremoterun.utilities.nonjdk.downloadutils.IffZipRefs
import net.sf.jremoterun.utilities.nonjdk.downloadutils.UrlDownloadUtils3
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;

/**
 * https://github.com/notvelleda/enderX
 * http://www.jcraft.com/jdxpc/index.html
 * https://github.com/wrey75/Lynx/blob/master/INSTALL
 * https://packages.debian.org/buster/all/weirdx/download
 * https://salsa.debian.org/java-team/weirdx
 */
@CompileStatic
class WeirdxDownloader {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static SfLink zipUrl2 = new SfLink("weirdx/WeirdX/1.0.32/weirdx-1.0.32.zip");

//    @Deprecated
//    public static UrlRef zipUrl = new UrlRef (new URL("https://10gbps-io.dl.sourceforge.net/project/weirdx/WeirdX/1.0.32/weirdx-1.0.32.zip"));

//    static int inited = false;
    public static ClRef cnr = new ClRef('com.jcraft.weirdx.WeirdX')
    public static FileChildLazyRef src = IffZipRefs.weirdx.childL('weirdx-1.0.32')
    public static FileChildLazyRef jar = IffZipRefs.weirdx.childL('weirdx-1.0.32/misc/weirdx.jar')


    @Deprecated
    static BinaryWithSource3 download(){
        return new BinaryWithSource3(jar,src)
    }

    @Deprecated
    static void downloadAndRun(){
        download()
//        File props = new File(source.source,'config/props')
//        assert props.exists()
        run([])
    }

    static void run(List<String> args){
        MainMethodRunner.run(cnr,args)
    }

    static void main(String[] args) {
        run([])
    }

}
