package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddToAdderSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef

import java.util.logging.Logger

@CompileStatic
class AddDirectoryWithFiles2 implements AddToAdderSelf {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public ToFileRef2 baseDir;
    public List<String> excludeFiles = [];

    public ClassPathCalculatorWithAdder classPathCalculatorWithAdder = new ClassPathCalculatorWithAdder() {
        @Override
        protected void addElement2(Object obj) {
            boolean needAdd = false
            if (obj instanceof File) {
                File file = (File) obj;
                needAdd = needAcceptFile(file)
            } else {
                needAdd = true
            }
            if (needAdd) {
                super.addElement2(obj)
            }
        }
    }

    boolean needAcceptFile(File file) {
        if (excludeFiles.contains(file.getName())) {
            return false
        }
        return true;

    }

    AddDirectoryWithFiles2(File baseDir, List<String> excludeFiles) {
        this(new FileToFileRef(baseDir), excludeFiles)
    }
    AddDirectoryWithFiles2(ToFileRef2 baseDir, List<String> excludeFiles) {
        this.baseDir = baseDir
        this.excludeFiles = excludeFiles
    }

    void calcElements(){
        classPathCalculatorWithAdder.addFilesToClassLoaderGroovySave.addAllJarsInDirAndSubdirsDeep(baseDir.resolveToFile())
    }

    @Override
    void addToAdder(AddFilesToClassLoaderCommon adder) {
        calcElements()
        adder.addAll(classPathCalculatorWithAdder.filesAndMavenIds)
    }
}
