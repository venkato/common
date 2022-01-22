package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser.ivyfile

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.parser.ModuleDescriptorParser
import org.apache.ivy.plugins.repository.Resource;

import java.util.logging.Logger;

@CompileStatic
class DefaultModuleDescriptorJrr extends DefaultModuleDescriptor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DefaultModuleDescriptorJrr(ModuleRevisionId id, String status, Date pubDate) {
        super(id, status, pubDate)
    }

    DefaultModuleDescriptorJrr(ModuleRevisionId id, String status, Date pubDate, boolean isDefault) {
        super(id, status, pubDate, isDefault)
    }

    DefaultModuleDescriptorJrr(ModuleDescriptorParser parser, Resource res) {
        super(parser, res)
    }
}
