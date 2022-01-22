package net.sf.jremoterun.utilities.nonjdk.securitymanager;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.security.Permission;
import java.util.logging.Logger;

@CompileStatic
public
class TrackSecurityManager extends SecurityManager{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    public void checkPermission(Permission perm) {
        System.out.println("aa "+perm);
//        super.checkPermission(perm)
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        System.out.println("aa2 "+perm);
//        super.checkPermission(perm, context)
    }
}
