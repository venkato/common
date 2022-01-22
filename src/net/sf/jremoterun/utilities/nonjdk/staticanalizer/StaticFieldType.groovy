package net.sf.jremoterun.utilities.nonjdk.staticanalizer


enum StaticFieldType {
    File, ClRef,
    FieldRef,
    MethodRef,
    ConstructorRef,
    getFieldValue,
    setFieldValue,
    getFieldValueR,
    setFieldValueR,
    findField,
    findMethodByCount,
    invokeJavaMethod,
    invokeJavaMethodR,
    invokeJavaMethod2,
    invokeJavaMethod2R,
    invokeConstructor,
    findConstructorByCount,
    findMethodByParamTypes1,
    findMethodByParamTypes2,
    findMethodByParamTypes3,
    findMethodByParamTypes4,
    runMainMethod,
    ;


    boolean isFirstParamFake(){
        return name().endsWith('R')
    }

    public static Map<String,StaticFieldType> map1 = values().collectEntries{[(it.name()):it]};




}