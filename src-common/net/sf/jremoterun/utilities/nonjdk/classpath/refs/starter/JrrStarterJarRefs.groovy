package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter


import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs;


@CompileStatic
class JrrStarterJarRefs {

    public static BinaryWithSource2 jrrutilitiesOneJar = new BinaryWithSource2(JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.calcGitRef(), JrrStarterOsSpecificFilesSrc.JrrUtilities.calcGitRefSrc());
    public static BinaryWithSource2 jrrbasetypes = new BinaryWithSource2(JrrStarterJarRefs2.jrrbasetypes.gitCopyRef(), JrrStarterOsSpecificFilesSrc.jrrbasetypes.calcGitRefSrc());

    public static ExactChildPattern FirstDownloadCustomConfig =new ExactChildPattern('FirstDownloadCustomConfig.groovy');

    static {
        // used in
        GitSomeRefs.starter.childL('firstdownload/jrrgroovyconfig_raw.groovy')
    }






}
