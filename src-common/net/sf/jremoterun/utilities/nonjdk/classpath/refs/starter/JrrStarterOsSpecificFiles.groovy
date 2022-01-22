package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect;

import java.util.logging.Logger;

@CompileStatic
enum JrrStarterOsSpecificFiles implements ChildRedirect,EnumNameProvider, ToFileRefRedirect{

    jrrutilitiesOneJarChildRef("onejar/${net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrLayoutfileName.jrrutilities}"),
    copyDir('libs/copy'),
    originDir('libs/origin'),
    libs( 'libs'),
    onejar( 'onejar'),
    log4j2_config('firstdownload/log4j2-config'),


    ;

    String customName;

    ExactChildPattern ref;

    FileChildLazyRef redirect;


    JrrStarterOsSpecificFiles(String customName) {
        this.customName = customName
        ref = new ExactChildPattern(customName)
        redirect = GitSomeRefs.starter.childL(customName)
    }

    FileChildLazyRef calcGitRef() {
        return redirect
    }

    @Override
    File resolveToFile() {
        return redirect.resolveToFile()
    }
}
