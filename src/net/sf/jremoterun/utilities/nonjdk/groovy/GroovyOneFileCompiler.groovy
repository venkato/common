package net.sf.jremoterun.utilities.nonjdk.groovy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.compile.GenericCompiler;

import java.util.logging.Logger;

@CompileStatic
class GroovyOneFileCompiler extends GenericCompiler{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void prepare() {

    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {

    }

    void addTxtSource(String data){
        params.groovyTxtFiles.add(data)
    }


}
