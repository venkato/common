package net.sf.jremoterun.utilities.nonjdk.net.dnsstat

import groovy.transform.CompileStatic
import groovy.transform.ToString;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider;

import java.util.logging.Logger;


@CompileStatic
class DnsInfoBean {

    public String hostName;
    public String resolvedTo;
    public Date lastResolved
    public DnsResolvedStatus resolvedStatus

    @Override
    String toString() {
        return getClass().getSimpleName()+' '+ new ObjectDumperProvider(true).dumpObject(this)
    }
}
