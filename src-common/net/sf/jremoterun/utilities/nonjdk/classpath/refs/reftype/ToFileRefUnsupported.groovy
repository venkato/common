package net.sf.jremoterun.utilities.nonjdk.classpath.refs.reftype

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef;

import java.util.logging.Logger;

@CompileStatic
class ToFileRefUnsupported implements File2FileRefWithSupportI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String msg

    ToFileRefUnsupported() {
    }

    ToFileRefUnsupported(Enum anEnum) {
        msg = anEnum.getClass().getName() + '.' + anEnum.name()
    }

    ToFileRefUnsupported(String msg) {
        this.msg = msg
    }

    @Override
    File resolveToFile() {
        if (msg == null) {
            throw new UnsupportedOperationException()
        } else {
            throw new UnsupportedOperationException(msg)
        }
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child)
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this, child)
    }

}
