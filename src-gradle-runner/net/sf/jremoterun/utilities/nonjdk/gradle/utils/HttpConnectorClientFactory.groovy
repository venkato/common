package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.impl.client.CloseableHttpClient
import org.gradle.internal.resource.connector.ResourceConnectorFactory
import org.gradle.internal.resource.transport.http.HttpClientHelper
import org.gradle.internal.resource.transport.http.HttpConnectorFactory
import org.gradle.internal.resource.transport.http.HttpSettings
import org.gradle.internal.service.DefaultServiceRegistry

import java.util.logging.Logger

@CompileStatic
class HttpConnectorClientFactory implements HttpClientHelper.Factory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HttpClientHelper.Factory original;
    public HttpConnectorFactory factory1

    HttpConnectorClientFactory() {
        factory1 = findHttpFactory()
        original= getchOriginalHttpConnectorFactory(factory1)
    }

    HttpConnectorClientFactory(HttpClientHelper.Factory original) {
        this.original = original
    }

    @Override
    HttpClientHelper create(HttpSettings settings) {
        HttpClientHelper clientHelper = original.create(settings);
        JrrClassUtils.setFieldValue(clientHelper,'client',buildClient(settings))
        return clientHelper
    }


    /**
     * configure on demand
     */
    CloseableHttpClient buildClient(HttpSettings settings){
        JrrHttpUtils httpUtils = new JrrHttpUtils()
        httpUtils.addRequestLogger(true)
        httpUtils.addResponseLogger(true)
        httpUtils.createClient()
        return httpUtils.httpClient1
    }


    static HttpConnectorFactory findHttpFactory(){
        DefaultServiceRegistry services = GradleEnvsUnsafe.gradle.getServices() as DefaultServiceRegistry
        List<ResourceConnectorFactory> all1 = services.getAll(ResourceConnectorFactory)
        HttpConnectorFactory hr = all1.find {it instanceof HttpConnectorFactory} as HttpConnectorFactory
        if(hr==null){
            throw new Exception("HttpConnectorFactory not found in ${all1}")
        }
        return    hr
    }



    static HttpClientHelper.Factory getchOriginalHttpConnectorFactory(HttpConnectorFactory factory1){
        HttpClientHelper.Factory  original2 = JrrClassUtils.getFieldValue(factory1,'httpClientHelperFactory') as HttpClientHelper.Factory
        return original2
    }

    void patchHttpConnectorFactory2(){
        patchHttpConnectorFactory(factory1)
    }

    void patchHttpConnectorFactory(HttpConnectorFactory f){
        HttpClientHelper.Factory  original2 = JrrClassUtils.getFieldValue(f,'httpClientHelperFactory') as HttpClientHelper.Factory
        JrrClassUtils.setFieldValue(f,'httpClientHelperFactory',this)
    }

}
