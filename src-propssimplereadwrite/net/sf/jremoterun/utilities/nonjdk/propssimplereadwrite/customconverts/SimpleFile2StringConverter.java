package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts;

import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.ObjectToStringConverter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class SimpleFile2StringConverter implements ObjectToStringConverter<File> {



    @Override
    public String convertToString(Field f, File object) throws IOException {
        return object.getCanonicalFile().getAbsoluteFile().getCanonicalPath().replace("\\", "/");
    }

    @Override
    public File convertFromString(Field f, Class<File> type,String str) throws Exception {
        return new File(str);
    }

    @Override
    public Class getType() {
        return File.class;
    }
}
