package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger


@CompileStatic
class InetAddressRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    InetAddressRed() {
        super(InetAddress)
    }

    void redefindeDnsResolving() {
        Class class1 = InetAddress;
        // exists in java21 ??
        final CtMethod method = JrrJavassistUtils.findMethodByCount(class1, cc, "getAllByName", 2);
        method.insertBefore """
if(\$1!=null && \$1.indexOf('.')==-1  && \$1.length() >1 ){
    if( \$1.toUpperCase().startsWith("myhost")){
        \$1 = "myhost.fullname";    
    }else{
        if(!"localhost".equals(\$1.toLowerCase())){
           \$1 = \$1+".suffix";
        }
    }
}
""";
        doRedefine()
    }


}
