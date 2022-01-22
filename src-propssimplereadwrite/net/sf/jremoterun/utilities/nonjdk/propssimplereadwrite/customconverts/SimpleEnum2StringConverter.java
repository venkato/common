package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts;

import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.ObjectToStringConverter;

import java.io.IOException;
import java.lang.reflect.Field;

public class SimpleEnum2StringConverter implements ObjectToStringConverter<Enum> {
    @Override
    public String convertToString(Field f, Enum object) throws IOException {
        return object.name();
    }

    @Override
    public Enum convertFromString(Field f, Class<Enum> type,String str) throws Exception {
        Enum t = Enum.valueOf((Class) f.getType(), str);
        return t;
    }

    @Override
    public Class getType() {
        throw new UnsupportedOperationException();
    }
}
