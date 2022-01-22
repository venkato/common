package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class StoreStat {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, Throwable> failedPlugins = [:];
    public List<String> skippedPlugins = [];
    public List<String> emptyPlugins = [];
    public Date startDate=new Date();

    public Map<String, Collection<String>> data;

    public long duration


}
