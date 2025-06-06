package idea.plugins.thirdparty.filecompletion.jrr.a.file.sample

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.JeditermBinRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SshdMavenIds
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.windowsos.UserHomeClass

import java.nio.charset.Charset;


@CompileStatic
public class SampleGroovy {

    public static File file1 = new File("c:/1/");

    private void testNoyUsed(){
        GitSomeRefs.jtermGitSpec.childL('terminal/build.gradle');
        new GitSpec('https://github.com/JetBrains/jediterm').childL('terminal/build.gradle');
        GitSomeRefs.jtermGitSpec.childL('terminal/src/com/jediterm/terminal/DataStreamIteratingEmulator.java');
        GitSomeRefs.jtermGitSpec.resolveToFile().child('lib/javac2.jar');
        GitSomeRefs.jtermGitSpec.resolveToFile().child('lib/javac2.jar');
        //UserHomeClass.ProgramFiles.childL('Microsoft Update Health Tools/Logs/')
        UserHomeClass.ProgramFiles.getRedirect().resolveToFile().child('Microsoft Update Health Tools/Logs/').child('aaa')
        UserHomeClass.ProgramFiles.getRedirect().childL('Microsoft Update Health Tools/Logs/').childL('bb')
        UserHomeClass.ProgramFiles.getRedirect().childL('Microsoft Update Health Tools/Logs/').childL('bb').resolveToFile().child('uva')

        JeditermBinRefs2.terminal.getRedirect().childL('com/jediterm/terminal/ArrayTerminalDataStream.java')

        new File("c:/windows/System32/drivers/");
        Charset.forName("273");
        System.getProperty('user.home')
        "aaa".toString()
        file1.toString();
        "C:\\progi\\idea\\2017.2ce\\lib\\annotations.jar" as File
        SshdMavenIds.core.toString()
        file1.child("gcc2/regeva/jremoterun-1.0.zip");
        SampleJava.f.child('a.txt')
        SampleEnum.a2.f.child('System32')
        SampleEnum.a2.childL('System32')
    }
}
