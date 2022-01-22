package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.runtime.MethodClosure

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class Writer3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static boolean addCompileStaticDefault = true

    List<String> header = [];

    List<String> importss = []
    private List<String> simpleClassNameUsed = []
    List<String> importsStatic = []

    List<String> bodyManuallyAddedEnd = [];
    List<String> body = [];


    boolean addCompileStatic = addCompileStaticDefault

    Writer3() {
    }

    void addCreatedAtHeader() {
        header.add "// created at ${new SimpleDateFormat('yyyy-MM-dd  HH:mm').format(new Date())}" as String
    }


    List<String> addAnotations() {
        if (addCompileStatic) {
            addImport(CompileStatic)
            return ['@CompileStatic']
        }
        return []
    }

    public boolean doCheckResult = true

    String buildResult() {
        List<String> res = header + importss.sort().collect { "import ${it} ;" as String } + importsStatic.collect {
            "import static ${it} ;" as String
        } + ['',''] + body+bodyManuallyAddedEnd + ['']
        String res3 = res.join('\n');
        if(doCheckResult) {
            GroovyFileChecker.analize(res3, CompilePhase.CANONICALIZATION, true, false)
        }
        return res3;
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

    void addImport(Class clazz) {
        if (importss.contains(clazz.getName())) {

        } else {
            String simpleName1 = clazz.getSimpleName()
            if(simpleClassNameUsed.contains(simpleName1)){
                String containsss = importss.find { it.contains('.' + simpleName1) }
                if(containsss==null){
                    containsss = importss.unique().sort()
                }
                throw new Exception("Current import ${clazz.getName()}  conflicts with another import : ${containsss}")
            }
            importss.add(clazz.getName())
            simpleClassNameUsed.add(simpleName1)
        }
    }

    String generateGetProperty(String propName) {
        return " binding.getProperty('${propName}') "
    }


    void addParentMethodCall(ObjectWriter objectWriter, MethodClosure methodName, List args) {
        addParentMethodCall(objectWriter, methodName.getMethod(), args)
    }

    void addParentMethodCall(ObjectWriter objectWriter, String methodName, List args) {
        Writer3 r3 = this
        List<String> args4 = args.collect { objectWriter.writeObject(r3, it) }
        String r = "${methodName}( ${args4.join(',')} ) ;"
        body.add r
    }

}
