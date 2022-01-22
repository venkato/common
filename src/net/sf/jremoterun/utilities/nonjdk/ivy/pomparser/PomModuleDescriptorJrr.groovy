package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.plugins.parser.ModuleDescriptorParser
import org.apache.ivy.plugins.parser.m2.PomModuleDescriptorBuilder
import org.apache.ivy.plugins.repository.Resource;

import java.util.logging.Logger;

@CompileStatic
class PomModuleDescriptorJrr extends PomModuleDescriptorBuilder.PomModuleDescriptor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PomModuleDescriptorJrr(ModuleDescriptorParser parser, Resource res) {
        super(parser, res)
    }
}
