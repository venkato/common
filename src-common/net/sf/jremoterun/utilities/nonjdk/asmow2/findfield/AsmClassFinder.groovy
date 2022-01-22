package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ClassLocaltionInfo
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.glavo.jimage.ImageReader
import org.objectweb.asm.ClassReader

import java.util.logging.Logger;

@CompileStatic
abstract class AsmClassFinder<T extends ClassMemberRef> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public List<File> files = []
    public AllClasspathAnalysis allClasspathAnalysis;
    //public ElFinderI finder;
    public boolean checkParents = true;
    public boolean foundAll = false;
    public HashSet<String> visitedClasses = new HashSet<>()
    public ClassLocaltionInfo classFound
    public HashSet<String> parentClasses = new HashSet<>()
    public List<AsmFieldFound> foundField = []
    public T fieldRef;
//
//    void addFilesFromGroovyFile(File f) {
//        AddFileToClassloaderDummy dummy = new AddFileToClassloaderDummy()
//        dummy.addFromGroovyFile(f)
//        files.addAll(dummy.addedFiles2)
//    }

    boolean isFound() {
        foundField.size() > 0
    }

    boolean doStuff2() {
        doStuff(fieldRef.getClRef().clRef)
    }

    boolean doStuff(ClRef clazz) {
        doStuffImpl(clazz)
        ClassLocaltionInfo foundCache1 = classFound
        if (checkParents) {
            if (isFound() && !foundAll) {
                return true
            } else {
                while (true) {
                    Set<String> toVisit = new HashSet<>(parentClasses) - visitedClasses
                    if(isDebugNeeded(clazz)){
                        log.info "toVisit = ${toVisit}"
                    }
                    toVisit.remove('java/lang/Object')
                    if (toVisit.size() == 0) {
                        if (isFound()) {
                            return true
                        }
                        //throw new Exception("Failed find class ${clazz} , visited = ${visitedClasses.sort()}")

//                        if(!isFound()){
                            classFound = foundCache1
//                        }
                        return false
                    }
                    String toVisit1 = toVisit.first()
//                    log.info "toVisit = ${toVisit1}"
                    doStuffImpl(new ClRef(toVisit1))
                }
            }
        } else {
            if (isFound()) {
                return true
            }
            //throw new Exception("Failed find class")
            return false
        }
    }

//    abstract ClRef getFirstClass()

    abstract BaseVisitor createClassVisitor()

    void check(File baseFile, ClRef clazz, ClassReader cr) {
        BaseVisitor fieldFinderVisitor = createClassVisitor()
        fieldFinderVisitor.baseFile = baseFile
        fieldFinderVisitor.clRef = clazz
        cr.accept(fieldFinderVisitor, getParsingOptions())
    }

    String normalizeClass(String className) {
        return className
        //return className.replace('/', '.')
//        return className.replace('/', '.')
    }

    List<ClRef> debugClasses = [new ClRef('org.slf4j.impl.StaticLoggerBinder')]

    boolean isDebugNeeded(ClRef clRef){
        boolean isDebugThis = debugClasses.contains(clRef)
        return isDebugThis
    }

    void doStuffImpl(ClRef clazz) {
        boolean thisDone = false
        boolean isDebugThis = isDebugNeeded(clazz)
        try {

//            if (files.size() == 0) {
//                throw new Exception("no files")
//            }
            String slashClass = clazz.getClassPath()
            visitedClasses.add slashClass
            //List<ClassLocaltionInfo> localtionInfos = allClasspathAnalysis.duplicateClassesDetector.classesOnLocationReverse.get(slashClass)

            ClassLocaltionInfo localtionInfo = allClasspathAnalysis.duplicateClassesDetector.getClassLocation( new ClassSlashNoSuffix( slashClass))
            if (localtionInfo == null) {
                log.info "failed find ${clazz}"
            } else {
                if(isDebugThis){
                    log.info "${clazz} loc=${localtionInfo}"
                }
                byte[] bs = allClasspathAnalysis.duplicateClassesDetector.onFile(localtionInfo)
                if (bs == null) {
                    log.info "failed get content for ${clazz}"
                } else {
                    classFound = localtionInfo
                    thisDone = true
                    ClassReader cr = new ClassReader(bs)
                    check(localtionInfo.f, clazz, cr)
                    if(isDebugThis){
                        log.info "bb ${clazz} loc=${localtionInfo}"
                    }
                    addParents(cr)

                }
            }
        } catch (Throwable e) {
            log.info "failed on ${clazz} : ${e}"
            throw e
        }
        if (!thisDone) {
            //throw new Exception("failed find class ${clazz}")
        }
    }

    void addParents(ClassReader cr) {
        String[] interfaces = cr.getInterfaces()
        if (interfaces != null) {
            parentClasses.addAll(interfaces.toList().collect { normalizeClass(it) })
        }
        String supername1 = cr.getSuperName()
        if (supername1 != null) {
            parentClasses.add normalizeClass(supername1)
        }
    }



    int getParsingOptions() {
        return 0
    }

    void addFound(BaseVisitor methodFinderVisitor, AsmFieldFound asmFieldFound) {
        asmFieldFound.baseFile = methodFinderVisitor.baseFile
        asmFieldFound.clRef = methodFinderVisitor.clRef
        boolean isDebugThis = isDebugNeeded(methodFinderVisitor.clRef)
        if(isDebugThis){
            log.info "${methodFinderVisitor.clRef} ${asmFieldFound}"
        }
        foundField.add(asmFieldFound)
    }
}
