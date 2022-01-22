package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum KeyExchangeEnum1 {
    PROPOSAL_KEX_ALGS,
    PROPOSAL_SERVER_HOST_KEY_ALGS,
    PROPOSAL_ENC_ALGS_CTOS,
    PROPOSAL_ENC_ALGS_STOC,
    PROPOSAL_MAC_ALGS_CTOS,
    PROPOSAL_MAC_ALGS_STOC,
    PROPOSAL_COMP_ALGS_CTOS,
    PROPOSAL_COMP_ALGS_STOC,
    PROPOSAL_LANG_CTOS,
    PROPOSAL_LANG_STOC,
    PROPOSAL_MAX,

    JRR_Server_Public_Key,
    JRR_Server_Public_Key2,
    JRR_Client_Publickey_Short,
    jrr_proposalServer,
    jrr_proposalClient,
    jrr_versionClient,
    jrr_versionServer,
    jrr_connectionState,
    jrr_dumpResolvedProps,
    jrr_sig,
    jrr_notAvailableSig,
    ;


    public int idd;

    KeyExchangeEnum1() {
        this.idd =   ordinal()
        //mapp1.put(idd,this);
    }

    public static Map<Integer,KeyExchangeEnum1> mapp1 = values().toList().collectEntries {[(it.idd):it]}
}
