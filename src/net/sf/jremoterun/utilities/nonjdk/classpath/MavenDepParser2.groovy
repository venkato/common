package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGitRefSup

import java.util.logging.Logger

/**
 * Or run : mvn dependency:tree
 * Or : mvn dependency:list -Dsort=true
 * see https://maven.apache.org/plugins/maven-dependency-plugin/list-mojo.html
 */
 @CompileStatic
class MavenDepParser2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


     static void findDep(File inFile,File outFile){
         assert inFile.exists()
         List<MavenId> deps = new MavenDepParser().findDepsFromPomXml(inFile.text)
         ClassPathCalculatorGitRefSup calculator = new ClassPathCalculatorGitRefSup()
         calculator.filesAndMavenIds.addAll deps
         outFile.text =  calculator.saveClassPath9()
     }

}
