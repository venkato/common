package net.sf.jremoterun.utilities.nonjdk.props.propsanalysis

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.JustStackTrace2
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.LocationElement
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.PropertiesTracker
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.PropsTracker

import java.util.logging.Logger;

@CompileStatic
class PropsLocationAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean sortByLocation = false;
    public Map<Object, LocationElement> usedPropsStacktrace;

    public HashSet<String> logClasses = new HashSet<>();
    public HashSet<String> logIgnoreClasses = new HashSet<>();

    public Comparator<Map.Entry<Object, LocationElement>> resultComparator = new PropsComparator() {
        @Override
        boolean needSortByLocation() {
            return sortByLocation
        }
    }

    public List<String> ignoreClasses = [
            'com.sun.jmx.mbeanserver.GetPropertyAction',
            'java.security.AccessController',
            'io.netty.util.internal.SystemPropertyUtil',
            'com.sun.org.apache.xerces.internal.utils.SecuritySupport',
            'marytts.server.MaryProperties',
            'com.azure.core.util.Configuration',
            'java.util.Properties',
            'java.lang.Boolean',
            'java.lang.Integer',
            'java.lang.Long',
            'java.lang.Short',
            'sun.security.action.',
            'com.ibm.icu.impl.ICUConfig',
            'org.apache.log4j.helpers.OptionConverter',
            'javax.xml.transform.SecuritySupport',
            'com.intellij.openapi.util.registry.RegistryValue',
            'com.intellij.util.SystemProperties',
            'sun.security.ssl.Utilities',
            'kotlinx.coroutines.internal.SystemPropsKt',
            'org.apache.groovy.util.SystemUtil',
            'org.bouncycastle.util.Properties',
            'com.android.flags.overrides.PropertyOverrides',
            'sun.net.NetProperties',
            'sun.java2d.marlin.MarlinProperties',
            'org.eclipse.osgi.framework.util.SecureAction',
            'org.eclipse.oomph.util.PropertiesUtil',
            'org.apache.ivy.core.settings.IvyVariableContainerImpl',
//            'java.lang.reflect.Constructor',
//            'java.lang.Class',
            java.lang.System.getName(), PropertiesTracker.getName(), PropsTracker.getName(),
    ];

    public List<String> getSystemPropertyClassName = [
            'com.sun.org.apache.xerces.internal.utils.XMLSecurityManager',
            'com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager',
            'com.sun.org.apache.bcel.internal.util.SecuritySupport',
            'com.sun.org.apache.xalan.internal.utils.SecuritySupport',
            'org.apache.commons.logging.LogFactory',
            'org.xml.sax.helpers.SecuritySupport',
            'javax.xml.parsers.SecuritySupport',
            'jdk.xml.internal.SecuritySupport',
            'jdk.xml.internal.JdkXmlFeatures',
            'org.apache.xerces.parsers.SecuritySupport',
            'org.ini4j.Config',
            'org.apache.commons.lang3.SystemUtils',
    ];


    PropsLocationAnalysis(PropsTracker tracker) {
        this(tracker.usedProps)
        sortByLocation = tracker.logStack
    }

    PropsLocationAnalysis(Map<Object, LocationElement> usedPropsStacktrace) {
        this.usedPropsStacktrace = usedPropsStacktrace
    }

    boolean isGoodEl(Object propName, StackTraceElement e) {
        String className = e.getClassName()
        String methodName1 = e.getMethodName()
        if (className == 'com.azure.core.util.Configuration' && methodName1 == 'load') {
            return true;
        }
        if(methodName1!=null && methodName1.startsWith('lambda$')){
            return false
        }
        if (methodName1 == 'getSystemProperty') {
            boolean b = getSystemPropertyClassName.contains(className)
            if (b) {
                return false
            }
        }
        if (className.contains('$') && methodName1 == 'run') {
            String find2 = getSystemPropertyClassName.find { className.startsWith(it) }
            if (find2 != null) {
                return false
            }
        }

        if (className == 'java.awt.Font' && methodName1 == 'getFont') {
            return false;
        }
//        if (className == 'sun.java2d.marlin.MarlinProperties' && methodName1 == 'getBoolean') {
//            return false;
//        }
        if (className == 'sun.security.ssl.Debug' && methodName1 == 'getBooleanProperty') {
            return false;
        }
        String find2 = ignoreClasses.find { className.startsWith(it) }
        if (find2 != null) {
            return false
        }
//        String find1 = JrrClassUtils.ignoreClassesForCurrentClass.find { className.startsWith(it) }
//        if (find1 != null) {
//            return false
//        }
        return true
    }

    Map<Object, LocationElement> analyze() {
        usedPropsStacktrace.each {
            if (it.value.fullLocation != null) {
                it.value.location = findStackEl(it.key, it.value.fullLocation);
            }
        }
        return usedPropsStacktrace
    }

    StackTraceElement findStackEl(Object propName, JustStackTrace2 e) {
        StackTraceElement[] trace = e.getStackTrace()
        return trace.toList().find { isGoodEl(propName, it) }
    }


    boolean isNeedLog(Object propName, LocationElement e) {
        if(propName == PropsTracker.propsRandomName){
            return false;
        }
        if (e.location == null) {
            return true
        }
        String className = e.location.getClassName()
        boolean accept = true;
        if (logClasses.size() > 0) {
            String whileList1 = logClasses.find { it.startsWith(className) }
            if (whileList1 == null) {
                accept = false
            }
        }
        if (accept) {
            String blackList1 = logIgnoreClasses.find { it.startsWith(className) }
            if (blackList1 != null) {
                accept = false
            }
        }
        return accept;
    }

    String convertToIdeaStackTrace() {
        Map<Object, LocationElement> map = analyze()
        Map<Object, LocationElement> res = map.findAll { isNeedLog(it.key, it.value) };
        List<Map.Entry<Object, LocationElement>> entries = sort1(res.entrySet());
        String res2 = entries.collect { convertEl(it.key, it.value) }.join('\n')
        return res2
    }

    String convertEl(Object propName, LocationElement e) {
        String location1 = e.location;
        if (location1 == null) {
            location1 = ''
        }
        String isUsed2 = e.isUsed ? 'T' : 'F'
        StringBuilder suffix = new StringBuilder();
        if(!(propName instanceof String)){
            suffix.append "key not string class : ${propName.getClass().getName()} "
        }
        if(e.propValueClass !=null && e.propValueClass!=String){
            suffix.append "value not string class : ${e.propValueClass.getName()} "
        }
        if(e.propRemoved){
            suffix.append "removed "
        }

        return "${location1} \t${isUsed2} ${propName} ${suffix}"
    }

    List<Map.Entry<Object, LocationElement>> sort1(Set<Map.Entry<Object, LocationElement>> entries) {
        List<Map.Entry<Object, LocationElement>> res = new ArrayList<Map.Entry<Object, LocationElement>>(entries);
        res.sort(resultComparator)
        return res
    }

}
