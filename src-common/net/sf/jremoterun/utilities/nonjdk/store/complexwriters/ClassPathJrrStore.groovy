package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesWithSourcesI
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
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

    void addElToBodyS(String s) {
        writer7Sub.body.add(new LineInfo(Brakets.netural, "${buildAddPrefix()} ${s} ${semiColumn}"));
    }

    void addElToBodySuffix(String s) {
        writer7Sub.bodySuffixBlocks.add(new LineInfo(Brakets.netural, "${buildAddPrefix()} ${s} ${semiColumn}"));
    }

    void addElToBody(Object obj) {
        addElToBodyS(objectWriter.writeObject(writer3Importer,obj))
//        writer7Sub.body.add(new LineInfo(Brakets.netural, convertEl(obj)))
    }

    @Deprecated
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
        writer7Sub.bodySuffixBlocks.add( new LineInfo(Brakets.netural, convertSourceEl(el)+' '+semiColumn))
    }

    void writeSources(List sources) {
        assert sources != null
        if (sources.size() > 0) {
            writeSourcesImpl(sources)
        }
    }

    void writeSourcesImpl(List sources) {
        assert sources != null

        writer7Sub.bodySuffixBlocks.add  new LineInfo(Brakets.netural,'')
        writer7Sub.bodySuffixBlocks.add  new LineInfo(Brakets.openBacket,"if(${writer7Sub.varNameThis} instanceof ${writer3Importer.addImportWithName(AddFilesWithSourcesI)}){")
        writer7Sub.bodySuffixBlocks.add  new LineInfo(Brakets.netural,"  ${writer3Importer.addImportWithName(AddFilesWithSourcesI)} ${sourceVarName} = ${writer7Sub.varNameThis} as ${writer3Importer.addImportWithName(AddFilesWithSourcesI)} ${semiColumn}")
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
        writer7Sub.bodySuffixBlocks.add  new LineInfo(Brakets.closeBacket,'')
        writer7Sub.bodySuffixBlocks.add  new LineInfo(Brakets.netural,'')
    }


}
