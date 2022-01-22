package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustomRefsUrls
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitSpecRef
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.lib.Ref

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class JeditTermCompilerConsoleCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String commitIdJterm = 'faa6d1df3cd62acdc59823832ef57203e57d1802'
    public static String commitIdPty4j = 'cb75a796dd093c135be37604adf98674c253e27b'

    List mavenIds = [
            LatestMavenIds.jcraftHostFix,
            LatestMavenIds.jcraftZlib,
            LatestMavenIds.guavaMavenIdNew,
            LatestMavenIds.log4jOld,
            LatestMavenIds.jnaPlatform,
            LatestMavenIds.jna,
//            Log4j2MavenIds.slf4j_impl,
//            LatestMavenIds.slf4jApi,
            LatestMavenIds.jetbrainsAnnotations,
    ]


    ClRef clRef1 = new ClRef('com.jediterm.ssh.jsch.JSchTtyConnector');
    ClRef clRef2 = new ClRef('com.jediterm.terminal.ui.TerminalPanel');


    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    JeditTermCompilerConsoleCompiler() {
    }

//    File baseDir
    File buildDir


    void prepare() {
        detectBuildDir()
        buildDir.mkdir()
        assert buildDir.exists()
        compilerPure.outputDir = buildDir.child('jediterm_classes')
        compilerPure.outputDir.mkdirs()

//        compilerPure.adder.addAll Log4j2MavenIds.all
        compilerPure.adder.addAll mavenIds
        compilerPure.adder.add CustomRefsUrls.pureJavacommnyJetBrainsUrl

        compilerPure.addInDir GitReferences.pty4jJetbrainsSrc


        JeditermBinRefs2.all.each { compilerPure.addInDir(it.getRedirect()) }
        compilerPure.javaVersion = '1.8'
        hackSpeicicClass()
        hackSpeicicClass2()
        log.info("out dir : ${compilerPure.outputDir}")
    }


    public static String createJSchMethodName = 'createJSch';

    void hackSpeicicClass() {
        String specificClassSuffix1 = clRef1.className.replace('.', '/') + ClassNameSuffixes.dotjava.customName;
        File specificFile = JeditermBinRefs2.ssh.getRedirect().childL(specificClassSuffix1).resolveToFile()
        assert specificFile.exists()
        File fileToRemove = compilerPure.files.find { it.getName() == specificFile.getName() }
        assert compilerPure.files.remove(fileToRemove)
        String text = specificFile.text
        text = text.replace('config.put("compression.s2c", "zlib,none");', ' ; ')
        text = text.replace('config.put("compression.c2s", "zlib,none");', ' ; ')
        String fromText1 = 'JSch jsch = new JSch();'
        String fromText2 = 'private Session connectSession(Questioner questioner) throws JSchException {'
        assert text.contains(fromText1)
        assert text.contains(fromText2)
        if (text.contains(fromText1)) {
            text = text.replace(fromText1, "JSch jsch = ${createJSchMethodName}();")
            text = text.replace(fromText2, """

protected JSch ${createJSchMethodName}(){return new JSch();}

protected Session connectSession(Questioner questioner) throws JSchException {

""")
        }
        File srcOverride = buildDir.child('src_override')
        srcOverride.mkdir()
        assert srcOverride.exists()
        File fileOverride = srcOverride.child(specificFile.getName())
        fileOverride.text = text
        compilerPure.files.add(fileOverride)

    }


    void hackSpeicicClass2() {
        String specificClassSuffix1 = clRef2.className.replace('.', '/') + ClassNameSuffixes.dotjava.customName;
        File specificFile = JeditermBinRefs2.terminal.getRedirect().childL(specificClassSuffix1).resolveToFile();
        assert specificFile.exists()
        File fileToRemove = compilerPure.files.find { it.getName() == specificFile.getName() }
        assert compilerPure.files.remove(fileToRemove)
        String text = specificFile.text
        String fromText1 = 'private void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {'
        String fromText2 = 'private HyperlinkStyle findHyperlink(Point p) {'
        assert text.contains(fromText1)
        assert text.contains(fromText2)
        if (text.contains(fromText1)) {
            text = text.replace(fromText1, "protected void pasteFromClipboard(boolean useSystemSelectionClipboardIfAvailable) {")
            text = text.replace(fromText2, "protected HyperlinkStyle findHyperlink(Point p) {")
        }
        File srcOverride = buildDir.child('src_override')
        srcOverride.mkdir()
        assert srcOverride.exists()
        File fileOverride = srcOverride.child(specificFile.getName())
        fileOverride.text = text
        compilerPure.files.add(fileOverride)

    }


    void detectBuildDir() {
        if (buildDir == null) {
            File baseDir = JeditermBinRefs2.terminal.ref.specOnly.resolveToFile()
            buildDir = baseDir.child('build')
        }
    }

    //static String branchName12 = 'java8'

    static void checkoutByCommit(GitSpecRef gitSpecRef, String commitId) {
        log.info "do checkout ${gitSpecRef.gitSpec.repo} ${gitSpecRef} by commit ${commitId} .."
//        File file123 = GitSomeRefs.jtermGitSpec.resolveToFile()
        File file = gitSpecRef.resolveToFile()
        GitRepoUtils gitRepoUtils = new GitRepoUtils(file)
//        try {
            gitRepoUtils.checkoutByCommitId(commitId)
            log.info "${gitSpecRef.gitSpec.repo} switched to commit=${commitIdJterm}"

//        } catch (Exception e) {
//            log.log(Level.SEVERE, "failed swicth ${file} to commit ${commitId}", e)
//        }

    }


    static File compileIfNeededSpecialBranchChkout() {
        checkoutByCommit(GitSomeRefs.jtermGitSpec, commitIdJterm)
        checkoutByCommit(GitSomeRefs.pty4jJetBrains, commitIdPty4j)
        return compileIfNeededS()
    }

    static File compileIfNeededS() {

        return new JeditTermCompilerConsoleCompiler().compileIfNeeded()
    }

    File compileIfNeeded() {
        detectBuildDir()
        File zipFile = buildDir.child('jediterm.jar')
        if (!zipFile.exists()) {
            doCompile()
        }
        assert zipFile.exists()
        return zipFile
    }

    void doCompile() {
        log.info "compiling JeditTerm"
        prepare()
        compilerPure.compile()
        zipp()
        log.info "JeditTerm compiled"
    }


    void checkSpecificFile() {
        String specificClassSuffix1 = clRef1.getClassPath() + ClassNameSuffixes.dotclass.customName;
        File f = compilerPure.outputDir.child(specificClassSuffix1)
        assert f.exists()
        boolean methodExists = f.text.contains(createJSchMethodName)
        assert methodExists
        log.info "${createJSchMethodName} method found"
    }


    File zipp() {
        assert buildDir.exists()
        checkSpecificFile()
        File zipFile = buildDir.child('jediterm.jar')
        File zipFileTmp = buildDir.child('jeditermTmp.jar')
        zipFileTmp.delete()
        assert !zipFileTmp.exists()
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(zipFileTmp, compilerPure.outputDir)
        zipFileTmp.setLastModified(GroovyCustomCompiler.defaultDate.getTime())
        FileUtilsJrr.copyFile(zipFileTmp, zipFile)
        return zipFile;
    }


}
