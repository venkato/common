package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.json

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.ClassChecker2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI
import org.codehaus.groovy.runtime.MethodClosure

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
abstract class Writer3Json implements Writer3Import {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> header =[];
    public List<String> trailer =[];

    public List<LineInfo> body = [];


    Writer3Json() {
    }


    void writeAdditionalMethods(){

    }

    abstract List<String> transformBody()

    String buildResult() {
        writeAdditionalMethods()
        List<String> bodyTransformaed = transformBody()
        bodyTransformaed.addAll(0, header)
        bodyTransformaed.addAll(trailer)

        String res4 = joinLines(bodyTransformaed)
        return res4;
    }

    abstract String joinLines(List<String> res)

    @Deprecated
    @Override
    void addImport(Class clazz) {
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
        return simpleName1
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
        return simpleName1
    }


}
