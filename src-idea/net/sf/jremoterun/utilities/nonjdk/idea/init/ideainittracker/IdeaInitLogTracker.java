package net.sf.jremoterun.utilities.nonjdk.idea.init.ideainittracker;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class IdeaInitLogTracker implements IdeaInitLogTrackerMBean {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName defaultObjectName;
    static {
        try {
            defaultObjectName = new ObjectName("iff:type=ideaInitLogs");
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }
    public static IdeaInitLogTracker defaultTracker = new IdeaInitLogTracker();
    public Vector<IdeaLogItem> listItems = new Vector();
    //public boolean passToLog = false;
    public boolean passToSysout = false;

    static {
        JrrClassUtils.addIgnoreClass(JrrClassUtils.getCurrentClass());
    }

    public IdeaInitLogTracker() {
        try {
            MBeanServer beanServer = JrrUtils.findLocalMBeanServer();
            if (!beanServer.isRegistered(defaultObjectName)) {
                beanServer.registerMBean(this, defaultObjectName);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed register initLogTracker", e);
        }
    }

    public void setListItems(Vector<IdeaLogItem> listItems) {
        this.listItems = listItems;
    }

    public void addLog(String msg) {
        IdeaLogItem logItem = new IdeaLogItem();
        logItem.msg = msg;
        listItems.add(logItem);
        if (passToSysout) {
            System.out.println(msg);
        }
    }

    public void addException(String msg, Throwable exception) {
        IdeaLogItem logItem = new IdeaLogItem();
        logItem.msg = msg;
        logItem.exception = exception;
        listItems.add(logItem);
        System.out.println("" + msg + " " + exception);
    }


    @Override
    public Vector<IdeaLogItem> getListItems() {
        return listItems;
    }

    @Override
    public String getParticularLogItemStringWithException(int i) {
        return JrrUtils.exceptionToString(getParticularLogItem(i).exception);
    }

    @Override
    public String getParticularLogItemString(int i) {
        return getParticularLogItem(i).toString();
    }

    @Override
    public IdeaLogItem getParticularLogItem(int i) {
        return listItems.get(i);
    }

    @Override
    public IdeaLogItem getLastLogItem() {
        return listItems.lastElement();
    }

}
