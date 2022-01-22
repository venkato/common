
package com.google.cloud.tools.jib.registry;

import com.google.cloud.tools.jib.hash.Digests;
import com.google.cloud.tools.jib.http.Response;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class RegistryEndpointProviderJrr extends AbstractManifestPuller {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public URL fullUrl;

    public OutputStream outputStream1;

    public RegistryEndpointProviderJrr(URL fullUrl, OutputStream outputStream1) {
        super(null, null, null);
        this.fullUrl = fullUrl;
        this.outputStream1 = outputStream1;
    }

    @Override
    Object computeReturn(ManifestAndDigest manifestAndDigest) {
        log.info("" + manifestAndDigest);
        return null;
    }

    @Override
    public Object handleResponse(Response response) throws IOException{
        Digests.computeDigest(response.getBody(), outputStream1).getDigest();
        outputStream1.flush();
        return null;
    }


    @Override
    public List<String> getAccept() {
        return Collections.emptyList();
    }

    @Override
    public URL getApiRoute(String apiRouteBase) throws MalformedURLException {
        return fullUrl;
    }

    @Override
    public String getActionDescription() {
        return "custom for " + fullUrl;
    }
}
