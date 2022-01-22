package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.javassist.CodeInjector;

import java.util.logging.Logger;

@CompileStatic
class IdeaProxyStore extends InjectedCode{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProxySelector proxySelectorNested;

    IdeaProxyStore(ProxySelector proxySelectorNested) {
        this.proxySelectorNested = proxySelectorNested
    }

    @Override
    Object getImpl(Object key) throws Exception {
        return select(key as URL)
    }

    List<Proxy> select(URL uri){
        return proxySelectorNested.select(uri.toURI())
    }
}
