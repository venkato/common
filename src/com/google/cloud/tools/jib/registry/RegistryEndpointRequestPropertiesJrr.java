package com.google.cloud.tools.jib.registry;

import net.sf.jremoterun.utilities.JrrClassUtils;

public class RegistryEndpointRequestPropertiesJrr {
    public RegistryEndpointRequestProperties registryEndpointRequestProperties;

    public RegistryEndpointRequestPropertiesJrr(RegistryClient registryClient) throws NoSuchFieldException, IllegalAccessException {
         registryEndpointRequestProperties = getRegistryProps(registryClient);
    }

    static RegistryEndpointRequestProperties getRegistryProps(RegistryClient registryClient) throws NoSuchFieldException, IllegalAccessException {
        return (RegistryEndpointRequestProperties) JrrClassUtils.getFieldValue(registryClient, "registryEndpointRequestProperties");
    }

    public String getServerUrl() {
        return registryEndpointRequestProperties.getServerUrl();
    }

    public String getImageName() {
        return registryEndpointRequestProperties.getImageName();
    }

    public String getSourceImageName() {
        return registryEndpointRequestProperties.getSourceImageName();
    }


}
