package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef

import java.util.logging.Logger

@CompileStatic
class SampleFieldRefs2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldRef fieldRef1=new FieldRef(new ClRef('java.util.ArrayList'),'elementData') //NOFIELDCHECK

    public static java.util.ArrayList alField1;
    public static ArrayList alField2;

    void getField(){
        Class clRef1 = java.util.ArrayList ;
        JrrClassUtils.getFieldValue(clRef1,'elementData1') //NOFIELDCHECK
        JrrClassUtils.getFieldValue(fieldRef1,'elementData2') //NOFIELDCHECK
        //JrrClassUtils.findField(al.getClass(),'elementData')
    }



}
