package net.sf.jremoterun.utilities.nonjdk.resourcebundle

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger

@CompileStatic
class ResourceLoaderFactory extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
//    public static ServiceLoaderFactory instance = new ServiceLoaderFactory();
    //public static Field serviceClassField = JrrClassUtils.findField(ServiceLoader, 'service');
    public ResourceBundleStorage loaderStorage;

    ResourceLoaderFactory(ResourceBundleStorage loaderStorage) {
        this.loaderStorage = loaderStorage
    }


    @Override
    Object get(Object key) {
        if (!(key instanceof ArrayList)) {
            throw new RuntimeException("Strange key ${key.getClass()} : ${key}");
        }
        ArrayList serviceLoader1 = key as ArrayList;
        return loaderStorage.getImpl(serviceLoader1)
    }



}
