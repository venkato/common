package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
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
    public boolean writeCreatedAt = true
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
            addImport(CompileStatic)
            return ['@CompileStatic']
        }
        return []
    }


    String buildResult() {
        List<String> anotations = addAnotations()
        String classDeclarationName = getClassDeclarationName()
        String mainMethod = getMainMethod()
        List<String> bodyTransformaed = transformBody()
        // above methods add imports
        List<String> res = [];
        if (writeCreatedAt) {
            addCreatedAtHeader()
        }


        res += header;
        res += importss.collect { "import ${it} ${semiColumn}" as String };
        res += importsStatic.collect { "import static ${it} ${semiColumn}" as String }
        res.addAll anotations
        res.add classDeclarationName
        res.addAll fieldsOfGeneratedClass
        if (addOverride) {
            res.add '@Override'
        }
        res.add mainMethod
        res += ['']
        res += bodyTransformaed
        res += bodySuffixBlocks.collect {it.toString()}
        res += bodyManuallyAddedEnd.collect {it.toString()}
        res += ['']
        res += ['} } ']
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

    List<String> transformBody() {
        List<String> r = []
        List<List<String>> body1 = splitBody()
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

    List<List<String>> splitBody() {
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

    @Override
    void addImport(Class clazz) {
        if (importss.contains(clazz.getName())) {

        } else {
            String simpleName1 = clazz.getSimpleName()
            if (simpleClassNameUsed.contains(simpleName1)) {
                String containsss = importss.find { it.contains('.' + simpleName1) }
                if (containsss == null) {
                    containsss = importss.unique().sort()
                }
                throw new Exception("Current import ${clazz.getName()}  conflicts with another import : ${containsss}")
            }
            importss.add(clazz.getName())
            simpleClassNameUsed.add(simpleName1)
        }
    }




}
