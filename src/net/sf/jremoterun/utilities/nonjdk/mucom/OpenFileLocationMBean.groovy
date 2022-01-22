package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean

import javax.management.ObjectName;
import java.util.logging.Logger;

@CompileStatic
class OpenFileLocationMBean implements OpenFolderInMuCommander2, DefaultObjectName {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ObjectName defaultObjectName = defaultObjectName1

    public SetDaultCred daultCred

    @Override
    void openInMuCommander3(String location) {
        if(daultCred==null){
            daultCred = new SetDaultCred()
        }
        daultCred.setDir(location)
    }

    static void registerMBean() {
        MBeanFromJavaBean.registerMBean(new OpenFileLocationMBean())
    }
}
