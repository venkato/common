package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect

@CompileStatic
enum JrrStarterOsSpecificFilesSrc implements ChildRedirect, ToFileRefRedirect{
    JrrUtilities,
    JrrStarter,
    JrrInit,
    custom,
    firstdownload,
    jrrbasetypes,
    ;


    ExactChildPattern ref;
    FileChildLazyRef redirect;

    JrrStarterOsSpecificFilesSrc() {
        ref = new ExactChildPattern(name())
        redirect = GitSomeRefs.starter.childL(name())
    }

    ExactChildPattern getSrcPath(){
        return new ExactChildPattern(name()+'/src')
    }

    FileChildLazyRef calcGitRefSrc() {
        return calcGitRef().childL('src')
    }

    FileChildLazyRef calcGitRef() {
        return redirect
    }

    @Override
    File resolveToFile() {
        return redirect.resolveToFile()
    }
}
