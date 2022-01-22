package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

enum ReflectionElement {

    setFieldValue, findField, getFieldValue, invokeJavaMethod, newFieldRef, newMethodRef, findMethodByParamTypes1, findMethodByParamTypes2, findMethodByCount,
    ;

    public static EnumSet<ReflectionElement> reFields = EnumSet.of(setFieldValue,
            getFieldValue, findField, newFieldRef);

    public static EnumSet<ReflectionElement> reMethods = EnumSet.of(invokeJavaMethod,
            findMethodByCount, findMethodByParamTypes1, findMethodByParamTypes2, newMethodRef);


}
