package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect

@CompileStatic
enum JrrStarterJarRefs2 implements ToFileRefRedirect {

    jremoterun, jrrassist, groovy_custom, groovy, java11base, jrrbasetypes,
    ;


    public FileChildLazyRef ref;

    JrrStarterJarRefs2() {
        ref = GitSomeRefs.starter.childL('libs/copy/' + name() + '.jar')
    }

    ChildChildPattern childOriginRef() {
        return JrrStarterOsSpecificFiles.originDir.ref.childL(getJarName())
    }

    ChildChildPattern childCopyRef() {
        return JrrStarterOsSpecificFiles.copyDir.ref.childL(getJarName())
    }

    FileChildLazyRef gitOriginRef() {
        return GitSomeRefs.starter.childP(childOriginRef())
    }

    FileChildLazyRef gitCopyRef() {
        return GitSomeRefs.starter.childP(childCopyRef())
    }

    String getJarName() {
        return name() + '.jar';
    }

    @Override
    FileChildLazyRef getRedirect() {
        ref;
    }

    @Override
    File resolveToFile() {
        return ref.resolveToFile()
    }
}
