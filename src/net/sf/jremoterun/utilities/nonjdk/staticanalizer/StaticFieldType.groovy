package net.sf.jremoterun.utilities.nonjdk.staticanalizer


/**
 * @see idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.ReflectionElement
 */
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
    findMethodByCountR,
    invokeJavaMethod,
    invokeJavaMethodR,
    invokeJavaMethod2,
    invokeJavaMethod2R,
    invokeConstructor,
    invokeConstructorR,
    findConstructorByCount,
    findConstructorByCountR,
    findMethodByParamTypes1,
    findMethodByParamTypes2,
    findMethodByParamTypes3,
    findMethodByParamTypes4,
    runMainMethod,


    // use in eclipse
    invokeMethod,
    findMethod,
   // synthetic elements :
    newFieldRef,
    newMethodRef,
    ;


    boolean isFirstParamFake() {
        return name().endsWith('R')
    }

    public static Map<String, StaticFieldType> map1 = values().collectEntries { [(it.name()): it] };


}