package net.sf.jremoterun.utilities.nonjdk.classpath.inittracker

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes

import javax.management.MBeanServer
import javax.management.ObjectName
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class InitLogTracker implements InitLogTrackerMBean{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static  ObjectName defaultObjectName = new ObjectName('iff:type=initlogs')
    public static InitLogTracker defaultTracker = new InitLogTracker();
    public Vector<LogItem> listItems = new Vector<>()
    //public boolean passToLog = false;
    public boolean passToSysout = false;

    static {
        JrrClassUtils.addIgnoreClass(JrrClassUtils.getCurrentClass())
    }

    InitLogTracker() {
        if(net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTrackerConfig.registerInJmx) {
            try {
                MBeanServer beanServer = JrrUtils.findLocalMBeanServer();
                if (beanServer.isRegistered(defaultObjectName)) {
                    addLog("${defaultObjectName} already registered1")
                    Object[] params = ['already registered2'];
                    String[] signature = [String.getName()];
                    beanServer.invoke(defaultObjectName, 'addLog', params, signature)
                } else {
                    beanServer.registerMBean(this, defaultObjectName)
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "failed register initLogTracker", e);
            }
        }
    }

    void setListItems(Vector<LogItem> listItems) {
        this.listItems = listItems
    }

    void addLog(String msg){
        LogItem logItem =new LogItem()
        logItem.msg = msg
        listItems.add(logItem)
        if(passToSysout){
            println(msg)
        }
    }

    void addException(String msg,Throwable exception){
        LogItem logItem =new LogItem()
        logItem.msg = msg
        logItem.exception = exception
        listItems.add(logItem)
        println("${msg} ${exception}")
    }



    @Override
    Vector<LogItem> getListItems() {
        return listItems
    }

    @Override
    String getParticularLogItemStringWithException(int i){
        return JrrUtils.exceptionToString(getParticularLogItem(i).exception)
    }

    @Override
    String getParticularLogItemString(int i){
        return getParticularLogItem(i).toString()
    }

    @Override
    LogItem getParticularLogItem(int i){
        return listItems.get(i)
    }

    @Override
    LogItem getLastLogItem(){
        return listItems.lastElement();
    }


    @Override
    Object getClassLocation(String classLoaderId, ClRef clRef){
        try {
            ClassLoader classLoader1
            if (classLoaderId == null) {
                classLoader1 = getClass().getClassLoader()
            } else {
                classLoader1 = JrrUtils.findParentClassLoader(classLoaderId)
            }
            if (classLoader1 == null) {
                throw new NullPointerException('classloder is null')
            }
            String path1 = clRef.getClassPath() + ClassNameSuffixes.dotclass.customName
            Enumeration<URL> resources1 = classLoader1.getResources(path1)
            if (resources1 == null) {
                throw new NullPointerException('resources1 is null')
            }
            List<URL> list1 = resources1.toList()
            if (list1.size() == 1) {
                URL url1 = list1[0]
                if (url1 == null) {
                    throw new NullPointerException('url is null')
                }
                return url1.toString()
            }
            List<String> collect1 = list1.collect { it.toString() }
            return collect1
        }catch(Throwable e){
            log.log(Level.WARNING,"failed find ${clRef}",e)
            JrrUtils.throwRootExceptionAndCheckSer(e)
        }
    }


}
