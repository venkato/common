package net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable

import java.lang.reflect.Method;
import java.util.logging.Logger;

@CompileStatic
class MethodFinderReceive {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef clRef1 = new ClRef('net.sf.jremoterun.utilities.nonjdk.java11.modulewrappers.MethodFinderReceive')

    public static volatile Method addExportsToAll0M;
    public static volatile Method implAddExportsOrOpensM;

    public static ModuleFinderI getModuleFinder() {
        if(!Java11ModuleSetDisable.isJava11()){
            throw new IllegalStateException("not java 11")
        }
        return clRef1.newInstance3() as ModuleFinderI
    }


    static Object getEveryOneModule() {
        return JrrClassUtils.getFieldValue(new ClRef('java.lang.Module'), 'EVERYONE_MODULE')
    }

    static void disabledForEveryone() {
        ModuleFinderI moduleFinder = getModuleFinder()
//        ModuleWrapperI evryModule = moduleFinder.wrap(getEveryOneModule())
        moduleFinder.getAllModules().each {
            addExportsToAll0(it, it.getPackages());
        }
    }

    static void addExportsToAll0(ModuleWrapperI moduleWrapperI, Collection<String> package1) {
        package1.each {
            addExportsToAll0(moduleWrapperI, it);
        }
    }

    static void addExportsToAll0(ModuleWrapperI moduleWrapperI, String package1) {
        if (addExportsToAll0M == null) {
            addExportsToAll0M = JrrClassUtils.findMethodByCount(new ClRef('java.lang.Module'), 'addExportsToAll0', 2);
        }
        addExportsToAll0M.invoke(null,moduleWrapperI.getNestedObject(), package1);
    }

    static void implAddExportsOrOpens(ModuleWrapperI moduleWrapperI, ModuleWrapperI other, String package1, boolean open, boolean syncVM) {
        if (implAddExportsOrOpensM == null) {
            implAddExportsOrOpensM = JrrClassUtils.findMethodByCount(new ClRef('java.lang.Module'), 'implAddExportsOrOpens', 4);
        }
        implAddExportsOrOpensM.invoke(moduleWrapperI.getNestedObject(), package1, other.getNestedObject(), open, syncVM);
    }


}
