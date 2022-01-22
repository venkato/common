package net.sf.jremoterun.utilities.nonjdk.compiler3

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseCompilerFactoryC
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseJavaCompiler2C
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.FileSystemJrr;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CompileRequestRemote implements CompilerRequest {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    ClassLoader classLoader12 = getClass().getClassLoader();

    @Override
    GroovyCompilerReply compile(GroovyCompilerParams params) {
        params.classNameRunnerWithParams.each {
            RunnableWithParamsFactory.fromClass3(it, classLoader12, params)
        }

        URL groovyJar = JrrUtils.getClassLocation(GroovyObject)
        log.fine "groovy jar = ${groovyJar}"
        GroovyCompiler groovyCompiler = new GroovyCompiler(params)
        groovyCompiler.init()
        if (params.javaVersion != null) {
            groovyCompiler.setJavaVersion(params.javaVersion);
        }
        params.dirs.unique().each {
            groovyCompiler.addClassesInDirForCompile(it,params.checkFilesPresentInDir)
        }
        File[] files2 = (File[])params.files.unique().toArray(new File[0])
        int aaa=0
        params.groovyTxtFiles.each {
            groovyCompiler.unit.addSource("someGroovyFile${aaa}",it)
            aaa++
        }

        groovyCompiler.unit.addSources(files2)
//        GroovyClassLoader cl = JrrClassUtils.currentClassLoaderGroovy
        groovyCompiler.additionalFlags.addAll(params.additionalFlags)
        groovyCompiler.additionalFlags.addAll(params.additionalFlags)
        groovyCompiler.compile()
        GroovyCompilerReply compilerReply = new GroovyCompilerReply();
        compilerReply.lookupedByGroovyClasses = groovyCompiler.classNodeResolverJrr.lookupedClasses
        compilerReply.groovyMethodsAddedTo = groovyCompiler.astTransformationSetPropsMethod.adedTo
        compilerReply.groovyMethodsSkipped = groovyCompiler.astTransformationSetPropsMethod.skipped
        if (groovyCompiler.compilerFactory instanceof EclipseCompilerFactoryC) {
            EclipseCompilerFactoryC vvv = (EclipseCompilerFactoryC) groovyCompiler.compilerFactory;
            EclipseJavaCompiler2C ccc = vvv.javaCompilerC
            if(ccc!=null) {
                FileSystemJrr nameEnvironment = ccc.compiler3.nameEnvironment
                if (nameEnvironment != null) {
                    if (nameEnvironment.usedNames != null) {
                        compilerReply.usedClasses = ccc.compiler3.buildUsedClasses()
                    }
                }
                if(compilerReply.usedClasses ==null){
                    compilerReply.usedClasses = []
                }
            }
        }

        return compilerReply
    }
}
