package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrClassUtils2
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor

import java.lang.reflect.Field
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class AsmConsoleDecompiler implements ClassNameSynonym {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static volatile boolean setMaxAsmF = true
    public static volatile Throwable exceptionOnInit
    public static volatile Field asmCodeField
    public static volatile int asmCode = Opcodes.ASM5
    public static int options = 0

    static int getMaxAsmM() {
        if(setMaxAsmF){
            setMaxAsmF = false
            setMaxAsmImpl()
        }
        return asmCode
    }

    static void setMaxAsmImpl() {
        int max1 = -1
        JrrClassUtils2.fieldsIterator(Opcodes) {Field f->
            String name2 = f.getName()
            if (name2.startsWith('ASM') && name2.length() < 5) {
                try {
                    int value2 = f.get(Opcodes) as int
                    if (max1 < value2) {
                        max1 = value2
                        asmCodeField = f
                    }
                } catch (Exception e) {
                    log.log(Level.WARNING, "failed get ${name2}", e)
                    exceptionOnInit = e;
                }
            }
            return false
        }
        if (max1 > 0) {
            asmCode = max1
        } else {
            log.warning("failed get asm code")
        }
    }

    /**
     * options - 0 for all,
     * @see org.objectweb.asm.ClassReader#SKIP_DEBUG
     */
    void printAll(String className, boolean asAsm) {
        ClassReader cr = new ClassReader(className);
        String code2 = printAll2(cr, asAsm)
        log.info "code : \n ${code2}"
    }

    String printAll2(ClassReader cr, boolean asAsm) {
        Printer printer = asAsm ? new Textifier() : new ASMifier()
        StringWriter stringWriter = new StringWriter()
        TraceClassVisitor visitor = new TraceClassVisitor(null, printer, new PrintWriter(stringWriter, true))
        cr.accept(visitor, options);
//        String join = printer.text.join('')
        String code2 = stringWriter.toString()
        return code2
    }

    void printMethod(String className, String methodName, boolean asAsm) {
        ClassReader cr = new ClassReader(className);
        String join = printMethod2(cr, methodName, asAsm)
        log.info "code : \n ${join}"
    }

    String printMethod2(ClassReader cr, String methodName, boolean asAsm) {
        Printer printer = asAsm ? new Textifier() : new ASMifier()
        AsmConsoleDecompilerVisitor visitor = new AsmConsoleDecompilerVisitor(AsmConsoleDecompiler.getMaxAsmM(), null, methodName, printer)
        cr.accept(visitor, options);
        if (!visitor.found) {
            throw new NoSuchMethodException("${cr.className} ${methodName}")
        }
        String join = printer.getText().join('')
        return join
    }

}
