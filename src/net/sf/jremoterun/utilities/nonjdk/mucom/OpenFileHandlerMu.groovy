package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.nonjdk.shellcommands.NativeCommand
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.EnvOpenSettings
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.OpenFileHandler;

import java.util.logging.Logger;


@CompileStatic
class OpenFileHandlerMu implements OpenFileHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void openFile(File file) {
        JrrUtilitiesFile.checkFileExist(file)
//        if(file.isDirectory()) {
            OpenFolderInMuCommander.openFolderInMuCommander2.openInMuCommander3(file.normalizePathPathToParent())
//        }else{
//            File getParentFile = file.getParentFile()
//            assert getParentFile.exists()
//            OpenFolderInMuCommander.openFolderInMuCommander2.openInMuCommander3(getParentFile.normalizePathPathToParent())
//        }
        //return null
    }


    static void setMuCommander(MbeanConnectionCreator connectionCreator){
        OpenFolderInMuCommander.setMuCommander(connectionCreator)
        EnvOpenSettings.defaultOpenFileHandler = new OpenFileHandlerMu()
    }
}
