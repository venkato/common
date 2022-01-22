package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef;

import java.util.logging.Logger;

@CompileStatic
class GitSpecBase implements ChildFileLazy{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    File2FileRefWithSupportI childL(String childS) {
        GitSpec gitSpec = new GitSpec()
        gitSpec.repo = childS;
        return gitSpec
    }

    @Override
    File2FileRefWithSupportI childP(ChildPattern child) {
        throw new UnsupportedOperationException("${child}")
    }
}
