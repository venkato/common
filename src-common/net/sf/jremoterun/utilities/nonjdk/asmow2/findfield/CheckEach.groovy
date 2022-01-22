package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result.ClassElNotFound
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result.ClassFoundButElementFailed
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result.ResolvedFine
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.FieldFoundedEls
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ListStore2
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.GroovyStoreLoad;

import java.util.logging.Logger;

@CompileStatic
class CheckEach {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    //public List<File> files = []
    public List<FieldFoundedEls> input = []
    public List<ReflectionElCommon> output = []
    public AllClasspathAnalysis classpathAnalysis
    public boolean sortByClassName = true

    void doAll(File inputFile, File outputFile, int rotateCount) {
        doAllImpl(inputFile)
//        ListStore2 listStore2 = new ListStore2()
//        String s = listStore2.saveComplexObject(output)
//        FileRotate.rotateFile(outputFile, rotateCount)
//        outputFile.text = s
        GroovyStoreLoad groovyStoreLoad=new GroovyStoreLoad(output,outputFile,true)
        groovyStoreLoad.defaultListMap()
        groovyStoreLoad.saveIfNeeded()
    }

    void doAllImpl(File inputFile) {
        RunnableWithParamsFactory.runFile(inputFile, input)
        doStuff()
        assert output.size()>0
    }

    void doStuff() {
        assert input.size()>0
        input.each {
            classpathAnalysis.classes.add(it.foundEl.getClRef().clRef.className)
        }
        //assert files.size()>0

        input.each {
            try {
                output.add onObject(it)
            } catch (Throwable e) {
                log.info "failed on ${it} : ${e}"
                throw e
            }
        }
        if(sortByClassName){
            output =  output.sort(false,new ReflectionElCommonCompartor())
        }
    }

    ReflectionElCommon onObject(FieldFoundedEls obj2) {
        log.info "checking ${obj2}"
        Object obj = obj2.foundEl
        AsmClassFinder finder;
        if (obj instanceof FieldRef) {
            FieldFindAsm asm = new FieldFindAsm()
            asm.prepare(obj)
            finder = asm
        } else if (obj instanceof MethodRef) {
            MethodFindAsm asm = new MethodFindAsm()
            asm.prepare(obj)
            finder = asm
        } else if (obj instanceof ConstructorRef) {
            MethodFindAsm asm = new MethodFindAsm()
            asm.prepare(obj)
            finder = asm
        } else {
            throw new UnsupportedOperationException("${obj}")
        }
        finder.allClasspathAnalysis = classpathAnalysis
        //finder.files = files
        boolean debugNeeded =finder.isDebugNeeded(finder.fieldRef.getClRef().clRef)
        finder.doStuff(finder.fieldRef.getClRef().clRef)
        if (finder.isFound()) {
            ResolvedFine resolvedFine = new ResolvedFine(obj2, convertFileToObject(finder.foundField[0].baseFile))
            return resolvedFine
        } else {
            if(finder.classFound==null){
                return new ClassElNotFound(obj2.locationRef, obj2.foundEl)
            }else {
                Object objectLocationn = convertFileToObject(finder.classFound.f)
                if(debugNeeded){
                    log.info "${finder.fieldRef.getClRef().clRef} ${obj2.locationRef} loc=${objectLocationn} f=${finder.classFound}"
                }
                return new ClassFoundButElementFailed(obj2.locationRef,    obj2.foundEl, objectLocationn)
            }
        }
    }

    Object convertFileToObject(File f ){
        return classpathAnalysis.createCalculator().convertFileToObject(f)
    }


}
