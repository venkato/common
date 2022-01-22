package net.sf.jremoterun.utilities.nonjdk.staticanalizer


enum StaticFieldType {
    File, ClRef,
    FieldRef,
    MethodRef,
    ConstructorRef,
    getFieldValue,
    setFieldValue,
    findField,
    findMethodByCount,
    invokeJavaMethod,
    invokeJavaMethod2,
    invokeConstructor,
    findConstructorByCount,
    findMethodByParamTypes1,
    findMethodByParamTypes2,
    findMethodByParamTypes3,
    findMethodByParamTypes4,
    runMainMethod,
    ;



    public static Map<String,StaticFieldType> map1 = values().collectEntries{[(it.name()):it]};


}