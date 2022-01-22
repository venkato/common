package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef

import java.util.logging.Logger

@CompileStatic
class SampleFieldRefs2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData')

    public static java.util.ArrayList alField1;
    public static ArrayList alField2;

    void getField(){
        Class clRef1 = java.util.ArrayList ;
        JrrClassUtils.getFieldValue(clRef1,'elementData1')
        JrrClassUtils.getFieldValue(fieldRef1,'elementData2')
        //JrrClassUtils.findField(al.getClass(),'elementData')
    }



}
