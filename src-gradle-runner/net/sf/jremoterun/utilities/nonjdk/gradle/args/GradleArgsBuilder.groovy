package net.sf.jremoterun.utilities.nonjdk.gradle.args

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
class GradleArgsBuilder  {

    static String javaHome(File javaHome){
        assert javaHome.exists()
        return "-D${GradleArgs.org_gradle_java_home.customName}=${javaHome.getAbsolutePathUnix()}"
    }

    static String jvmArgs(List<String> args){
        List<String> badArgs = args.findAll { it.contains(' ') }
        assert badArgs.size()==0
        String join1 = args.join(' ')
        return "-D${GradleArgs.org_gradle_jvmargs.customName}=${join1}"
    }


}
