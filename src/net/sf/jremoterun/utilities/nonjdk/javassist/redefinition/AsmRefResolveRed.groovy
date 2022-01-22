package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class AsmRefResolveRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    AsmRefResolveRed() {
        super(org.codehaus.groovy.ast.decompiled.AsmReferenceResolver)
    }

    void redefineResolveMethod() throws Exception {
        Class class1 = org.codehaus.groovy.ast.decompiled.AsmReferenceResolver
        CtBehavior method1;
        method1 = JrrJavassistUtils.findMethodByCount(class1, cc, 'resolveClass', 1);
        method1.insertBefore("""

resolver = unit.getClassNodeResolver();
if(false){
String cl1 = resolver.getClass().getName();
if(! "net.sf.jremoterun.utilities.nonjdk.compiler3.ClassNodeResolverJrr".equals(cl1)){
   System.out.println( "classNodeResolver = "+cl1);
   Thread.dumpStack();
}
}

""");
        doRedefine()
    }

    void redefineConstructor() throws Exception {
        Class class1 = org.codehaus.groovy.ast.decompiled.AsmReferenceResolver
        CtBehavior method1;
        method1 = JrrJavassistUtils.findConstructorByCount(class1, cc, 2);
        method1.insertAfter("""

resolver = unit.getClassNodeResolver();
String cl1 = resolver.getClass().getName();
if(! "net.sf.jremoterun.utilities.nonjdk.compiler3.ClassNodeResolverJrr".equals(cl1)){
   System.out.println( "classNodeResolver = "+cl1);
   Thread.dumpStack();
}


""");
        doRedefine()

    }


}
