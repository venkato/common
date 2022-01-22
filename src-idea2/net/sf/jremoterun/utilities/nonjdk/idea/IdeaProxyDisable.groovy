package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.util.proxy.CommonProxyCompatibility
import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class IdeaProxyDisable extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    IdeaProxyDisable() {
        super(com.intellij.util.proxy.CommonProxy)
    }

    void disableIdeaProxy() {
        Class cl = com.intellij.util.proxy.CommonProxy
//        CtClass pool = JrrJavassistUtils.getClassFromDefaultPool(cl)
        CtMethod method = JrrJavassistUtils.findMethodByCount(cl, cc, 'isInstalledAssertion', 0)
        method.setBody(null)
        doRedefine();
    }

    void ideaProxyDefault1() {
        Class cl = com.intellij.util.proxy.CommonProxy
//        CtClass pool = JrrJavassistUtils.getClassFromDefaultPool(cl)
        // com.intellij.util.proxy.CommonProxy.select(java.net.URI)
        CtMethod method = JrrJavassistUtils.findMethodByCount(cl, cc, 'select', 1)
        //ProxySelector.getDefault().select($1)
        method.setBody '''
    {
        return java.net.ProxySelector.getDefault($1);
        }
'''


        doRedefine();
    }



    void ideaProxyDefault2(ProxySelector proxySelector) {
        assert proxySelector!=null
        Class cl = com.intellij.util.proxy.CommonProxy
//        CtClass pool = JrrJavassistUtils.getClassFromDefaultPool(cl)
        // com.intellij.util.proxy.CommonProxy.select(java.net.URI)
        CtMethod method = JrrJavassistUtils.findMethodByCount(cl, cc, 'select', 1)
        //ProxySelector.getDefault().select($1)
        IdeaProxyStore ideaProxyStore = new IdeaProxyStore(proxySelector)
        net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector.putInector2(IdeaProxyStore,ideaProxyStore)
        method.setBody """
            {
               ${net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector.createSharedObjectsHookVar2(IdeaProxyStore)}
               return ${net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector.myHookVar}.get(\$1);    
            }
"""


        doRedefine();
    }
}
