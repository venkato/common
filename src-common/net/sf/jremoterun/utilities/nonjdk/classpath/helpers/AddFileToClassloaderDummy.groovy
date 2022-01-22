package net.sf.jremoterun.utilities.nonjdk.classpath.helpers;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class AddFileToClassloaderDummy extends AddFilesToClassLoaderGroovy{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<File> addedFilesWithOrder = []

    @Override
    void addFileImpl(File file) throws Exception {
        addedFilesWithOrder.add(file)
    }

    static List<File> extractFilesFromGroovyFile(File groovyFile1){
        AddFileToClassloaderDummy addCl = new AddFileToClassloaderDummy()
        addCl.addFromGroovyFile(groovyFile1)
        return addCl.addedFilesWithOrder.findAll { it != null }.unique()
    }

}
