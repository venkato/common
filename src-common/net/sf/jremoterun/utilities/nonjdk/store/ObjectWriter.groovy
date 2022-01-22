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
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CharWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ClassSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CustomWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.DateWithFormatWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.FileSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.IntegerWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.JavaBeanStore2
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ListSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.LongWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.MapSimpleWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.NumSeparatorWriter
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.StringWriter
import org.codehaus.groovy.runtime.MethodClosure
import org.joda.time.LocalDate

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class ObjectWriter implements ObjectWriterI{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static Map<String, CustomWriter> customsS = [:]
    public static HashSet<String> simpleStringWritersS = []

    public Map<String, CustomWriter> customsCurrent = new HashMap<>(customsS)
    public HashSet<String> simpleStringWritersCurrent = new HashSet<>(simpleStringWritersS)
    public static CustomWriter<Map> mapWriterS = new MapSimpleWriter();
    public static CustomWriter<List> listWriterS = new ListSimpleWriter();
    public static StringWriter stringWriterS = new StringWriter();
    public StringWriter stringWriterCurrent = stringWriterS;
    public static CustomWriter<JavaBean2> javaBeanStore2 = (CustomWriter)new JavaBeanStore2(false,true);
    public static CustomWriter<JavaBean3> javaBeanStore3 = (CustomWriter)new JavaBeanStore2(true,false);
    public CustomWriter<Map> mapWriter = mapWriterS;
    public CustomWriter<List> listWriter = listWriterS;
    public boolean writeCollectionAsList = false
    public String nullObject = 'null'
    public CustomWriter<Integer> integerWriter = new IntegerWriter(null)
    public CustomWriter<Long> longWriter = new LongWriter(null)
    public CustomWriter<Character> charWriter = new CharWriter()


    void addSimpleStringWriter2(Class clazz) {
        simpleStringWritersCurrent.add(clazz.getName())
    }

    static void addSimpleStringWriter(Class clazz) {
        simpleStringWritersS.add(clazz.getName())
    }

    static void addAddCustomWriter(CustomWriter cw) {
        customsS.put(cw.getDataClass().getName(), cw)
    }

    void addAddCustomWriter2(CustomWriter cw) {
        customsCurrent.put(cw.getDataClass().getName(), cw)
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

    void writeIntAsHuman(NumSeparatorWriter numSeparatorWriter){
        ((IntegerWriter)integerWriter).numSeparatorWriter = numSeparatorWriter
        ((LongWriter)longWriter).numSeparatorWriter = numSeparatorWriter
    }

    @Override
    String writeObject(Writer3Import writer3, Object obj) {
        try {
            writeObjectImpl(writer3, obj)
        } catch (Exception e) {
            log.info("failed write ${obj.getClass().getName()} ${e}")
            throw e
        }
    }

    String useCustomWriter( CustomWriter customWriter ,Writer3Import writer3, Object obj){
        return customWriter.save(writer3,this,obj)
    }

    protected String writeObjectImpl(Writer3Import writer3, Object obj) {
        if (obj == null) {
            return nullObject;
        }
        String classNameFull = obj.getClass().getName()
        if (obj != null) {
            CustomWriter customWriter = customsCurrent.get(classNameFull)
            if (customWriter != null) {
                return useCustomWriter(customWriter,writer3, obj)
            }
            if (simpleStringWritersCurrent.contains(classNameFull)) {
                return writeStringConstructor(writer3,obj.getClass(),  obj.toString())
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
                return writeConstructorWithArgs(writer3, RefLink, [ee.enumm])
            case { obj instanceof SimpleDateFormat }:
                SimpleDateFormat sdf = obj as SimpleDateFormat
                return writeStringConstructor(writer3, SimpleDateFormat, sdf.toPattern());
            case { obj instanceof GradleRef }:
                GradleRef repo = obj as GradleRef
                //return write2ArgsConstructor(writer3, GradleRef, repo.mavenId, repo.suffix);
                return writeConstructorWithArgs( writer3, GradleRef,  [repo.mavenId, repo.suffix]);
            case { obj instanceof GeneralBiblioRepository }:
                GeneralBiblioRepository repo = obj as GeneralBiblioRepository
                return writeConstructorWithArgs(writer3, GeneralBiblioRepository, [ repo.name(), repo.url]);
            case { obj instanceof MavenIdAndRepo }:
                MavenIdAndRepo mavenIdAndRepo = obj as MavenIdAndRepo
                return writeConstructorWithArgs(writer3, MavenIdAndRepo, [ mavenIdAndRepo.m, mavenIdAndRepo.repo]);
            case { obj instanceof BinaryWithSource3 }:
                BinaryWithSource3 bs = obj as BinaryWithSource3
                Object source2
                if (bs.source.size() == 1) {
                    source2 = bs.source[0]
                } else {
                    source2 = bs.source
                }
                return writeConstructorWithArgs(writer3, BinaryWithSource3,  [bs.binary, source2])
            case { obj instanceof BinaryWithSource2 }:
                BinaryWithSource2 bs = obj as BinaryWithSource2
                Object source2
                if (bs.source.size() == 1) {
                    source2 = bs.source[0]
                } else {
                    source2 = bs.source
                }
                return writeConstructorWithArgs(writer3, BinaryWithSource2,  [bs.binary, source2])
            case { obj instanceof BinaryWithSource }:
                BinaryWithSource bs = obj as BinaryWithSource
                return writeConstructorWithArgs(writer3, BinaryWithSource,  [bs.binary, bs.source])
            case { obj instanceof GitBinaryAndSourceRef }:
                GitBinaryAndSourceRef bs = obj as GitBinaryAndSourceRef
                return writeConstructorWithArgs(writer3, GitBinaryAndSourceRef,  [bs.repo, bs.pathInRepo, bs.src])
            case { obj instanceof FileToFileRef }:
                FileToFileRef bs = obj as FileToFileRef
                return writeConstructorWithArgs(writer3, FileToFileRef,  [bs.file])
            case { obj instanceof SvnRef }:
                SvnRef svnRef = obj as SvnRef
                return writeConstructorWithArgs(writer3, SvnRef,  [svnRef.repo, svnRef.branch])
            case { obj instanceof SvnSpec }:
                SvnSpec svnSpec = obj as SvnSpec
                return writeStringConstructor(writer3, SvnSpec,  svnSpec.repo)
            case { obj instanceof GitRef }:
                GitRef gitRef = obj as GitRef
                return writeConstructorWithArgs(writer3, GitRef,  [gitRef.repo, gitRef.pathInRepo])
            case { obj instanceof EnumIdea }:
                EnumIdea ev = obj as EnumIdea
                String value = writeObject(writer3, ev.getName())
                return "${writer3.addImportWithName(obj.getClass())}.getE( ${value} )"
            case { obj instanceof Integer }:
                return useCustomWriter(integerWriter,writer3,obj)
            case { obj instanceof LocalDate }:
                LocalDate date2 = (LocalDate) obj;
                return writeConstructorWithArgs(writer3, LocalDate,  [date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth()]);
            case { obj instanceof FieldRef }:
                FieldRef date2 = (FieldRef) obj;
                return writeConstructorWithArgs(writer3, FieldRef,  [date2.clRef, date2.fieldName]);
            case { obj instanceof MethodRef }:
                MethodRef date2 = (MethodRef) obj;
                return writeConstructorWithArgs(writer3, MethodRef,  [date2.clRef, date2.methodName, date2.paramsCount]);
            case { obj instanceof ConstructorRef }:
                ConstructorRef date2 = (ConstructorRef) obj;
                return writeConstructorWithArgs(writer3, ConstructorRef,  [date2.clRef, date2.paramsCount]);
            case { obj instanceof LocationRef }:
                LocationRef date2 = (LocationRef) obj;
                if (date2.f == null) {
                    return writeConstructorWithArgs(writer3, LocationRef,  [date2.clRef, date2.lineNumber]);
                } else {
                    return writeConstructorWithArgs(writer3, LocationRef, [date2.f, date2.lineNumber]);
                }
            case { obj instanceof Character }:
                return useCustomWriter(charWriter,writer3,obj)
            case { obj instanceof Long }:
                return useCustomWriter(longWriter,writer3,obj)
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
                return useCustomWriter(javaBeanStore2,writer3,  bean2);
            case { obj instanceof JavaBean3 }:
                JavaBean3 bean2 = obj as JavaBean3;
                return useCustomWriter(javaBeanStore3,writer3, bean2);
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
        return "${writer3.addImportWithName(ee.getClass())}.${ee.name()}"
    }

    String writeStringConstructor(Writer3Import writer3, Class clazz, String objectAsStr) {
        //writer3.addImport(clazz)
        String strrr = writeObject(writer3, objectAsStr)
        return " new ${writer3.addImportWithName(clazz)}(${strrr}) "
    }

    String writeConstructorWithArgs(Writer3Import writer3, Class clazz, List objects) {
        //writer3.addImport(clazz)
        int counttt = -1
        List<String> args4 = objects.collect {
            try {
                counttt++
                return writeObject(writer3, it)
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                failedWriteCountedEl(it, counttt, e)
                throw e
            }
        }
        //String args =
        return " new ${writer3.addImportWithName(clazz)}( ${args4.join(argsSeparator)} ) "
    }

    String writeMethodClosure2(Writer3Import writer3, Closure ref, List args) {
        writeMethodClosure(writer3, ref as MethodClosure, args)
    }

    public String argsSeparator = ', '

    /**
     * using Closure instead of MethodClosure to reduce lines of codes for users
     */
    String writeMethodClosure(Writer3Import writer3, Closure ref1, List args) {
        MethodClosure ref = ref1 as MethodClosure
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
        return "${delegate1}.${ref.getMethod()}( ${argsS.join(argsSeparator)} ) "
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
                return writeMethodClosure(writer3, parentRef.&childL, [exactChildPattern.child])
            }
            return writeMethodClosure(writer3, parentRef.&childP, [child1])
        }
        return writeConstructorWithArgs(writer3, FileChildLazyRef, [parentRef, child1])
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
        return useCustomWriter(mapWriter,writer3,  map)
    }

    String writeUnknownObject(Writer3Import writer3, Object obj) {
        obj.getClass().getConstructor(String)
        return writeStringConstructor(writer3, obj.getClass(), obj.toString())
    }

//    String writeLocalDate(Writer3Import writer3, LocalDate date1) {
//
//    }

    String writeList(Writer3Import writer3, List list) {
        return listWriter.save(writer3, this, list)
    }


    String writeUnknownCollection(Writer3Import writer3, Collection collection1) {
        if (writeCollectionAsList) {
            return useCustomWriter( listWriter,writer3,  new ArrayList(collection1))
        }
        return writeUnknownObject(writer3, collection1)
    }

}
