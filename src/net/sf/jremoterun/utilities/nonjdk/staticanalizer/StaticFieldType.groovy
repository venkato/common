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


    public static EnumSet<StaticFieldType> memberRefClasses = EnumSet.of(StaticFieldType.MethodRef, StaticFieldType.FieldRef, StaticFieldType.ConstructorRef)
    public static EnumSet<StaticFieldType> constructorsFunctions = EnumSet.of(StaticFieldType.invokeConstructor, StaticFieldType.invokeConstructorR, StaticFieldType.findConstructorByCount, StaticFieldType.findConstructorByCountR)
    public static EnumSet<StaticFieldType> reFields = EnumSet.of(StaticFieldType.setFieldValue, StaticFieldType.getFieldValue, StaticFieldType.findField, StaticFieldType.setFieldValueR, StaticFieldType.getFieldValueR, StaticFieldType.newFieldRef,)

}