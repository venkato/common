package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

import java.util.logging.Logger

@CompileStatic
class DirAccessModifController extends AccessModifController {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    void handleDirSelf(File inJarFile) {
        handleDir(inJarFile,inJarFile)
    }

    void handleDir(File inFileDir, File outputFileDir) {
        handleDirImpl(inFileDir,outputFileDir)
        onFinish()
    }

    void handleDirImpl(File inFileDir, File outputFileDir) {
        assert inFileDir.exists()
        inFileDir.listFiles().toList().each {
            File child = outputFileDir.child(it.name)
            if (it.isDirectory()) {
                child.mkdir()
                assert child.exists()
                handleDirImpl(it, child)
            } else if (it.isFile()) {
                handleFile(it, child)
            }else{
                throw new Exception("Stange file : ${it}")
            }
        }
    }

    void handleFile(File inFile, File outputFile) {
        outputFile.bytes = removeFinalModifier(inFile.absolutePath, inFile.bytes)
    }

    boolean decideAccessForInnderClass(ClassReader classReader, String name, int access) {
        String classFound = makeClassPublic.find { classReader.getClassName().startsWith(it) }
        if (classFound == null) {
            return false
        }
        boolean isFinal = (access & Opcodes.ACC_FINAL) > 0
        if (isFinal) {
            return true
        }
//        boolean isStatic = (access & Opcodes.ACC_STATIC) > 0
//        if (!isStatic) {
//            return true
//        }
        return false
    }

}
