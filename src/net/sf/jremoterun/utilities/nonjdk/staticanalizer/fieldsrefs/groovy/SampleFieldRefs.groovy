package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef

@CompileStatic
class SampleFieldRefs {


    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData') //NOFIELDCHECK
    public static java.util.ArrayList alField1;
    public static ArrayList alField2;


    void f1(){
        ClRef clRef11
        FieldRef fieldRef2=new FieldRef(new ClRef('java.util.BitSet'),'words') //NOFIELDCHECK
        FieldRef fieldRef3=new FieldRef(new ClRef('java.util.BitSet'),clRef11.className) //NOFIELDCHECK
    }

    void getField(){
        java.util.ArrayList al;
        JrrClassUtils.getFieldValue(al,'elementData') //NOFIELDCHECK
        JrrClassUtils.findField(al.getClass(),'elementData') //NOFIELDCHECK
    }

    void invokeMethodVar(){
        java.util.ArrayList al;
        JrrClassUtils.invokeJavaMethod(al,'add',null) //NOFIELDCHECK
    }

    void invokeMethodField(){
        JrrClassUtils.invokeJavaMethod(alField1,'add',null) //NOFIELDCHECK
    }

    void invokeMethodFieldClass1(){
        JrrClassUtils.invokeJavaMethod( java.util.ArrayList,'add',null) //NOFIELDCHECK
    }

    void invokeMethodFieldClass2(){
        JrrClassUtils.invokeJavaMethod( ArrayList,'add',null) //NOFIELDCHECK
    }

    void invokeMethodFieldClass3(){
        JrrClassUtils.invokeJavaMethod( alField2,'add',null) //NOFIELDCHECK
    }

    void invokeMethodFieldClass4(){
        JrrClassUtils.findMethodByCount( alField2.getClass(),'add',3) //NOFIELDCHECK
    }

    void invokeMethodFieldClass5(){
        JrrClassUtils.findMethodByParamTypes1( alField2.getClass(),'add',1,2) //NOFIELDCHECK
        JrrClassUtils.findMethodByParamTypes2( alField2.getClass(),'add',[1,2]) //NOFIELDCHECK
        JrrClassUtils.findMethodByParamTypes3( alField2.getClass(),'add',int) //NOFIELDCHECK
        Class[] param4=[Integer]
        JrrClassUtils.findMethodByParamTypes4( alField2.getClass(),'add',param4) //NOFIELDCHECK
    }

    ClRef clRefF=new ClRef('java.util.ArrayList');
    Class alv=ArrayList

    void invokeMethodFieldForainClass(){
        //JrrClassUtils.invokeJavaMethod(net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner.groovyScriptRunner,'initCl',null)
    }



    void invokeConstructor1(){
        JrrClassUtils.invokeConstructor(ArrayList,123123) //NOFIELDCHECK
        JrrClassUtils.findConstructorByCount(ArrayList,5) //NOFIELDCHECK
        //ClRef clRef1=new ClRef('java.util.ArrayList');
        JrrClassUtils.getFieldValue(clRefF,'elementData') //NOFIELDCHECK

        ClRef clRef1=new ClRef('java.util.ArrayList');
        JrrClassUtils.getFieldValue(clRef1,'elementData') //NOFIELDCHECK
    }







    void methodRef(){
        new MethodRef(new ClRef('java.util.BitSet'),'add',2); //NOFIELDCHECK
        new ConstructorRef(new ClRef('java.util.BitSet'),3); //NOFIELDCHECK
        new ConstructorRef(new ClRef(java.util.BitSet),4); //NOFIELDCHECK



        JrrClassUtils.runMainMethod(ArrayList,'sad') //NOFIELDCHECK
    }


}
