package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite;

import java.lang.reflect.Field;

public interface ObjectToStringConverter<T> {

    String convertToString(Field f, T object) throws Exception;
    T convertFromString(Field f, Class<T> type,String str) throws Exception;

    Class<T> getType();



}
