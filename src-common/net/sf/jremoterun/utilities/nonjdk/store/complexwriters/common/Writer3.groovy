package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI
import org.codehaus.groovy.runtime.MethodClosure

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
abstract class Writer3 implements Writer3Import {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static boolean addCompileStaticDefault = true

    public List<String> header = [];

    public List<String> importss = []
    private List<String> simpleClassNameUsed = []
    public List<String> importsStatic = []

    public List<LineInfoI> bodyManuallyAddedEnd = [];
    public List<String> fieldsOfGeneratedClass = [];
    public List<LineInfoI> body = [];
    public List<LineInfoI> bodySuffixBlocks = [];

    public String classNameGenerated = 'Config'
    public boolean addOverride = true


    public boolean addCompileStatic = addCompileStaticDefault
    public boolean doCheckResult = true
    public boolean writeCreatedAt = false
    public static String varName = 'b'
    public String varNameThis = varName
    public String semiColumn = ';'

    Writer3() {
    }

    void addCreatedAtHeader() {
        header.add "// created at ${new SimpleDateFormat('yyyy-MM-dd  HH:mm').format(new Date())}" as String
    }


    abstract String getClassDeclarationName()


    abstract String getMainMethod();

    abstract String generateSyntheticMethod(String methodName);

    List<String> addAnotations() {
        if (addCompileStatic) {
            return ['@' + addImportWithName(CompileStatic)]
        }
        return []
    }




    List<LineInfoI> writeAdditionalMethodsBefore(){
        return []
    }
    List<LineInfoI> writeAdditionalMethodsAfter(){
        return []
    }

    public static String overrideS = '@Override'

    public List<LineInfoI> beforeAddtionalMethods = []
    public List<LineInfoI> afterAddtionalMethods = []

    void writeAdditionalMethods(){

    }

    String buildResult() {
        writeAdditionalMethods()
        List<String> anotations = addAnotations()
        String classDeclarationName = getClassDeclarationName()
        String mainMethod = getMainMethod()
        List<String> bodyTransformaed = transformBody(syntheticMethodsPrefix,body)
        // above methods add imports
        List<String> res = [];
        if (writeCreatedAt) {
            addCreatedAtHeader()
        }


        res += header;
        res += importss.sort().collect { "import ${it} ${semiColumn}" as String };
        res += importsStatic.sort().collect { "import static ${it} ${semiColumn}" as String }
        res.addAll anotations
        res.add classDeclarationName
        res.addAll fieldsOfGeneratedClass
        if(beforeAddtionalMethods.size()>0) {
            res+=''
            res.addAll beforeAddtionalMethods.collect { it.toString() }
            res+=''
        }
        if (addOverride) {
            res.add overrideS
        }
        res.add mainMethod
        res += ['']
        res += bodyTransformaed
        res += bodySuffixBlocks.collect { it.toString() }
        res += bodyManuallyAddedEnd.collect { it.toString() }
        res += ['']
        if(afterAddtionalMethods.size()>0) {
            res += ['}']
            res += ['']
            res.addAll afterAddtionalMethods.collect { it.toString() }
            res += ['}']
        }else{

            res+=['} }']
        }
        String res4 = joinLines(res)
        if (doCheckResult) {
            //GroovyFileChecker.analize(res4, CompilePhase.CANONICALIZATION, true, false)
            GroovyFileChecker.analize(res4)
        }
        return res4;
    }

    String joinLines(List<String> res) {
        return res.join('\n')
    }

    public static int maxLinesInMethodsS = 1000
    public static int maxSymbolsInMethodsS = 30_000
    public int maxLinesInMethods = maxLinesInMethodsS
    public int maxSymbolsInMethods = maxSymbolsInMethodsS
    public String syntheticMethodsPrefix = 'synthetic'

    List<String> transformBody(String syntheticMethodsPrefix,List<LineInfoI> body) {
        List<String> r = []
        List<List<String>> body1 = splitBody(body)
        int countt = 9
        body1.each {
            countt++
            if (countt > 10) {
                String methodName1 = syntheticMethodsPrefix + countt
                r.add "${methodName1}(${varNameThis})${semiColumn}".toString()
                r.add('}')
                r.add(generateSyntheticMethod(methodName1))
            }
            r.addAll(it)
        }
        return r;
    }

    List<List<String>> splitBody(List<LineInfoI> body) {
        List<List<String>> r = []
        List<String> current = []
        int symbolsCount = 0
        body.each {
            boolean doReset = false
            if (current.size() > 0) {
                if (current.size() >= maxLinesInMethods) {
                    doReset = true
                }
                if ((symbolsCount + it.getLineContent().length()) > maxSymbolsInMethods) {
                    doReset = true
                }
            }
            if (doReset) {
                r.add current
                current = []
                symbolsCount = 0
            }

            current.add(it.getLineContent())
            symbolsCount += it.getLineContent().length()
        }
        if (current.size() > 0) {
            r.add(current)
        }
        return r;
    }


    void addImportStatic(MethodRef methodRef) {
        String sig = "${methodRef.clRef.clRef.className}.${methodRef.methodName}"
        addImportStatic2 sig
    }

    void addImportStatic(MethodClosure methodClosure) {
        Class owner = methodClosure.getOwner() as Class
        addImportStatic(owner, methodClosure.getMethod())
    }

    void addImportStatic(Class clazz, String methodName) {
        String sig = "${clazz.getName()}.${methodName}"
        addImportStatic2 sig
    }

    void addImportStatic2(String raw) {
        if (importsStatic.contains(raw)) {

        } else {
            importsStatic.add raw
        }
    }

    @Deprecated
    @Override
    void addImport(Class clazz) {
        addImportWithName(clazz)
    }

    @Override
    String addImportWithName(Class clazz) {
        String longName = clazz.getName()
        if (longName.contains('$')) {
            return addImportNestedClass(clazz)
        }
        String simpleName1 = clazz.getSimpleName()
        if(!longName.contains('.')){
            assert simpleName1==longName
            return longName
        }
        addImportSpecific(longName, simpleName1)
        return simpleName1
    }

    void addImportSpecific(String longName, String simpleName1) {
        if (importss.contains(longName)) {
            assert simpleClassNameUsed.contains(simpleName1)
        } else {
            if (simpleClassNameUsed.contains(simpleName1)) {
                String containsss = importss.find { it.contains('.' + simpleName1) }
                if (containsss == null) {
                    containsss = importss.unique().sort().join(', ')
                }
                throw new Exception("Current import ${longName}  conflicts with another import : ${containsss}")
            }
            importss.add(longName)
            simpleClassNameUsed.add(simpleName1)
        }
    }

    public boolean writeNestedClassWithParent = true

    String addImportNestedClass(Class clazz) {
        return addImportNestedClass2(clazz, writeNestedClassWithParent)
    }

    String addImportNestedClass2(Class clazz, boolean refWithParent) {
        String name1 = clazz.getName()
        assert name1.contains('$')
        assert !name1.contains('$$')
        assert !name1.startsWith('$')
        assert !name1.endsWith('$0')
        assert !name1.endsWith('$1')
        assert !name1.endsWith('$2')
        assert !name1.endsWith('$3')
        if (refWithParent) {
            return net.sf.jremoterun.utilities.nonjdk.store.customwriters.ClassSimpleWriter.writeNestedClass(this,clazz)
//            String impl1 = addImportWithName(clazz.getDeclaringClass())
//            return impl1 + '.' + clazz.getSimpleName()
        }
        String simpleName1 = clazz.getSimpleName()
        String replace1 = name1.replace('$', '.')
        addImportSpecific(replace1, simpleName1)
        return simpleName1
    }


}
