package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRefRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef;

import java.util.logging.Logger;

@CompileStatic
enum GitSomeRefs implements GitSpecRef {

    aradSocket('https://github.com/araditc/AradSocket'),
    jtermGitSpec('https://github.com/JetBrains/jediterm'),

    pty4jTraff('https://github.com/traff/pty4j'),
    pty4jJetBrains('https://github.com/JetBrains/pty4j'),
    purejavacommTraff('https://github.com/traff/purejavacomm'),
    purejavacommNyholku('https://github.com/nyholku/purejavacomm'),

    dex2jar('https://github.com/pxb1988/dex2jar'),
    smali('https://github.com/JesusFreke/smali'),
    rstaVenkato('https://github.com/venkato/RSTALanguageSupport'),

    javaDecompiler('https://github.com/Konloch/bytecode-viewer'),

    desugar('https://github.com/google/desugar_jdk_libs'),

    rstaAutoCompetionVenkato('https://github.com/venkato/AutoComplete'),

    @Deprecated
    ifFramework("https://github.com/venkato/InvocationFramework"),

    starter("https://github.com/venkato/starter3"),

    sshConsole("https://github.com/venkato/ssh-consoles"),
    commonUtil("https://github.com/venkato/common",'main'),

    firstdownloadGitRef("https://github.com/venkato/firstdownload"),


    jnaplatext('https://github.com/malyn/jnaplatext'),


    ideaPsiViewer('https://github.com/JetBrains/psiviewer'),

    rdesktop('https://github.com/kohsuke/properjavardp'),

    socketGuiTest('https://github.com/akshath/SocketTest'),

    eclipseBatchCompiler("https://github.com/groovy/groovy-eclipse"),

    helfy("https://github.com/xardazz/helfy"),

    eclipseFileCompiltion("https://github.com/impetuouslab/eclipse-filecompletion"),


    jnaRepo("https://github.com/venkato/jna"),

    @Deprecated
    jschDocumentationRef("https://github.com/ePaul/jsch-documentation"),

    androidR8('https://r8.googlesource.com/r8'),

    jhexViewer('https://github.com/google/binnavi'),

    // https://github.com/lbalazscs/Pixelitor
    // https://github.com/statickidz/SimpleNotepad-Swing

    eclipseGithubApi('https://github.com/eclipse/egit-github'),

    benchmarksuite('https://github.com/sfuhrm/benchmarksuite'),
    dockingFrames('https://github.com/Benoker/DockingFrames'),

    ideaDatabaseNavigator('https://bitbucket.org/dancioca/dbn'),

    mavenWagon('https://github.com/apache/maven-wagon'),
    //execute native code on fly
    nalim('https://github.com/apangin/nalim'),

    asyncProfiler('https://github.com/jvm-profiling-tools/async-profiler'),
    restTestSuite('https://github.com/supanadit/restsuite'),
    mooInfo('https://github.com/rememberber/MooInfo'),
    ghidra('https://github.com/NationalSecurityAgency/ghidra'),

    ;

    GitSpec gitSpec;

    GitSomeRefs(String url,String branchName) {
        this.gitSpec = new GitSpec()
        gitSpec.repo = url
        gitSpec.branch = branchName
    }
    GitSomeRefs(String url) {
        this.gitSpec = new GitSpec()
        gitSpec.repo = url
    }

    @Override
    File resolveToFile() {
        return getGitSpec().resolveToFile()
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }


    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }
}
