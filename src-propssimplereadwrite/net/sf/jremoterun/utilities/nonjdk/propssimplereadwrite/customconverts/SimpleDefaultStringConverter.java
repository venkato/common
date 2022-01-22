package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts;

import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.ObjectToStringConverter;
import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.SimpleConverterStorage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SimpleDefaultStringConverter {


    public SimpleConverterStorage simpleConverterStorage;

    public String nullValue = "null";

    public static final List<Class> primitiveClasses = Arrays.asList(new Class[]{int.class, long.class, boolean.class, char.class, short.class, byte.class, float.class, double.class, void.class});

    public static final List<Class> primitiveWrapperClasses = Arrays.asList(new Class[]{Integer.class, Long.class, Boolean.class, Character.class, Short.class, Byte.class, Float.class, Double.class, Void.class});

    public SimpleDefaultStringConverter(SimpleConverterStorage simpleConverterStorage) {
        this.simpleConverterStorage = simpleConverterStorage;
    }

//    @Override
    public String convertToString(Field f, Object object) throws Exception {
        if(object==null){
            return nullValue;
        }
        Class type1 = object.getClass();
        ObjectToStringConverter converter = simpleConverterStorage.customConverts.get(type1.getName());
        if (converter != null) {
            return converter.convertToString(f, object);
        }
        if(Collection.class.isAssignableFrom(type1)){
            return simpleConverterStorage.collectionConverter.convertToString(f, (Collection) object);
        }
        if(Enum.class.isAssignableFrom(type1)){
            return simpleConverterStorage.enumConverter.convertToString(f, (Enum) object);
        }
        return object.toString();
    }

//    @Override
    public Object convertFromString(Field field, Class type1,String valueS) throws Exception {
        if (nullValue.equals(valueS)) {
            return null;
        }
//        Class type1 = field.getType();
        ObjectToStringConverter converter = simpleConverterStorage.customConverts.get(type1);
        if (converter != null) {
            return converter.convertFromString(field, type1,valueS);
        }
        if(Collection.class.isAssignableFrom(type1)){
            return simpleConverterStorage.collectionConverter.convert2(field,valueS, field.getGenericType(), simpleConverterStorage.collectionConverter.separator);
        }
        if(Enum.class.isAssignableFrom(type1)){
            return simpleConverterStorage.enumConverter.convertFromString(field, type1,valueS);
        }
        if (type1.isPrimitive()) {
            int i = primitiveClasses.indexOf(type1);
            type1 = primitiveWrapperClasses.get(i);
        }
        return convertToObject2(type1, valueS);
    }


    public Object convertToObject2(Class type1, String valueS) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        valueS = valueS.trim();
        return type1.getConstructor(String.class).newInstance(valueS);
    }

//    @Override
//    public Class getType() {
//        throw new UnsupportedOperationException();
//    }
}
