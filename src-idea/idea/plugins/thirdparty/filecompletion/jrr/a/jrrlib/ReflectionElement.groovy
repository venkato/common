package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

/**
 * @see net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
 */
@Deprecated
enum ReflectionElement {

    setFieldValue,
    setFieldValueR,
    findField,
    getFieldValue,
    getFieldValueR,
    invokeJavaMethod,
    invokeJavaMethodR,
    newFieldRef,
    newMethodRef,
    findMethodByParamTypes1,
    findMethodByParamTypes2,
    findMethodByCount,
    invokeConstructorR,
    ;

    public static EnumSet<ReflectionElement> reFields = EnumSet.of(setFieldValue,setFieldValueR,
            getFieldValue, findField, newFieldRef, getFieldValueR,);

    public static EnumSet<ReflectionElement> reMethods = EnumSet.of(invokeJavaMethod,
            findMethodByCount, findMethodByParamTypes1, findMethodByParamTypes2, newMethodRef);

    boolean isFirstParamFake(){
        return name().endsWith('R')
    }

}
