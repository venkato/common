package net.sf.jremoterun.utilities.nonjdk.idea.classloaderinit

import groovy.transform.CompileStatic
import javassist.CannotCompileException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ExprEditorIdea202531 extends ExprEditor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean first = true

    String ideaPluginId;

    ExprEditorIdea202531(String ideaPluginId) {
        this.ideaPluginId = ideaPluginId
    }

    @Override
    void edit(MethodCall m) throws CannotCompileException {

        if( first &&  m.getMethodName() =='loadClassInsideSelf'){
            log.info "inside methdo1"
            first =false
            m.replace """   
if("${ideaPluginId}".equals(getPluginId().toString() ) ) {
    \$_= null; 
}else{
    \$_ = loadClassInsideSelf(name, fileName, packageNameHash);
}
"""
        }else {
            super.edit(m)
        }
    }


}
