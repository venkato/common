package net.sf.jremoterun.utilities.nonjdk.classpath.java11helper

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef;

import java.util.logging.Logger;

@CompileStatic
class AllowAllDeserialization {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClRef allowAllFilterClRef = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.java11.ObjectInputFilterAllowAll')


    public static FieldRef stdRef = new FieldRef(new ClRef('java.io.ObjectInputFilter$Config'), 'serialFilter')
    // non static
    public static FieldRef rmi1Ref = new FieldRef(new ClRef('javax.management.remote.rmi.RMIJRMPServerImpl'), 'cFilter')
    public static FieldRef rmi2Ref = new FieldRef(new ClRef(sun.rmi.registry.RegistryImpl), 'registryFilter')





    static void setAll(FieldRef fieldRef) {
        forceSet(stdRef)
        //forceSet(rmi1Ref)
        forceSet(rmi2Ref)
    }

    static void forceSet(FieldRef fieldRef) {
        net.sf.jremoterun.utilities.JrrClassUtils.setFieldValue(fieldRef.resolve().getDeclaringClass(),fieldRef.fieldName, net.sf.jremoterun.utilities.nonjdk.classpath.java11helper.AllowAllDeserialization.allowAllFilterClRef.newInstance3())

    }

    static void allowIfPossible() {
        try {
            new ClRef('java.io.ObjectInputFilter').loadClass2()
        } catch (ClassNotFoundException e) {
            return
        }
        RunnableFactory.runRunner allowAllFilterClRef
    }


}
