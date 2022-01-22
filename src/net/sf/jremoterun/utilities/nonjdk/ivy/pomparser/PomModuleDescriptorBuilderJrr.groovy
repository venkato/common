package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.module.descriptor.Configuration
import org.apache.ivy.plugins.parser.ModuleDescriptorParser
import org.apache.ivy.plugins.parser.ParserSettings
import org.apache.ivy.plugins.parser.m2.PomModuleDescriptorBuilder
import org.apache.ivy.plugins.repository.Resource;

import java.util.logging.Logger;

@CompileStatic
class PomModuleDescriptorBuilderJrr extends PomModuleDescriptorBuilder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static final String IVY_XML_MAVEN_NAMESPACE_URI_JRR = "http://ant.apache.org/ivy/maven";

    PomModuleDescriptorBuilderJrr(ModuleDescriptorParser parser, Resource res, ParserSettings ivySettings) {
        super(parser, res, ivySettings)


    }

    void setMyRef2(Resource res, PomModuleDescriptorJrr pomModuleDescriptorJrr) {
        //ivyModuleDescriptor = pomModuleDescriptorJrr
        JrrClassUtils.setFieldValue(this, "ivyModuleDescriptor", pomModuleDescriptorJrr);
        pomModuleDescriptorJrr.setResolvedPublicationDate(new Date(res.getLastModified()));
        for (Configuration m2conf : MAVEN2_CONFIGURATIONS) {
            pomModuleDescriptorJrr.addConfiguration(m2conf);
        }
        pomModuleDescriptorJrr.setMappingOverride(true);
        pomModuleDescriptorJrr.addExtraAttributeNamespace("m", IVY_XML_MAVEN_NAMESPACE_URI_JRR);
    }




}
