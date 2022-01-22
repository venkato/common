package net.sf.jremoterun.utilities.nonjdk.mucom;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class OpenFolderInMuCommander {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static OpenFolderInMuCommander openFolderInMuCommander
    public static OpenFolderInMuCommander2 openFolderInMuCommander2


    void openInMuCommander(String host, String folder) {
        open2(host, folder)
    }

    static void setMuCommander(MbeanConnectionCreator connectionCreator) {
        OpenFolderInMuCommander2 client2 = MBeanClient.buildMbeanClient(OpenFolderInMuCommander2, connectionCreator, OpenFolderInMuCommander2.defaultObjectName1)
        openFolderInMuCommander2 = client2
    }

    void open2(String host, String folder) {
        open3("sftp://${host}:${folder}")
    }

    Thread open3(String location) {
        Runnable r = {
            try {
                log.info "opening ${location} .."
                openFolderInMuCommander2.openInMuCommander3(location)
                log.info "opened ${location}"
            } catch (Throwable e) {
                log.warn("failed open ${location}", e)
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed open ${location}", e)
            }
        }
        Thread thread1 = new Thread(r, 'Open in mu commander')
        thread1.start()
        return thread1
    }


}
