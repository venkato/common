package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.ObjectHolder
import org.apache.maven.wagon.providers.http.httpclient.auth.UsernamePasswordCredentials
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.BasicUserPrincipal

import java.security.Principal;
import java.util.logging.Logger;

@CompileStatic
class UsernamePasswordCredentialsJrr implements Credentials {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    BasicUserPrincipal userPrincipal
    //public String userName;
    public ObjectHolder<String> password1;

    UsernamePasswordCredentialsJrr(String userName, ObjectHolder<String> password1) {
      //  this.userName = userName
        this.password1 = password1
        userPrincipal = new BasicUserPrincipal(userName)
    }

    @Override
    String getPassword() {
        return password1.getObject()
    }

//    boolean equals(o) {
//        if (this.is(o)) return true
//        if (!(o instanceof UsernamePasswordCredentialsJrr)) return false
//
//        UsernamePasswordCredentialsJrr that = (UsernamePasswordCredentialsJrr) o
//
//        if (password1 != that.password1) return false
//        if (userName != that.userName) return false
//        if (userPrincipal != that.userPrincipal) return false
//
//        return true
//    }
//
//    int hashCode() {
//        int result
//        result = (userPrincipal != null ? userPrincipal.hashCode() : 0)
//        result = 31 * result + (userName != null ? userName.hashCode() : 0)
//        result = 31 * result + (password1 != null ? password1.hashCode() : 0)
//        return result
//    }
}
