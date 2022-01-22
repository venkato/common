package net.sf.jremoterun.utilities.nonjdk.classpath.helpers.store

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Sortable;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBaseRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.store.WriteStateMySelf
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI

import java.util.logging.Logger;

@Canonical
@CompileStatic
@Sortable
class GitSpecBaseRefSpecific extends GitSpec implements WriteStateMySelf {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitBaseRef baseRef1;
    public String child1;

    GitSpecBaseRefSpecific(GitBaseRef baseRef1, String child1) {
        this.baseRef1 = baseRef1
        this.child1 = child1
        repo = "${baseRef1.getBaseRef()}/${child1}"
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter) {
        return objectWriter.writeMethodClosure(writer3, baseRef1.&childL, [child1])
    }


//    boolean equals(object) {
//        if (this.is(object)) return true
//        if (!(object instanceof GitSpecBaseRefSpecific)) return false
//
//        GitSpecBaseRefSpecific that = (GitSpecBaseRefSpecific) object
//
//        if (baseRef1 != that.baseRef1) return false
//        if (child1 != that.child1) return false
//
//        return true
//    }
//
//    int hashCode() {
//        int result
//        result = (baseRef1 != null ? baseRef1.hashCode() : 0)
//        result = 31 * result + (child1 != null ? child1.hashCode() : 0)
//        return result
//    }

//    @Override
//    String toString() {
//        return repo
//    }
    @Override
    FileChildLazyRef childL(String child) {
        return super.childL(child)
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return super.childP(child)
    }
}
