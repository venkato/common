package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts;

import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.ObjectToStringConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class SimpleClass2StringConverter implements ObjectToStringConverter<Class> {
    @Override
    public String convertToString(Field f, Class object) throws IOException {
        return object.getName();
    }

    @Override
    public Class convertFromString(Field f, Class<Class> type,String str) throws Exception {
        return getClass().getClassLoader().loadClass(str);
    }

    @Override
    public Class getType() {
        return Class.class;
    }
}
