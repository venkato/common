package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ClassLocaltionInfo
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix
import org.codehaus.groovy.ast.decompiled.AsmReferenceResolver
import org.codehaus.groovy.ast.decompiled.ClassStub
import org.codehaus.groovy.ast.decompiled.DecompiledClassNode
import org.codehaus.groovy.control.ClassNodeResolver
import org.codehaus.groovy.control.CompilationUnit

import java.util.logging.Logger

@CompileStatic
class ClassNodeResolverAnalazerJrr extends ClassNodeResolver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AllClasspathAnalysis allClasspathAnalysis
    public Map<String, ClassStub> StubCache = [:]
    public ClRef clRef = new ClRef('org.codehaus.groovy.ast.decompiled.AsmDecompiler$DecompilingVisitor')
    public int resolvedFromCache = 0
    public int foundFiles = 0
    public int failedFound = 0
    public int failedFoundNoFollar = 0

    ClassNodeResolverAnalazerJrr(AllClasspathAnalysis allClasspathAnalysis) {
        this.allClasspathAnalysis = allClasspathAnalysis
    }


    @Override
    LookupResult findClassNode(final String clazz, final CompilationUnit compilationUnit) {
        ClassStub stub = StubCache.get(clazz);
        if (stub == null) {
            ClassLocaltionInfo localtionInfo = allClasspathAnalysis.duplicateClassesDetector.getClassLocation(new ClassSlashNoSuffix(net.sf.jremoterun.utilities.nonjdk.classpath.classloader.UsedByAnalysis.convertClassNameToSlash(clazz)))
            if (localtionInfo == null) {
                failedFound++
                if (!clazz.contains('$')) {
                    failedFoundNoFollar++
                    //log.info "failed find ${clazz}"
                }
            } else {
                if (localtionInfo.isMultiReleaseFile || localtionInfo.f.getName() == net.sf.jremoterun.utilities.nonjdk.classpath.classloader.GetClassesFromLocation.modulesFileName) {

                } else {
                    byte[] bs = allClasspathAnalysis.duplicateClassesDetector.onFile(localtionInfo)
                    if (bs == null) {
                        log.info "failed get content for ${clazz}"
                    } else {
                        //log.info "found ${clazz}"

                        groovyjarjarasm.asm.ClassVisitor visitor = clRef.newInstance3() as groovyjarjarasm.asm.ClassVisitor;


                        new groovyjarjarasm.asm.ClassReader(bs).accept(visitor, groovyjarjarasm.asm.ClassReader.SKIP_FRAMES);

                        stub = JrrClassUtils.getFieldValueR(clRef, visitor, 'result') as ClassStub;
                        StubCache.put(clazz, stub);
                        foundFiles++
                    }
                }
            }
        } else {
            resolvedFromCache++
        }
        if (stub != null) {
            //return stub;
            DecompiledClassNode asmClass = new DecompiledClassNode(stub, new AsmReferenceResolver(this, compilationUnit));

            assert asmClass.getName() == clazz
            // this may happen under Windows because getResource is case insensitive under that OS!
//                asmClass = null;
            return new LookupResult(null, asmClass)


        }
        //log.info "failed lookup ${clazz}"
        return super.findClassNode(clazz, compilationUnit)
    }

    void printStat() {
        log.info "foundFiles=${foundFiles} resolvedFromCache=${resolvedFromCache} failedFoundNoFollar=${failedFoundNoFollar} failedFound=${failedFound}"
    }
}
