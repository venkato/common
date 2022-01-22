package net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.customconverts;


import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.ObjectToStringConverter;
import net.sf.jremoterun.utilities.nonjdk.propssimplereadwrite.SimpleConverterStorage;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SimpleList2StringConverter  {

    public SimpleConverterStorage simpleConverterStorage;
    public String separator = ",";

    public SimpleList2StringConverter(SimpleConverterStorage simpleConverterStorage) {
        this.simpleConverterStorage = simpleConverterStorage;
    }

//    @Override
    public String convertToString(Field f, Collection object) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object obj : object) {
            if (isFirst) {
                isFirst = false;
            }else{
                sb.append(separator);
            }
            sb.append(simpleConverterStorage.initialConverter.convertToString(f, obj));
        }
        return sb.toString();
    }

//    @Override
//    public Collection convertFromString(Class clazz, String str) throws Exception {
//        return convert2(f, str, f.getGenericType(), separator);
//    }

//    @Override
//    public Class getType() {
//        return List.class;
//    }


    public Collection convert2(Field f, String str, Type genericArg, String separator1) throws Exception {
        Class paramClass = getParamClass(genericArg);
        if (paramClass == null) {
            throw new IllegalArgumentException("failed find converter from collection " + genericArg + " for " + str);
        }

        StringTokenizer tokenize = new StringTokenizer(str, separator1);
        Class type1 = f.getType();
        Collection result;
        if (Collection.class.isAssignableFrom(type1)) {
            if(type1==List.class){
                result = new ArrayList();
            }else if(type1== Set.class){
                result = new HashSet();
            }else if(type1== Collection.class) {
                result = new ArrayList();
            }else{
                result = (Collection) type1.newInstance();
            }
        } else {
            result = new ArrayList();
        }
        while (tokenize.hasMoreTokens()) {
            result.add(simpleConverterStorage.initialConverter.convertFromString(f, paramClass,  tokenize.nextToken()));
        }
        return result;
    }

    public Class getParamClass(Type genericArg) {
        return getParamClassS(genericArg);
    }

    public static Class getParamClassS(Type genericArg) {
        if (!(genericArg instanceof ParameterizedType)) {
            throw new IllegalArgumentException("not ParameterizedType : " + genericArg);
//            return null
        }
        ParameterizedType pt = (ParameterizedType) genericArg;
        Type[] typeArguments = pt.getActualTypeArguments();
        if (typeArguments.length != 1) {
            throw new IllegalArgumentException("type should be 1 : " + typeArguments);
        }
        Type type = typeArguments[0];
        if (!(type instanceof Class)) {
            throw new IllegalArgumentException("not a class : " + type);
        }
        Class clazz = (Class) type;
        if (clazz == Object.class) {
            clazz = String.class;
        }
        return clazz;
    }
}
