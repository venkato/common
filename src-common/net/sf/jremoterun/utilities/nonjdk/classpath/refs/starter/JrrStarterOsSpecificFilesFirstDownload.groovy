package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect
import net.sf.jremoterun.utilities.nonjdk.linux.EolConversion

@CompileStatic
enum JrrStarterOsSpecificFilesFirstDownload implements ChildRedirect, EnumNameProvider, ToFileRefRedirect {
    gr_dot_bat,
    gr_custom_sample_dot_bat,
    gr_custom_sample_dot_sh,
    gr_custom_dot_bat(true),
    gr_custom_dot_sh(true),
    gr_dot_sh,
    groovyrunner_dot_groovy,
    FirstDownloadCustomConfig_dot_groovy,
    jrrgroovyconfig_raw_dot_groovy,
    ;

    String customName;

    ExactChildPattern ref;

    FileChildLazyRef redirect;

    public boolean optionalFile = false

    JrrStarterOsSpecificFilesFirstDownload(boolean isOptional) {
        this();
        optionalFile = isOptional
    }

    JrrStarterOsSpecificFilesFirstDownload() {
        this.customName = name().replace('_dot_', '.')
        ref = new ExactChildPattern('firstdownload/' + customName)
        redirect = GitSomeRefs.starter.childL(ref.child)
    }


    FileChildLazyRef calcGitRef() {
        return redirect
    }

    @Override
    File resolveToFile() {
        return redirect.resolveToFile()
    }

//    boolean isNeedConvertToUnixEOL(){
//        return customName.endsWith('.sh')
//    }

    EolConversion getEolConversion(){
        if(customName.endsWith('.bat')){
            return EolConversion.windows;
        }
        if(customName.endsWith('.sh')){
            return EolConversion.linux;
        }
        return EolConversion.asIs;
    }


}
