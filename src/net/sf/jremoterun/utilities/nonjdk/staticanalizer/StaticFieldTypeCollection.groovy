package net.sf.jremoterun.utilities.nonjdk.staticanalizer

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils

@CompileStatic
class StaticFieldTypeCollection {


    public static EnumSet<StaticFieldType> memberRefClasses = EnumSet.of(StaticFieldType.MethodRef, StaticFieldType.FieldRef, StaticFieldType.ConstructorRef)
    public static EnumSet<StaticFieldType> constructorsFunctions = EnumSet.of(StaticFieldType.invokeConstructor, StaticFieldType.invokeConstructorR, StaticFieldType.findConstructorByCount, StaticFieldType.findConstructorByCountR)
    public static EnumSet<StaticFieldType> reFields = EnumSet.of(StaticFieldType.setFieldValue, StaticFieldType.getFieldValue, StaticFieldType.findField, StaticFieldType.setFieldValueR, StaticFieldType.getFieldValueR, StaticFieldType.newFieldRef,)

    public static List<Class> classesConatains = [JrrClassUtils, JrrJavassistUtils, MethodRef, FieldRef, ConstructorRef,] as List<Class>;

    public static HashSet<String> javaClassNamesAll = new HashSet<>( classesConatains.collect{it.getName()})
    public static HashSet<String> javaClassRefNames = new HashSet<>( [MethodRef.getName(), FieldRef.getName(), ConstructorRef.getName()])


    static boolean isContainsName(String fileContent){
        String find1 = javaClassNamesAll.find { fileContent.contains(it) }
        return find1!=null
    }
}
