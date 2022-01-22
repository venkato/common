package net.sf.jremoterun.utilities.nonjdk.groovy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

// MetaClassRegistryImpl
// Not used
@CompileStatic
class MetaClassRegistryJrr implements MetaClassRegistry {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public MetaClassRegistry delegate;

    MetaClassRegistryJrr(MetaClassRegistry delegate) {
        this.delegate = delegate
    }

    @Override
    MetaClass getMetaClass(Class theClass) {
        return delegate.getMetaClass(theClass)
    }

    @Override
    void setMetaClass(Class theClass, MetaClass theMetaClass) {
        delegate.setMetaClass(theClass,theMetaClass)
    }

    @Override
    void removeMetaClass(Class theClass) {
        delegate.removeMetaClass(theClass)
    }

    @Override
    MetaClassCreationHandle getMetaClassCreationHandler() {
        return delegate.getMetaClassCreationHandler();
    }

    @Override
    void setMetaClassCreationHandle(MetaClassCreationHandle handle) {
        delegate.setMetaClassCreationHandle(handle)
    }

    @Override
    void addMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        delegate.addMetaClassRegistryChangeEventListener(listener)
    }

    @Override
    void addNonRemovableMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        delegate.addNonRemovableMetaClassRegistryChangeEventListener(listener)
    }

    @Override
    void removeMetaClassRegistryChangeEventListener(MetaClassRegistryChangeEventListener listener) {
        delegate.removeMetaClassRegistryChangeEventListener(listener)
    }

    @Override
    MetaClassRegistryChangeEventListener[] getMetaClassRegistryChangeEventListeners() {
        return delegate.getMetaClassRegistryChangeEventListeners()
    }

    @Override
    Iterator iterator() {
        return delegate.iterator()
    }
}
