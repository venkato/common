package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser.ivyfile

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.plugins.parser.ParserSettings
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;

import java.util.logging.Logger;

@CompileStatic
class XmlModuleDescriptorParserJrr extends XmlModuleDescriptorParser{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    protected Parser newParser(ParserSettings ivySettings) {
        return new IvyParserJrr(this,ivySettings)
    }


    void setSelf(){
        JrrClassUtils.setFieldValue(XmlModuleDescriptorParser,'INSTANCE',this)
    }
}
