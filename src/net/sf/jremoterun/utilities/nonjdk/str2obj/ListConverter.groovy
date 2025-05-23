package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverterI

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.logging.Logger

@CompileStatic
class ListConverter implements StringToObjectConverterI {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String separatorS = ','
    public String separator = ','

    @Override
    Object convert(String str, Type genericArg) {
        convert2(str, genericArg,separator)
    }

    static List convert2(String str, Type genericArg,String separator1) {
        Class clazz = getParamClass(genericArg)
        if (clazz == null) {
            throw new IllegalArgumentException("failed find converter from collection ${genericArg} for ${str}")
        }
        List<String> tokenize = str.tokenize(separator1)
        return tokenize.collect {
            Object el = StringToObjectConverter.defaultConverter.convertFromStringToType(it, clazz, null)
            return el
        }
    }

    static Class getParamClass(Type genericArg) {
        if (!(genericArg instanceof ParameterizedType)) {
            throw new IllegalArgumentException("not ParameterizedType : ${genericArg}")
//            return null
        }
        ParameterizedType pt = (ParameterizedType)genericArg;
//        Type[] typeArguments = JrrClassUtils.invokeJavaMethod(genericArg,'getActualTypeArguments') as Type[]
        Type[] typeArguments = pt.getActualTypeArguments()
        if (typeArguments.length != 1) {
            throw new IllegalArgumentException("type should be 1 : ${typeArguments}")
        }
        Type type = typeArguments[0]
        if (!(type instanceof Class)) {
            throw new IllegalArgumentException("not a class : ${type}")
        }
        Class clazz = (Class) type;
        if (clazz == Object) {
            clazz = String
        }
        return clazz
    }

}
