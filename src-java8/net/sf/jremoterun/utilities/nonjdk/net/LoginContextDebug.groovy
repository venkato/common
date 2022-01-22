package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.security.auth.login.LoginContext;
import java.util.logging.Logger;

@CompileStatic
class LoginContextDebug extends sun.security.util.Debug{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile loginContextDebug;

    static void init(){
        if(loginContextDebug==null){
            initImpl()
        }
    }

    static void initImpl(){
        System.setProperty('logincontext','all');
        System.setProperty('java.security.debug','true');
        System.setProperty('java.security.auth.debug','true');
        System.setProperty('sun.security.krb5.debug','true');
        System.setProperty('sun.security.spnego.debug','true');
        System.setProperty('sun.security.jgss.debug','true');
        JrrClassUtils.ignoreClassesForCurrentClass.add JrrClassUtils.getCurrentClass().getName()
        loginContextDebug = new LoginContextDebug();
        JrrClassUtils.setFieldValue(LoginContext,'debug',loginContextDebug);
    }


    @Override
    void println(String message) {
        log.info message
    }


}
