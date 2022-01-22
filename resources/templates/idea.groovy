import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef

import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

@CompileStatic
class IdeaInitInHomeDirTemplate implements Runnable {

    ClRef cnr1 = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init2.IdeaInit3')


    File gitRepo = "FgitRepoF" as File
    File ideaLogDir = "FideaLogDirF" as File
    File compiledClasses = "FcompiledClassesF" as File


    AddFilesToClassLoaderGroovy adder2 = IdeaClasspathAdd.addCl

    @Override
    void run() {
        println('IdeaInitInHomeDir3 : in idea.groovy')
        adder2.isLogFileAlreadyAdded = false
        adder2.add compiledClasses
        println('IdeaInitInHomeDir3 : compiled classes added')
        RunnableWithParamsFactory.runClRefClassloader(cnr1, IdeaClasspathAdd.pluginClassLoader, [gitRepo, ideaLogDir])
        println(' idea.groovy finished')
    }
}





