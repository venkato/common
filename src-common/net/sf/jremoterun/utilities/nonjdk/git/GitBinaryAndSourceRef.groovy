package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Sortable
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef

import java.util.logging.Logger

@Canonical
@CompileStatic
@Sortable
class GitBinaryAndSourceRef extends GitRef implements GitBinaryAndSourceRefRef {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String src;

    GitBinaryAndSourceRef(GitSpecRef repo, String bin, String src) {
        super(repo.getGitSpec(), bin)
        this.src = src
    }

    GitBinaryAndSourceRef(String repo, String bin, String src) {
        super(repo, bin)
        this.src = src
    }

    @Override
    String toString() {
        return "${repo} ${pathInRepo} ${src}"
    }


    GitRef getRefToSourceOnly() {
        return new GitRef(this, src);
    }

    @Override
    GitBinaryAndSourceRef getRef() {
        return this
    }



    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }


}
