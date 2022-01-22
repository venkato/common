package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

import java.util.logging.Logger


@CompileStatic
class SvnRef extends SvnSpec{

//    @Canonical
//    @EqualsAndHashCode
//    @ToString

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String branch;

    SvnRef(String repo, String branch) {
        super(repo)
        this.branch = branch
    }

    @Override
    FileChildLazyRef childL(String child) {
        return super.childL(child)
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return super.childP(child)
    }
}
