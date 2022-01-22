package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.git.GitBaseRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec;

import java.util.logging.Logger;

@CompileStatic
enum FrameworkBaseRef implements ChildFileLazy, GitBaseRef{
//enum FrameworkBaseRef implements File2FileRefWithSupportI{


    venkato('https://github.com/venkato'),
    ;

    String baseRef;

    FrameworkBaseRef(String baseRef) {
        this.baseRef = baseRef
    }

    @Override
    GitSpec childL(String child) {
        GitSpec gitSpec = new GitSpec()
        gitSpec.repo = baseRef+'/'+child
        return gitSpec
    }

    @Override
    File2FileRefWithSupportI childP(ChildPattern child) {
        throw new UnsupportedOperationException("${child}")
    }
}
