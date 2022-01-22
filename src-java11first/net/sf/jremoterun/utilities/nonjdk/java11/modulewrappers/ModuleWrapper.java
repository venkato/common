package net.sf.jremoterun.utilities.nonjdk.java11.modulewrappers;

//import groovy.transform.CompileStatic;
import jdk.internal.module.Modules;
import jdk.internal.reflect.CallerSensitive;
//import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.module.ModuleDescriptor;
import java.util.Set;
import net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi.ModuleWrapperI;

//import java.util.logging.Logger;

//@CompileStatic
public class ModuleWrapper implements net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi.ModuleWrapperI{
//    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Module module;

    public ModuleWrapper(Module module) {
        this.module = module;
    }





    public Object getNestedObject(){
        return module;
    }

    public boolean isNamed() {
        return module.isNamed();
    }

    public String getName() {
        return module.getName();
    }

    public ClassLoader getClassLoader() {
        return module.getClassLoader();
    }

    public ModuleDescriptor getDescriptor() {
        return module.getDescriptor();
    }

    public ModuleLayer getLayer() {
        return module.getLayer();
    }

    Module w(ModuleWrapperI other){
        return ((ModuleWrapper)other).module;
    }

    public boolean canRead(ModuleWrapperI other) {
        return module.canRead(w(other));
    }

    @CallerSensitive
    public ModuleWrapper addReads(ModuleWrapperI other) {
        return new ModuleWrapper(module.addReads(w(other)));
    }

    public boolean isExported(String pn, ModuleWrapperI other) {
        return module.isExported(pn, w(other));
    }

    public boolean isOpen(String pn, ModuleWrapperI other) {
        return module.isOpen(pn, w(other));
    }

    public boolean isExported(String pn) {
        return module.isExported(pn);
    }

    public boolean isOpen(String pn) {
        return module.isOpen(pn);
    }

    @CallerSensitive
    public ModuleWrapper addExports(String pn, ModuleWrapperI other) {
        return new ModuleWrapper(module.addExports(pn, w(other)));
    }

    @CallerSensitive
    public ModuleWrapper addOpens(String pn, ModuleWrapperI other) {
        return new ModuleWrapper(module.addOpens(pn, w(other)));
    }

    @CallerSensitive
    public ModuleWrapper addUses(Class<?> service) {
        return new ModuleWrapper(module.addUses(service));
    }

    public boolean canUse(Class<?> service) {
        return module.canUse(service);
    }

    public Set<String> getPackages() {
        return module.getPackages();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return module.getAnnotation(annotationClass);
    }

    public Annotation[] getAnnotations() {
        return module.getAnnotations();
    }

    public Annotation[] getDeclaredAnnotations() {
        return module.getDeclaredAnnotations();
    }

    @CallerSensitive
    public InputStream getResourceAsStream(String name) throws IOException {
        return module.getResourceAsStream(name);
    }

    @Override
    public String toString() {
        return module.toString();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return module.isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return module.getAnnotationsByType(annotationClass);
    }

    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return module.getDeclaredAnnotation(annotationClass);
    }

    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return module.getDeclaredAnnotationsByType(annotationClass);
    }
}
