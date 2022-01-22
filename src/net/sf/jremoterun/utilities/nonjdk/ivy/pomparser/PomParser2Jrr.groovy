package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.mdep.ivy.URLResourceJrr
import net.sf.jremoterun.utilities.nonjdk.ivy.pomparser.ivyfile.XmlModuleDescriptorParserJrr
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.descriptor.ModuleDescriptor
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.osgi.core.OSGiManifestParser
import org.apache.ivy.plugins.parser.ModuleDescriptorParser
import org.apache.ivy.plugins.parser.ModuleDescriptorParserRegistry
import org.apache.ivy.plugins.parser.m2.PomModuleDescriptorParser
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser
import org.apache.ivy.plugins.repository.url.URLResource

import java.util.logging.Logger

@CompileStatic
class PomParser2Jrr {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public IvySettings ivySettings = new IvySettings();
    public URLResource res;
    public boolean validate = false;

    void setResourceFromFile(File f) {
        res = new URLResourceJrr(f.toURL())
    }

    void copySettingsFromJrrDefault(IvyDepResolver2 depResolver2) {
        ivySettings = depResolver2.ivySettings
    }

    ModuleDescriptor parse() {
        ModuleDescriptorParser parser = ModuleDescriptorParserRegistry.getInstance().getParser(res);
        return parser.parseDescriptor(ivySettings, res.getURL(), res, validate);
    }

    DefaultModuleDescriptor parsePom(File f) {
        URLResourceJrr res = new URLResourceJrr(f.toURL())
        return new PomModuleDescriptorParserJrr().parseDescriptor(ivySettings, res.getURL(), res, validate);
    }

    public static volatile boolean myParserAdded = false

    static void addJrrParsers() {
        if (myParserAdded) {
            log.info "jrr paprser already added"
        }else{
            myParserAdded = true
            addJrrParsersImpl(ModuleDescriptorParserRegistry.getInstance())
        }
    }

    static void addJrrParsersImpl(ModuleDescriptorParserRegistry moduleDescriptorParserRegistry) {
        //ModuleDescriptorParserRegistry moduleDescriptorParserRegistry = ModuleDescriptorParserRegistry.getInstance()
        XmlModuleDescriptorParserJrr xmlModuleDescriptorParserJrr = new XmlModuleDescriptorParserJrr()
        xmlModuleDescriptorParserJrr.setSelf()
        moduleDescriptorParserRegistry.addParser(xmlModuleDescriptorParserJrr);
        moduleDescriptorParserRegistry.addParser(new PomModuleDescriptorParserJrr());
        moduleDescriptorParserRegistry.addParser(OSGiManifestParser.getInstance());

    }


}
