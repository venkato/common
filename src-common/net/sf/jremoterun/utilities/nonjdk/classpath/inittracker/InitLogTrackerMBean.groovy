package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

@CompileStatic
interface InitLogTrackerMBean {

    Vector<LogItem> getListItems()

    String getParticularLogItemStringWithException(int i)

    String getParticularLogItemString(int i)

    LogItem getParticularLogItem(int i)

    LogItem getLastLogItem()

    void addLog(String msg);

    Object getClassLocation(String classLoaderId, ClRef clRef);
}