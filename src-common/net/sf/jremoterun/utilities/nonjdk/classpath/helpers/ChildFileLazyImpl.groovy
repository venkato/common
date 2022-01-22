package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect;

import java.util.logging.Logger;

@CompileStatic
class ChildFileLazyImpl implements ChildFileLazy, ToFileRef2, ToFileRefRedirect{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    ToFileRef2 redirect

    ChildFileLazyImpl(ToFileRef2 redirect1) {
        this.redirect = redirect1
    }

    @Override
    File resolveToFile() {
        return redirect.resolveToFile()
    }

    @Override
    String toString() {
        return redirect.toString();
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child);
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }
}
