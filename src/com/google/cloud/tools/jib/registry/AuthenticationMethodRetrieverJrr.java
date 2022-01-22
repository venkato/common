package com.google.cloud.tools.jib.registry;

import com.google.cloud.tools.jib.http.FailoverHttpClient;

public class AuthenticationMethodRetrieverJrr extends AuthenticationMethodRetriever{
    public AuthenticationMethodRetrieverJrr(RegistryClient registryEndpointRequestProperties, String userAgent, FailoverHttpClient httpClient) throws NoSuchFieldException, IllegalAccessException {
        super(RegistryEndpointRequestPropertiesJrr.getRegistryProps(registryEndpointRequestProperties), userAgent, httpClient);
    }
}
