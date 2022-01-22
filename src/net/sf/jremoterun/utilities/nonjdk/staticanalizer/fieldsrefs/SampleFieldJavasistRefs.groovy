package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils

@CompileStatic
class SampleFieldJavasistRefs {


    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData')
    public static java.util.ArrayList alField1;
    public static ArrayList alField2;


    ClRef clRefF=new ClRef('java.util.ArrayList');
    Class alv=ArrayList









    void invokeMethodFieldForainClass(){
        JrrJavassistUtils.findMethodByCount(clRefF,null,'add',1)
        JrrJavassistUtils.findMethodByCount(alv,null,'add',2)
        JrrJavassistUtils.findConstructorByCount(clRefF,null,3)
    }



}
