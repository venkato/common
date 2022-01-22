package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.verifier.AsmByteCodeVerifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
abstract class AccessModifController extends AccessModifControllerP{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public AsmUtils asmUtils
    public AsmByteCodeVerifier asmByteCodeVerifier

    public Set<String> ignoreModifyClasses = new HashSet<>()
    public Set<String> modifiedClasses = new HashSet<>()
    public Set<String> makeClassPublic = new HashSet<>()

    AccessModifController() {
        this(JrrClassUtils.getCurrentClassLoader())
    }

    AccessModifController(ClassLoader cl) {
        asmByteCodeVerifier= new AsmByteCodeVerifier(cl)
        asmUtils = new AsmUtils(cl)
    }

    void addMakeClassPublic(ClRef clRef) {
        makeClassPublic.add(clRef.className.replace('.', '/'))
    }


    void addIgnoreModifyClasses(ClRef clRef) {
        ignoreModifyClasses.add(clRef.className.replace('.', '/'))
    }

    boolean needEntry(ZipEntry zipEntry) {
        return true
    }


    boolean needModifyClass(ClassReader className) {
        String find1 = ignoreModifyClasses.find { className.getClassName().startsWith(it) }
        if (find1 == null) {
            return true
        }
        return false
    }

    byte[] removeFinalModifier(String classNameWIthShash, byte[] bs) {
        final ClassReader reader = new ClassReader(bs);
        if (needModifyClass(reader)) {
            modifiedClasses.add(reader.getClassName().replace('/', '.'))
            return removeFinalModifier2(reader, bs)
        }
        return bs
    }


    void onFinish() {

    }




    String convertClassNameToShash(String className) {
        return className.replace('.', '/')
    }

    String convertClassNameToDot(String className) {
        return className.replace('/', '.')
    }

    /**
     * @param className with separator: /
     */
    boolean isNeedMakeClassPublic(String className) {
        className = className.replace('.', '/')
        String classFound = makeClassPublic.find { className.startsWith(it) }
        if (classFound == null) {
            return false
        }
        return true

    }


}
