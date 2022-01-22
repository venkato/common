package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr;

import java.util.logging.Logger;

@CompileStatic
class ConfigLoaderInstanceList<T> extends ConfigLoaderInstanceWrapperFile<Collection<T>>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ConfigLoaderInstanceList(File fileToLoad) {
        super(fileToLoad)
    }

    ConfigLoaderInstanceList(File fileToLoad, GroovyConfigLoaderGeneric<GroovyConfigLoader2I<Collection<T>>> configLoader) {
        super(fileToLoad, configLoader)
    }

    @Override
    Collection<T> createNew() {
        return []
    }

    @Override
    Collection<T> clone1(Collection<T> collection) {
        return new ArrayList(collection)
    }
}
