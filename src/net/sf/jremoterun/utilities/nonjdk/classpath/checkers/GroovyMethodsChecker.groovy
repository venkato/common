package net.sf.jremoterun.utilities.nonjdk.classpath.checkers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmConsoleDecompiler
import net.sf.jremoterun.utilities.nonjdk.asmow2.MethodListClassVisitor
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.objectweb.asm.ClassReader

import java.util.logging.Logger;

@CompileStatic
class GroovyMethodsChecker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int options = 0



    void checkGroovyMethods(File baseDir, ClRef childPattern) {
        checkGroovyMethods2( new File(baseDir,childPattern.getClassPath()+ ClassNameSuffixes.dotclass.customName))
    }

    void checkGroovyMethods2(File f) {
        assert f.exists()
        BufferedInputStream in1 = f.newInputStream()
        try {
            ClassReader cr = new ClassReader(in1);
            checkGroovyMethodsImpl(cr)
        }finally {
            JrrIoUtils.closeQuietly2(in1,log)
        }
    }

    void checkGroovyMethods2(ClRef className) {
        ClassReader cr = new ClassReader(className.className);
        checkGroovyMethodsImpl(cr)
    }

    void checkGroovyMethodsImpl(ClassReader cr) {
        MethodListClassVisitor visitor = new MethodListClassVisitor(AsmConsoleDecompiler.getMaxAsmM(), null);
        cr.accept(visitor, options);
        List<String> names1 = GroovyMethodsEnum.values().collect { it.getNameAndDescriptor() }
        HashSet<String> existedMethods = visitor.existedMethods
//        log.info2(existedMethods.join('\n'))
        assert existedMethods.contains(GroovyMethodsEnum.setProperty.getNameAndDescriptor())
        assert existedMethods.containsAll(names1)

    }


}
