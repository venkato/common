package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.plugins.parser.m2.PomReader
import org.apache.ivy.plugins.repository.Resource
import org.xml.sax.SAXException;

import java.util.logging.Logger;

@CompileStatic
class PomReaderJrr extends PomReader{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PomReaderJrr(URL descriptorURL, Resource res) throws IOException, SAXException {
        super(descriptorURL, res)
    }
}
