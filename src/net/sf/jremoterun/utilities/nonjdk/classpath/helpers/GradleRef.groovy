package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.fileloayout.GradleFileLayout
import net.sf.jremoterun.utilities.nonjdk.windowsos.UserHomeOsGenericClass
import org.apache.tools.ant.DirectoryScanner;

import java.util.logging.Logger;

@CompileStatic
class GradleRef  implements ChildChildPattern {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenId mavenId
    public String suffix
    public ExactChildPPattern childP


    GradleRef(MavenId mavenId, String suffix) {
        this.mavenId = mavenId
        this.suffix = suffix
        DirectoryScanner scanner=new DirectoryScanner()
        scanner.setIncludes("*/${mavenId.artifactId}-${mavenId.version}${suffix}")
        childP = new ExactChildPattern(mavenId.groupId + '/' + mavenId.artifactId + '/' + mavenId.version).childP(new AntChildPattern(scanner))

    }

    @Override
    File resolveChild(File parent) {
        return childP.resolveChild(parent)
    }

    @Override
    String approximatedName() {
        return "${mavenId}-${suffix}"
    }

    @Override
    String toString() {
        return "${mavenId}-${suffix}"
    }

    @Override
    ChildChildPattern childL(String child) {
        return new ExactChildPPattern(this,child)
    }

    @Override
    ChildChildPattern childP(ChildPattern child) {
        return new ExactChildPPattern(this,child)
    }

    File resolveToFile(){
        return UserHomeOsGenericClass.userHome.childL(GradleFileLayout.jarsDir.customName).childP(this).resolveToFile()
    }


}
