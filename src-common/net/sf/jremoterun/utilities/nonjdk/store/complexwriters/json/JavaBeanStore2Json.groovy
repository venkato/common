package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.json;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCommon;
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import;
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.JavaBeanStore2;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

@CompileStatic
public class JavaBeanStore2Json extends JavaBeanStore2 {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public String csvSeparator = ", ";


    public JavaBeanStore2Json(boolean writeNull, boolean writeAsProp) {
        super(writeNull, writeAsProp);
    }

    @Override
    public String writeBeanELs(Writer3Import writer3, Object javaBean, List<String> resuult2) {
        return resuult2.join( csvSeparator);
    }

    @Override
    public String writeProp(final Field fieldName, final String serValue, Object javaBean) {
        if (writeAsProp) {
            return "\"" + fieldName.getName() + "\": " + serValue;
        }

        return serValue;
    }

}
