package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class JschRequestRed extends  RedefinitionBase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static net.sf.jremoterun.utilities.classpath.ClRef clRef1= new ClRef('com.jcraft.jsch.Request');

    JschRequestRed() {
        super(clRef1)
    }

    boolean check(){
        try {
            Class clazz = clRef1.loadClass2()
            clazz.getClassLoader().loadClass(net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession.getName())
            return true
        }catch(ClassNotFoundException e){
            log.info3("failed load JrrSch",e)
            return false
        }
    }

    void redefineRequest() throws Exception {
        if(check()){
            redefineReuestImpl()
        }
    }

    void redefineReuestImpl() throws Exception {
//        Class clazz = com.jcraft.jsch.Request
        clazz.getClassLoader()
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clRef1, cc, "write", 1);

        method1.insertBefore """
{
        if(session instanceof net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession){
            net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession _aaa =(net.sf.jremoterun.utilities.nonjdk.sshsup.JrrJschSession)session;
            _aaa.onNewRequest(this);
        } 
    
}
"""
        doRedefine()
    }

}
