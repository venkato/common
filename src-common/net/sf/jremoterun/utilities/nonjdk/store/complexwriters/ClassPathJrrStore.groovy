package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.nonjdk.store.StoreComplex
import org.codehaus.groovy.runtime.MethodClosure;

import java.util.logging.Logger;

@CompileStatic
class ClassPathJrrStore extends StoreComplex<List> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static MethodClosure addGenerecMethod = (MethodClosure) (Closure) AddFilesToClassLoaderCommon.&add


    public static MethodClosure addSourceFileMethod = (MethodClosure) (Closure) AddFilesWithSourcesI.&addSourceF

    public static MethodClosure addSourceCustomMethod = (MethodClosure) (Closure) AddFilesWithSourcesI.&addSourceS

    public static String sourceVarName = 's'

    ClassPathJrrStore() {
        super(AddFilesToClassLoaderGroovy)
    }


    ClassPathJrrStore(Class configClass) {
        super(configClass)
    }

    @Deprecated
    String saveClassPath7(List files) throws Exception {
        return saveComplexObject(files)
    }

    @Override
    String saveComplexObject(List files) throws Exception {
        writeBinaryClasses(files)
        onBodyCreated()
        return writer7Sub.buildResult()
    }


    String saveClassPathWithSources(List files, List sources) throws Exception {
        writeBinaryClasses(files)
        writeSources(sources)
        onBodyCreated()
        return buildResult()
    }

    void writeBinaryClasses(List files) {
        assert files != null
//        buildHeader(writer7Sub)
        //buildImport(writer7Sub)
        int counttt = -1;
        files.collect {
            try {
                counttt++
                addElToBody(it)
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                onFailedWriteEl(it, counttt, e)
            }
        }
    }


    void addElToBody(Object obj) {
        writer7Sub.body.add(convertEl(obj))
    }


    String convertEl(Object el) {
        String s = objectWriter.writeObject(writer3Importer, el)
        return "${buildAddPrefix()} ${s} ${semiColumn}"
    }


    String buildAddPrefix() {
        return "${writer7Sub.varNameThis}.${addGenerecMethod.method}"
    }


    String convertSourceEl(Object el) {
        String s = objectWriter.writeObject(writer3Importer, el)
        if (el instanceof File) {
            return "  ${sourceVarName}.${addSourceFileMethod.method} ${s}"
        }
        return "  ${sourceVarName}.${addSourceCustomMethod.method} ${s}"
    }

    void addSourceToBodyBlock(Object el) {
        writer7Sub.bodySuffixBlocks.add(convertSourceEl(el)+' '+semiColumn)
    }

    void writeSources(List sources) {
        assert sources != null
        if (sources.size() > 0) {
            writeSourcesImpl(sources)
        }
    }

    void writeSourcesImpl(List sources) {
        assert sources != null
        writer3Importer.addImport(AddFilesWithSourcesI)
        writer7Sub.bodySuffixBlocks.add ''
        writer7Sub.bodySuffixBlocks.add "if(${writer7Sub.varNameThis} instanceof ${AddFilesWithSourcesI.simpleName}){".toString()
        writer7Sub.bodySuffixBlocks.add "  ${AddFilesWithSourcesI.simpleName} ${sourceVarName} = ${writer7Sub.varNameThis} as ${AddFilesWithSourcesI.simpleName} ${semiColumn}".toString()
        int counttt = -1;
        sources.each {
            try {
                counttt++
                addSourceToBodyBlock(it)
            } catch (Throwable e) {
                log.info("Failed write source ${counttt} el from list : ${e}")
                onFailedWriteEl(it, counttt, e)
            }

        }
        writer7Sub.bodySuffixBlocks.add '}'
        writer7Sub.bodySuffixBlocks.add ''

    }


}
