package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.BinaryWithSource
import net.sf.jremoterun.utilities.classpath.BinaryWithSource2
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenPath
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.GeneralBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.BinaryWithSource3
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.GradleRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.RefLink
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.git.GitBinaryAndSourceRef
import net.sf.jremoterun.utilities.nonjdk.git.GitRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnRef
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.FieldFoundedEls
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ClassSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CustomWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.DateWithFormatWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.FileSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.JavaBeanStore2
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ListSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.MapSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.StringWriter
import org.codehaus.groovy.runtime.MethodClosure
import org.joda.time.LocalDate

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class ObjectWriter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static Map<String, CustomWriter> customsS = [:]
    public static HashSet<String> simpleStringWritersS = []

    public Map<String, CustomWriter> customsCurrent = new HashMap<>(customsS)
    public HashSet<String> simpleStringWritersCurrent = new HashSet<>(simpleStringWritersS)
    public static CustomWriter<Map> mapWriterS = new MapSimpleWriter();
    public static CustomWriter<List> listWriterS = new ListSimpleWriter();
    public static StringWriter stringWriterS = new StringWriter();
    public StringWriter stringWriterCurrent = stringWriterS;
    public static CustomWriter<JavaBean2> javaBeanStore2 = new JavaBeanStore2();
    public CustomWriter<Map> mapWriter = mapWriterS;
    public CustomWriter<List> listWriter = listWriterS;
    public boolean writeCollectionAsList = false
    public String nullObject = 'null'

    static void addSimpleStringWriter(Class clazz) {
        simpleStringWritersS.add(clazz.getName())
    }

    static void addAddCustomWriter(CustomWriter cw) {
        customsS.put(cw.getDataClass().getName(), cw)
    }

    static {
        // !! register only classes, which doesn't have ancestors
        addAddCustomWriter(new DateWithFormatWriter());
        addAddCustomWriter(new FileSimpleWriter());
        addAddCustomWriter(new ClassSimpleWriter());
        addSimpleStringWriter(MavenId)
        addSimpleStringWriter(URL)
        addSimpleStringWriter(ClRef)
        addSimpleStringWriter(MavenPath)
    }

    ObjectWriter() {
    }

    String writeObject(Writer3Import writer3, Object obj) {
        try {
            writeObjectImpl(writer3, obj)
        } catch (Exception e) {
            log.info("failed write ${obj.getClass().getName()} ${e}")
            throw e
        }
    }

    protected String writeObjectImpl(Writer3Import writer3, Object obj) {
        if (obj == null) {
            return nullObject;
        }
        String classNameFull = obj.getClass().getName()
        if (obj != null) {
            CustomWriter customWriter = customsCurrent.get(classNameFull)
            if (customWriter != null) {
                return customWriter.save(writer3, this, obj)
            }
            if (simpleStringWritersCurrent.contains(classNameFull)) {
                return writeStringConstructor2(obj.getClass(), writer3, obj.toString())
            }
        }
        switch (obj) {
            case { obj instanceof WriteStateMySelf }:
                WriteStateMySelf selff = obj as WriteStateMySelf
                return selff.save(writer3, this)
            case { obj instanceof Boolean }:
                boolean b = obj as Boolean
                return "${b}"
            case { obj instanceof String }:
                return writeString(obj as String)
            case { obj instanceof Enum }:
                Enum ee = obj as Enum
                return writeEnum(writer3, ee)
            case { obj instanceof RefLink }:
                RefLink ee = obj as RefLink
//                writer3.addImport(RefLink)
//                writer3.addImport(ee.enumm.getClass())
//                return "new RefLink( ${ee.enumm.getClass().getSimpleName()}.${ee.enumm.name()} )"
                //return "new ${RefLink.getSimpleName()}( ${writeEnum(writer3,ee.enumm)} )"
                return writeConstructorWithArgs(RefLink, writer3, [ee.enumm])
            case { obj instanceof SimpleDateFormat }:
                SimpleDateFormat sdf = obj as SimpleDateFormat
                return writeStringConstructor2(SimpleDateFormat, writer3, sdf.toPattern());
            case { obj instanceof GradleRef }:
                GradleRef repo = obj as GradleRef
                return write2ArgsConstructor(writer3, GradleRef, repo.mavenId, repo.suffix);
            case { obj instanceof GeneralBiblioRepository }:
                GeneralBiblioRepository repo = obj as GeneralBiblioRepository
                return write2ArgsConstructor(writer3, GeneralBiblioRepository, repo.name(), repo.url);
            case { obj instanceof MavenIdAndRepo }:
                MavenIdAndRepo mavenIdAndRepo = obj as MavenIdAndRepo
                return write2ArgsConstructor(writer3, MavenIdAndRepo, mavenIdAndRepo.m, mavenIdAndRepo.repo);
            case { obj instanceof BinaryWithSource3 }:
                BinaryWithSource3 bs = obj as BinaryWithSource3
                Object source2
                if (bs.source.size() == 1) {
                    source2 = bs.source[0]
                } else {
                    source2 = bs.source
                }
                return writeConstructorWithArgs(BinaryWithSource3, writer3, [bs.binary, source2])
            case { obj instanceof BinaryWithSource2 }:
                BinaryWithSource2 bs = obj as BinaryWithSource2
                Object source2
                if (bs.source.size() == 1) {
                    source2 = bs.source[0]
                } else {
                    source2 = bs.source
                }
                return writeConstructorWithArgs(BinaryWithSource2, writer3, [bs.binary, source2])
            case { obj instanceof BinaryWithSource }:
                BinaryWithSource bs = obj as BinaryWithSource
                return writeConstructorWithArgs(BinaryWithSource, writer3, [bs.binary, bs.source])
            case { obj instanceof GitBinaryAndSourceRef }:
                GitBinaryAndSourceRef bs = obj as GitBinaryAndSourceRef
                return writeConstructorWithArgs(GitBinaryAndSourceRef, writer3, [bs.repo, bs.pathInRepo, bs.src])
            case { obj instanceof FileToFileRef }:
                FileToFileRef bs = obj as FileToFileRef
                return writeConstructorWithArgs(FileToFileRef, writer3, [bs.file])
            case { obj instanceof SvnRef }:
                SvnRef svnRef = obj as SvnRef
                return writeConstructorWithArgs(SvnRef, writer3, [svnRef.repo, svnRef.branch])
            case { obj instanceof SvnSpec }:
                SvnSpec svnSpec = obj as SvnSpec
                return writeStringConstructor2(SvnSpec, writer3, svnSpec.repo)
            case { obj instanceof GitRef }:
                GitRef gitRef = obj as GitRef
                return writeConstructorWithArgs(GitRef, writer3, [gitRef.repo, gitRef.pathInRepo])
            case { obj instanceof EnumIdea }:
                writer3.addImport(obj.getClass())
                EnumIdea ev = obj as EnumIdea
                String value = writeObject(writer3, ev.getName())
                return "${obj.getClass().getSimpleName()}.getE( ${value} )"
            case { obj instanceof Integer }:
                return "${obj}"
            case { obj instanceof LocalDate }:
                LocalDate date2 = (LocalDate) obj;
                return writeConstructorWithArgs(LocalDate, writer3, [date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth()]);
            case { obj instanceof FieldRef }:
                FieldRef date2 = (FieldRef) obj;
                return writeConstructorWithArgs(FieldRef, writer3, [date2.clRef, date2.fieldName]);
            case { obj instanceof MethodRef }:
                MethodRef date2 = (MethodRef) obj;
                return writeConstructorWithArgs(MethodRef, writer3, [date2.clRef, date2.methodName, date2.paramsCount]);
            case { obj instanceof ConstructorRef }:
                ConstructorRef date2 = (ConstructorRef) obj;
                return writeConstructorWithArgs(ConstructorRef, writer3, [date2.clRef, date2.paramsCount]);
            case { obj instanceof LocationRef }:
                LocationRef date2 = (LocationRef) obj;
                if (date2.f == null) {
                    return writeConstructorWithArgs(LocationRef, writer3, [date2.clRef, date2.lineNumber]);
                } else {
                    return writeConstructorWithArgs(LocationRef, writer3, [date2.f, date2.lineNumber]);
                }
            case { obj instanceof FieldFoundedEls }:
                FieldFoundedEls date2 = (FieldFoundedEls) obj;
                return writeConstructorWithArgs(FieldFoundedEls, writer3, [date2.locationRef, date2.foundEl]);
            case { obj instanceof Long }:
            case { obj instanceof Short }:
            case { obj instanceof Byte }:
                return "${obj} as ${obj.getClass().simpleName}"
            case { obj instanceof OneNestedField }:
                OneNestedField oneNestedField = obj as OneNestedField
                Object nestedField = oneNestedField.getNestedField()
                String asStr = writeObject(writer3, nestedField);
                return "new  ${obj.getClass().simpleName} ( ${asStr} )"
            case { obj instanceof JavaBean2 }:
                JavaBean2 bean2 = obj as JavaBean2;
                return javaBeanStore2.save(writer3, this, bean2);
            case { obj instanceof Map }:
                return writeSimpleMap(writer3, (Map) obj)
            case { obj instanceof FileChildLazyRef }:
                return writeFileChildLazyRef(writer3, obj as FileChildLazyRef)
            case { obj instanceof MethodClosure }:
                return writeMethodClosure(writer3, obj as MethodClosure, [])
            case { obj instanceof List }:
                return writeList(writer3, (List) obj)
            case { obj instanceof Collection }:
                return writeUnknownCollection(writer3, (Collection) obj)
            default:
                return writeUnknownObject(writer3, obj)
        }
    }


    String writeEnum(Writer3Import writer3, Enum ee) {
        writer3.addImport(ee.getClass())
        return "${ee.getClass().simpleName}.${ee.name()}"
    }

    String writeStringConstructor2(Class clazz, Writer3Import writer3, String objectAsStr) {
        writer3.addImport(clazz)
        String strrr = writeObject(writer3, objectAsStr)
        return " new ${clazz.getSimpleName()}(${strrr}) "
    }

    String writeConstructorWithArgs(Class clazz, Writer3Import writer3, List objects) {
        writer3.addImport(clazz)
        int counttt = -1
        String args = objects.collect {
            try {
                counttt++
                return writeObject(writer3, it)
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                failedWriteCountedEl(it, counttt, e)
                throw e
            }
        }.join(', ')
        return " new ${clazz.getSimpleName()}( ${args} ) "
    }

    String writeMethodClosure2(Writer3Import writer3, Closure ref, List args) {
        writeMethodClosure(writer3, ref as MethodClosure, args)
    }

    String writeMethodClosure(Writer3Import writer3, MethodClosure ref, List args) {
        String delegate1 = writeObject(writer3, ref.delegate)
        if (args.size() == 0) {
            return "${delegate1}.${ref.getMethod()}()"
        }
        List<String> argsS = []
        int i = -1
        args.each {
            i++
            try {
                argsS.add(writeObject(writer3, it));
            } catch (Throwable e) {
                log.warn("failed write ${i} ${e}")
                failedWriteCountedEl(it, i, e)
                throw e
            }
        }
        return "${delegate1}.${ref.getMethod()}( ${argsS.join(', ')} ) "
    }

    void failedWriteCountedEl(Object el, int countt, Throwable e) {

    }

    String writeFileChildLazyRef(Writer3Import writer3, FileChildLazyRef ref) {
        ToFileRef2 parentRef = ref.parentRef;
        ChildPattern child1 = ref.child
        if (parentRef instanceof ChildFileLazy) {
            //String parentRefS = writeObject(writer3, parentRef)
            if (child1 instanceof ExactChildPattern) {
                ExactChildPattern exactChildPattern = (ExactChildPattern) child1;
                //String childS = writeString(exactChildPattern.child);
                return writeMethodClosure2(writer3, parentRef.&childL, [exactChildPattern.child])
            }
            return writeMethodClosure2(writer3, parentRef.&childP, [child1])
        }
        return writeConstructorWithArgs(FileChildLazyRef, writer3, [parentRef, child1])
    }

    String write2ArgsConstructor(Writer3Import writer3, Class clazz, Object obj1, Object obj2) {
        writer3.addImport(clazz)
        String obj1S = writeObject(writer3, obj1)
        String obj2S = writeObject(writer3, obj2)
        return " new ${clazz.getSimpleName()}( ${obj1S}, ${obj2S} )"
    }

    String writeString(String s) {
        return stringWriterCurrent.writeString(s)
    }

    @Deprecated
    static String writeClass(Writer3Import writer3, Class clazz) {
        ClassSimpleWriter.writeClass(writer3, clazz)
    }

    @Deprecated
    String writeSimpleMapComplex(Writer3Import writer3, Map<String, Object> map) {
        return writeSimpleMap(writer3, map)
    }

    String writeSimpleMap(Writer3Import writer3, Map map) {
        return mapWriter.save(writer3, this, map)
    }

    String writeUnknownObject(Writer3Import writer3, Object obj) {
        obj.getClass().getConstructor(String)
        return writeStringConstructor2(obj.getClass(), writer3, obj.toString())
    }

//    String writeLocalDate(Writer3Import writer3, LocalDate date1) {
//
//    }

    String writeList(Writer3Import writer3, List list) {
        return listWriter.save(writer3, this, list)
    }


    String writeUnknownCollection(Writer3Import writer3, Collection collection1) {
        if (writeCollectionAsList) {
            return listWriter.save(writer3, this, new ArrayList(collection1))
        }
        return writeUnknownObject(writer3, collection1)
    }

}
