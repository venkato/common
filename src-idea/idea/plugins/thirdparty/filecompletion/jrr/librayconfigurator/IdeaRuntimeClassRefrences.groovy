package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.ComplexGitRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustomRefsUrls
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.FrameworkBaseRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.IffUrlRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc

import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SvnRefs
import net.sf.jremoterun.utilities.nonjdk.downloadutils.IffZipRefs
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNamesFile
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnumFile
import net.sf.jremoterun.utilities.nonjdk.windowsos.ChocolateyFileLayout
import net.sf.jremoterun.utilities.nonjdk.windowsos.UserHomeClass
import net.sf.jremoterun.utilities.nonjdk.windowsos.UserHomeOsGenericClass
import net.sf.jremoterun.utilities.nonjdk.windowsos.WindowsProgramsReferences;

import java.util.logging.Logger;

@CompileStatic
class IdeaRuntimeClassRefrences {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void addReferences(){
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(GitSomeRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(IffUrlRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(GitReferences)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(IfFrameworkSrcDirs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(IfFrameworkResourceDirs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(BaseDirSetting)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(ComplexGitRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(CustomRefsUrls)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JeditermBinRefs2)

        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(SvnRefs)

        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterJarRefs2)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterOsSpecificFilesFirstDownload)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterOsSpecificFiles)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(JrrStarterOsSpecificFilesSrc)

        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(FrameworkBaseRef)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(UserHomeClass)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(PropsEnumFile)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(OsEnvNamesFile)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(WindowsProgramsReferences)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(ChocolateyFileLayout)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectEnumClass(UserHomeOsGenericClass)

        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(JrrStarterJarRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(JrrStarterVariables2)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(IffZipRefs)
        FieldResolvedDirectly.fieldResolvedDirectly.addDirectClass(net.sf.jremoterun.utilities.classpath.MavenDefaultSettings)


    }

}
