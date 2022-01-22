package net.sf.jremoterun.utilities.nonjdk.mucom;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class OpenFolderInMuCommander {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static OpenFolderInMuCommander openFolderInMuCommander
    public static OpenFolderInMuCommander2 openFolderInMuCommander2


    abstract void openInMuCommander(String host,String folder)

    static void setMuCommander(MbeanConnectionCreator connectionCreator){
        OpenFolderInMuCommander2 client2 = MBeanClient.buildMbeanClient(OpenFolderInMuCommander2, connectionCreator, OpenFolderInMuCommander2.defaultObjectName1)
        openFolderInMuCommander2 = client2
    }

}
