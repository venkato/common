package net.sf.jremoterun.utilities.nonjdk.mbeans;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.DefaultObjectName;
import net.sf.jremoterun.utilities.JrrClassUtils;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.net.URL;
import java.util.logging.Logger;

public class DiagnosticJavaLang implements DiagnosticJavaLangMBean, DefaultObjectName {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ObjectName defaultObjectName = new ObjectName("jrr:type=diagnosticjava");

    public String getClassLocation(String className) throws Exception {
        Class aClass = getClass().getClassLoader().loadClass(className);
        URL classLocation = JrrUtils.getClassLocation(aClass);
        return classLocation.toString();
    }

}
