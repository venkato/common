package net.sf.jremoterun.utilities.nonjdk.timmoson

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class TransferObject implements Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = 1L;

    public RemoteRef remoteRef;
    public long fileLength =-1
    public String sha1

}
