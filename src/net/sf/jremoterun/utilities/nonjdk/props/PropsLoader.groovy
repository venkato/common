package net.sf.jremoterun.utilities.nonjdk.props

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils;

import java.util.logging.Logger;

@CompileStatic
class PropsLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Properties parseFromString(String s){
        Properties properties1 = new Properties()
        StringReader stringReader = new StringReader(s)
        properties1.load(stringReader)
        stringReader.close()
        return properties1
    }


    static Properties readPropsFromFile(File file) {
        assert file.exists()
        Properties props = new Properties()
        BufferedInputStream inputStream = file.newInputStream()
        try {
            props.load(inputStream)
        } finally {
            JrrIoUtils.closeQuietly2(inputStream, log)
        }
        return props
    }


    static String saveToString(Properties props){
        StringWriter sw = new StringWriter()
        props.store(sw,'')
        sw.flush()
        sw.close()
        return sw.toString()
    }
}
