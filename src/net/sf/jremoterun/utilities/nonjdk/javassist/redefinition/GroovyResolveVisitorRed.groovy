package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class GroovyResolveVisitorRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    GroovyResolveVisitorRed() {
        super(org.codehaus.groovy.control.ResolveVisitor)
    }

    void redefineClassLoader() throws Exception {
        Class class1 = org.codehaus.groovy.control.ResolveVisitor
        CtBehavior method1;
        method1 = JrrJavassistUtils.findConstructorByCount(class1, cc, 1);
        method1.insertAfter("""
this.classNodeResolver = compilationUnit.classNodeResolver;
String cl1 = compilationUnit.classNodeResolver.getClass().getName();
if(! "net.sf.jremoterun.utilities.nonjdk.compiler3.ClassNodeResolverJrr".equals(cl1)){
   System.out.println( "classNodeResolver = "+cl1);
   Thread.dumpStack();
}
""");
        doRedefine()

    }


}
