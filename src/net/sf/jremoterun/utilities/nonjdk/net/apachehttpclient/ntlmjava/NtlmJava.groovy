package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ntlmjava


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.params.HttpMethodParams
import sun.net.www.protocol.http.ntlm.NTLMAuthSequence

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

@CompileStatic
public class NtlmJava {

    private static final Logger LOG = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef ref8plus = new ClRef("sun.net.www.protocol.http.ntlm.NTLMAuthSequence")
    public static ClRef ref6plus = new ClRef("sun.net.www.protocol.http.NTLMAuthSequence")

    public Object myAuthSequenceObject;

    public Method myGetAuthHeaderMethod;

    public NtlmJava() {

    }


    void init1() {
        Class clazz = ref8plus.loadClass2();

        Constructor constructor = JrrClassUtils.findConstructorByCount(ref8plus,3);
//        Constructor constructor = clazz.getDeclaredConstructor(String.class, String.class, String.class);
        constructor.setAccessible(true);
        Object sequence = constructor.newInstance(new Object[]{null, null, null});
        Method getAuthHeaderM = JrrClassUtils.findMethodByCount(ref8plus, "getAuthHeader", 1);
//        Method getAuthHeaderM = clazz.getMethod("getAuthHeader", String.class);

        myAuthSequenceObject = sequence
        myGetAuthHeaderMethod = getAuthHeaderM

    }

    void initIfNeeded(){
        if(myGetAuthHeaderMethod==null){
            init1()
        }
    }


    String getType1MessageResponse() {
        initIfNeeded()
        return (String) myGetAuthHeaderMethod.invoke(myAuthSequenceObject, new Object[]{null});

    }


    String getType3MessageResponse(String type2message)  {
        initIfNeeded()
        return (String) myGetAuthHeaderMethod.invoke(myAuthSequenceObject, new Object[]{type2message});

    }

    long crdHandle(){
        long crdHandle1 = JrrClassUtils.getFieldValueR(ref8plus, myAuthSequenceObject,'crdHandle') as long
        return crdHandle1
    }

    Boolean statusObjectValue(){
        Object st = statusObject()
        if(st==null){
            return null
        }
        boolean value1 = JrrClassUtils.getFieldValueR(new ClRef('sun.net.www.protocol.http.ntlm.NTLMAuthSequence$Status'), st, 'sequenceComplete')
        return value1
    }

    Object statusObject(){
        Object  status1 = JrrClassUtils.getFieldValueR(ref8plus, myAuthSequenceObject,'status')
        return status1
    }

    int state(){
        Object  status1 = JrrClassUtils.getFieldValueR(ref8plus, myAuthSequenceObject,'state')
        return status1 as int
    }


    byte[] getNextToken ( byte[] lastToken){
        long crdHandle = crdHandle()
        Object  status1 = statusObject()
        byte[] r = JrrClassUtils.invokeJavaMethodR(ref8plus, myAuthSequenceObject,'getNextToken',crdHandle,lastToken,status1) as byte[];
        return r
    }



}