package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum SshjDumpParams {
    negotiated,
    proposalServerProps,
    versionClient,
    versionServer,
    proposalClient,
    proposalServer,
    serverPublicKey,
//    clientAvailable,
//    serverAvailable,
    ;


}
