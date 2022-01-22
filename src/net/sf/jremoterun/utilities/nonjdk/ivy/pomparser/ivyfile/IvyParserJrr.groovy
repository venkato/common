package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser.ivyfile

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.descriptor.ModuleDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.parser.ModuleDescriptorParser
import org.apache.ivy.plugins.parser.ParserSettings
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser
import org.apache.ivy.plugins.repository.Resource
import org.apache.ivy.plugins.repository.ResourceHelper
import org.xml.sax.Attributes
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException
import java.lang.reflect.Field
import java.text.ParseException;
import java.util.logging.Logger;

@CompileStatic
class IvyParserJrr extends XmlModuleDescriptorParser.Parser{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public XmlModuleDescriptorParserJrr parserJrr;
    public Field resourceField

    IvyParserJrr(XmlModuleDescriptorParserJrr parser, ParserSettings ivySettings) {
        super(parser, ivySettings)
        parserJrr = parser
        resourceField = JrrClassUtils.findField(this.getClass(), 'res')
    }

    DefaultModuleDescriptor createDefaultModuleDescriptor(Resource res){
        return new DefaultModuleDescriptorJrr(parserJrr, res);
    }

    @Override
    void setResource(Resource res) {
        //super.setResource(res)
        //this.res = res; // used for log and date only
        resourceField.set(this, res)
        DefaultModuleDescriptor md2 = createDefaultModuleDescriptor(res);
        setMd(md2)
        md2.setLastModified(ResourceHelper.getLastModifiedOrDefault(res));

    }

    @Override
    protected ModuleDescriptor parseOtherIvyFile(ModuleRevisionId parentMrid) throws ParseException {
        return super.parseOtherIvyFile(parentMrid)
    }

    @Override
    protected void includeConfStarted(Attributes attributes) throws SAXException, IOException, ParserConfigurationException, ParseException {
        super.includeConfStarted(attributes)
    }

    @Override
    protected void dependencyStarted(Attributes attributes) {
        super.dependencyStarted(attributes)
    }
}
