package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils

@CompileStatic
class SampleFieldJavasistRefs {


    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData') //NOFIELDCHECK
    public static java.util.ArrayList alField1;
    public static ArrayList alField2;


    ClRef clRefF=new ClRef('java.util.ArrayList');
    Class alv=ArrayList












    void invokeMethodFieldForainClass(){
        JrrJavassistUtils.findMethodByCount(clRefF,null,'add',1) //NOFIELDCHECK
        JrrJavassistUtils.findMethodByCount(alv,null,'add',2) //NOFIELDCHECK
        JrrJavassistUtils.findConstructorByCount(clRefF,null,3) //NOFIELDCHECK
    }



}
