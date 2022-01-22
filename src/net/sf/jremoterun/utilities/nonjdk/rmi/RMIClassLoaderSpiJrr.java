package net.sf.jremoterun.utilities.nonjdk.rmi;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoaderSpi;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class RMIClassLoaderSpiJrr extends RMIClassLoaderSpi{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public RMIClassLoaderSpi nested;

    public boolean isLogClassName = true;
    public boolean isLogGetClassLoader = true;
    public boolean isLogAnnotations = false;
    public Level level = Level.INFO;
    public Set<String> ignoreLoadClasses = new HashSet<>();


    public RMIClassLoaderSpiJrr() {
        //this.nested = nested;
        addIgnoreClasses();
    }

    public void addIgnoreClasses(){
        ignoreLoadClasses.add("[Ljava.rmi.server.ObjID;");
        ignoreLoadClasses.add("[B");
        ignoreLoadClasses.add("[Ljavax.management.ObjectName;");
        ignoreLoadClasses.add(new ClRef("java.rmi.dgc.Lease").className);
        ignoreLoadClasses.add(new ClRef("java.rmi.dgc.VMID").className);
        ignoreLoadClasses.add(new ClRef("java.rmi.server.UID").className);
        //ignoreLoadClasses.add(new ClRef("").className);
        //ignoreLoadClasses.add("java.rmi.server.RemoteObject");
        ignoreLoadClasses.add("");
    }

    @Override
    public Class<?> loadClass(String codebase, String name, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        if(isLogClassName){
            if(ignoreLoadClasses.contains(name)){

            }else {
                log.log(level, name);
            }
        }
        return nested.loadClass(codebase, name, defaultLoader);
    }

    @Override
    public Class<?> loadProxyClass(String codebase, String[] interfaces, ClassLoader defaultLoader) throws MalformedURLException, ClassNotFoundException {
        if(isLogClassName){
            if(interfaces==null){
                log.log(level, "null interfaces");
            }else {
                log.log(level, Arrays.toString(interfaces));
            }
        }
        return nested.loadProxyClass(codebase, interfaces, defaultLoader);
    }

    @Override
    public ClassLoader getClassLoader(String codebase) throws MalformedURLException {
        if(isLogGetClassLoader) {
            log.log(level, codebase);
        }
        return nested.getClassLoader(codebase);
    }

    @Override
    public String getClassAnnotation(Class<?> cl) {
        if(isLogAnnotations) {
            log.log(level, cl.getName());
        }
        return nested.getClassAnnotation(cl);
    }

    public static RMIClassLoaderSpi getRMIClassLoaderSpi() throws NoSuchFieldException, IllegalAccessException {
        return (RMIClassLoaderSpi) JrrClassUtils.getFieldValue(java.rmi.server.RMIClassLoader.class,"provider");
    }


    public static void setRMIClassLoaderSpi(RMIClassLoaderSpi impl) throws NoSuchFieldException, IllegalAccessException {
        JrrClassUtils.setFieldValue(java.rmi.server.RMIClassLoader.class, "provider", impl);
    }


    public void selfSetup() throws NoSuchFieldException, IllegalAccessException {
        nested = getRMIClassLoaderSpi();
        setRMIClassLoaderSpi(this);
    }



}
