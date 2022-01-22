package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.AsmFieldFound
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.FieldFindAsm
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ClassLocaltionInfo
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectStringComparator
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.FailedParse
import org.objectweb.asm.Type

import java.util.logging.Logger

@CompileStatic
class LoaderStuff {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassLoader classLoader = JrrClassUtils.currentClassLoader

    public List<ProblemFoundBean> problems = []
//    List<ProblemFoundBean> problems2
    public List<ProblemFoundBean> parentFiles = []
    public PrintStream problemStream = System.out
    public AllClasspathAnalysis allClasspathAnalysis
    public List<FailedParse> failedParseList= []


    void printFailedParse(){
        if(failedParseList.size()>0) {
            List v = new ArrayList<>(failedParseList)
            v.sort (new ObjectStringComparator())
            log.info "failed parse size = ${failedParseList.size()} \n ${v.join('\n')}"
        }else {
            log.info "no errors"
        }
    }


    void problemFound(StaticElementInfo pb, String msg) {
        ProblemFoundBean problemFoundBean = new ProblemFoundBean()
        problemFoundBean.msg = msg
        problemFoundBean.pb = pb
        problems.add(problemFoundBean)
    }

    Class loadClass(String className) {
        return classLoader.loadClass(className)
    }

    ClRef resolveFieldClass(ClRef clRef,String fieldName){
        log.info "resolving field ${fieldName} for ${clRef} "
        FieldFindAsm fieldFindAsm=new FieldFindAsm()
        fieldFindAsm.prepare(new FieldRef(clRef,fieldName)) //NOFIELDCHECK
        fieldFindAsm.allClasspathAnalysis = allClasspathAnalysis
        fieldFindAsm.doStuff(clRef)
        if(!fieldFindAsm.isFound()){
            if(fieldFindAsm.classFound==null){
                log.info "failed get content for ${clRef}"
            }else {
                log.info "class ${clRef} found ${allClasspathAnalysis.createCalculator().convertFileToObject(fieldFindAsm.classFound.f)}, but field not ${fieldName}"
            }
            return null
        }
        AsmFieldFound found = fieldFindAsm.foundField[0]
        Type type1 = Type.getType(found.descriptor)
        if(type1.className == ClRef.getName()){
            log.info "failed resolve ${clRef} with field=${fieldName}, trying via class loader"
            //return null
            return resolveFieldClassOld(clRef,fieldName)
        }
        return new ClRef(type1.className);
    }

    ClRef resolveFieldClassOld(ClRef clRef,String fieldName){
        log.info "resolving ${clRef}"
        Object field1 = resolveField(clRef, fieldName)
        if(field1==null){
            return null
        }
        if (field1 instanceof ClRefRef) {
            return field1.getClRef();
        }
        if (field1 instanceof Class) {
            return new ClRef(field1)
        }
        return new ClRef(field1.getClass())
    }

    Object resolveField(ClRef clRef,String fieldName){
        Class clazz1 = loadClass(clRef.className)
        return JrrClassUtils.getFieldValue(clazz1,fieldName)
    }

    boolean checkClassExists(String className) {
        log.info "checking class ${className}"
        try {
            loadCtClass(className)
            return true
        } catch (NotFoundException e) {
            try {
                loadClass(className)
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }


    void onFileFound(StaticElementInfo pb, File f) {
        ProblemFoundBean problemFoundBean = new ProblemFoundBean()
        problemFoundBean.msg = f.absolutePath
        problemFoundBean.pb = pb
        parentFiles.add(problemFoundBean)
//        problemFound2(pb,f.absolutePath)
    }

    void problemFound2(StaticElementInfo pb, String msg) {
        int lineNo = pb.lineNumber
        if (lineNo == -1) {
            lineNo = 1
        }
        File f = pb.printablePath as File
        if (f.exists()) {
            problemStream.println("${pb.printablePath}: ${lineNo}: ${msg}")
        } else {
            problemStream.println("${pb.printablePath}.${pb.fieldName}(${pb.fileName}:${lineNo}) - ${msg}")
        }
    }


    void eachProblem(ProblemFoundBean pb) {
        switch (pb) {
            case { pb.pb.className == null }:
            case { pb.pb.printablePath == null }:
                throw new Exception("bad ${pb.pb}")
        }
    }

    boolean isPrintProblem(ProblemFoundBean pb) {
        return true
    }

    boolean isParentFile(ProblemFoundBean pb) {
        return pb.pb.isParentFile && pb.pb
    }

    void printProblems() {
        if (problems.size() == 0) {
            log.info("no problems found")
        } else {
            problems.each { eachProblem(it) }
            problems = problems.sort()
            problems = problems.findAll { isPrintProblem(it) }
            problemStream.println("================")
            problemStream.println("founded problems ${problems.size()} :")
            problems.each {
                problemFound2(it.pb, it.msg)
            }
        }
    }


    void printParentFiles() {
//        parentFiles.each { eachProblem(it) }
        parentFiles = parentFiles.sort()
        parentFiles = parentFiles.findAll { isParentFile(it) }
        problemStream.println("================")
        problemStream.println("parent files ${parentFiles.size()} :")
        parentFiles.each {
            problemFound2(it.pb, it.msg)
        }
    }


    CtClass loadCtClass(String className) {
        return ClassPool.getDefault().get(className)
    }


    void onException(Throwable excption, Object object) {
        log.info "failed analize ${object} ${excption}"
        throw excption;
    }

}
