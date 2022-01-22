package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class Writer7Sub extends Writer6Sub {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class configClass ;

    Writer7Sub(Class configClass) {
        this.configClass = configClass
    }

    @Override
    Class getMainMethodArgType() {
        return configClass
    }
}
