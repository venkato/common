package net.sf.jremoterun.utilities.nonjdk.classpath;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.BinaryWithSourceI
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenIdContains;

import java.util.logging.Logger;

@CompileStatic
class ClasspathElAddedDebug implements NewValueListener<Object> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.GetClassesFromLocation.handleZipS to get list of files in jar archive
    public boolean resolveToFile = true
    
    @Override
    void newValue(Object o) {
        if (o instanceof MavenIdContains) {
            newEl((MavenIdContains) o);
        } else if (o instanceof File) {
            newEl((File) o);
        } else if (o instanceof BinaryWithSourceI) {
            newEl((BinaryWithSourceI) o);
        } else if (o instanceof AddDirectoryWithFiles) {
            newEl((AddDirectoryWithFiles) o);
        } else if (o instanceof AddDirectoryWithFiles2) {
            newEl((AddDirectoryWithFiles2) o);
        }else {
            newElGeneric(o)
        }
    }
    
    void newElGeneric(Object obj){
        if(resolveToFile) {
            File file = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler.resolveToFile(obj)
            newEl(file)
        }
    }

    void newEl(MavenIdContains m) {
        newElGeneric(m)
    }

    void newEl(File file) {

    }

    void newEl(BinaryWithSourceI binaryWithSourceI) {
        newElGeneric(binaryWithSourceI)
    }

    void newEl(AddDirectoryWithFiles directoryWithFiles) {
        if(resolveToFile) {
            directoryWithFiles.calcElements()
            directoryWithFiles.classPathCalculatorWithAdder.filesAndMavenIds.each {
                newValue(it)
            }
        }
    }

    void newEl(AddDirectoryWithFiles2 directoryWithFiles) {
        if(resolveToFile) {
            directoryWithFiles.calcElements()
            directoryWithFiles.classPathCalculatorWithAdder.filesAndMavenIds.each {
                newValue(it)
            }
        }
    }
}
