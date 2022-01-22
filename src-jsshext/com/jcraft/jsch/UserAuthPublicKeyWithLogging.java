/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002-2016 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

@Deprecated
public class UserAuthPublicKeyWithLogging extends UserAuthPublicKey {

    public Session session;

    public JrrSchSessionLog schSessionLog = new JrrSchSessionLog();

    protected boolean canContinue(Session session) {
        return session.auth_failures >= session.max_auth_tries;
    }

    protected int getPassphraseCount() {
        return 5;
    }


    protected void logError(String msg) {
        schSessionLog.logMsg(msg);
    }


    protected void onReply(int command) {
        if (command == SshCmds.SSH_MSG_USERAUTH_SUCCESS.cmdId) {
            schSessionLog.logMsg("got reply : ok");
        } else {
            schSessionLog.logMsg("got reply : " + command);
        }
    }

    protected void sendingCommand(SshCmds cmd) {
        schSessionLog.logMsg("sending command : " + cmd.name());
    }

    public void sendIdentity(byte[] _username, Identity identity, byte[] pubkeyblob) {
        sendIdentity2(_username, identity, pubkeyblob, identity.getAlgName());
    }

    public void sendIdentity2(byte[] _username, Identity identity, byte[] pubkeyblob, String algName) {
        buf.putByte((byte) SshCmds.SSH_MSG_USERAUTH_REQUEST.cmdId);
        buf.putString(_username);
        buf.putString(Util.str2byte("ssh-connection"));
        buf.putString(Util.str2byte("publickey"));
        buf.putByte((byte) 1);
        buf.putString(Util.str2byte(algName));
        buf.putString(pubkeyblob);
    }

    Packet getPacket() {
        return packet;
    }

    Buffer getBuffer() {
        return buf;
    }
}
