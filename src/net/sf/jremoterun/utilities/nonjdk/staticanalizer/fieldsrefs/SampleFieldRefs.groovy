package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef

@CompileStatic
class SampleFieldRefs {


    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData')
    public static java.util.ArrayList alField1;
    public static ArrayList alField2;


    void f1(){
        ClRef clRef11
        FieldRef fieldRef2=new FieldRef(new ClRef('java.util.BitSet'),'words')
        FieldRef fieldRef3=new FieldRef(new ClRef('java.util.BitSet'),clRef11.getClassName())
    }

    void getField(){
        java.util.ArrayList al;
        JrrClassUtils.getFieldValue(al,'elementData')
        JrrClassUtils.findField(al.getClass(),'elementData')
    }

    void invokeMethodVar(){
        java.util.ArrayList al;
        JrrClassUtils.invokeJavaMethod(al,'add',null)
    }

    void invokeMethodField(){
        JrrClassUtils.invokeJavaMethod(alField1,'add',null)
    }

    void invokeMethodFieldClass1(){
        JrrClassUtils.invokeJavaMethod( java.util.ArrayList,'add',null)
    }

    void invokeMethodFieldClass2(){
        JrrClassUtils.invokeJavaMethod( ArrayList,'add',null)
    }

    void invokeMethodFieldClass3(){
        JrrClassUtils.invokeJavaMethod( alField2,'add',null)
    }

    void invokeMethodFieldClass4(){
        JrrClassUtils.findMethodByCount( alField2.getClass(),'add',3)
    }

    void invokeMethodFieldClass5(){
        JrrClassUtils.findMethodByParamTypes1( alField2.getClass(),'add',1,2)
        JrrClassUtils.findMethodByParamTypes2( alField2.getClass(),'add',[1,2])
        JrrClassUtils.findMethodByParamTypes3( alField2.getClass(),'add',int)
        Class[] param4=[Integer]
        JrrClassUtils.findMethodByParamTypes4( alField2.getClass(),'add',param4)
    }

    ClRef clRefF=new ClRef('java.util.ArrayList');
    Class alv=ArrayList

    void invokeMethodFieldForainClass(){
        JrrClassUtils.invokeJavaMethod(net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner.groovyScriptRunner,'initCl',null)
    }



    void invokeConstructor1(){
        JrrClassUtils.invokeConstructor(ArrayList,123123)
        JrrClassUtils.findConstructorByCount(ArrayList,5)
        //ClRef clRef1=new ClRef('java.util.ArrayList');
        JrrClassUtils.getFieldValue(clRefF,'elementData')

        ClRef clRef1=new ClRef('java.util.ArrayList');
        JrrClassUtils.getFieldValue(clRef1,'elementData')
    }







    void methodRef(){
        new MethodRef(new ClRef('java.util.BitSet'),'add',2);
        new ConstructorRef(new ClRef('java.util.BitSet'),3);
        new ConstructorRef(new ClRef(java.util.BitSet),4);



        JrrClassUtils.runMainMethod(ArrayList,'sad')
    }


}
