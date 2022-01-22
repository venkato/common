package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.JSch
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

/**
 * @see com.jcraft.jsch.JSch#config
 */
@CompileStatic
enum JschSshConfigEnum implements EnumNameProvider{

    PreferredAuthentications,
    StrictHostKeyChecking,
    ssh_rsa,
    ssh_dss,
    kex,
    MaxAuthTries,
    ClearAllForwardings,
    ;

    String customName;

    JschSshConfigEnum() {
        customName = name().replace('_','-')
    }
}
