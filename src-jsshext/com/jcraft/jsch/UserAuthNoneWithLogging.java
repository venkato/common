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
public class UserAuthNoneWithLogging extends UserAuthNone {
 // public  static final int SSH_MSG_SERVICE_ACCEPT=                  6;
  private String methods=null;
  public Session session;
  public JrrSchSessionLog schSessionLog = new JrrSchSessionLog();

    protected boolean canContinue(Session session) {
        return session.auth_failures >= session.max_auth_tries;
    }

  protected void logError(String msg) {
    schSessionLog.logMsg(msg);
  }


    protected void onReply(int command) {
        if(command == SshCmds.SSH_MSG_USERAUTH_SUCCESS.cmdId){
            schSessionLog.logMsg("got reply : ok");
        }else {
            schSessionLog.logMsg("got reply : " + command);
        }
    }


  protected void sendingCommand(SshCmds cmd){
    schSessionLog.logMsg("sending command : "+cmd.name());
  }


/*
    public boolean start(Session session) throws Exception{
      this.session = session;
      this.userinfo=session.getUserInfo();
      this.packet=session.packet;
      this.buf=packet.getBuffer();
      this.username=session.getUserName();



    // send
    // byte      SSH_MSG_SERVICE_REQUEST(5)
    // string    service name "ssh-userauth"
    packet.reset();
    buf.putByte((byte)SshCmds.SSH_MSG_SERVICE_REQUEST.cmdId);
    buf.putString(Util.str2byte("ssh-userauth"));
    sendingCommand(SshCmds.SSH_MSG_SERVICE_REQUEST);
    session.write(packet);

    if(JSch.getLogger().isEnabled(Logger.INFO)){
      JSch.getLogger().log(Logger.INFO, 
                           "SSH_MSG_SERVICE_REQUEST sent");
    }

    // receive
    // byte      SSH_MSG_SERVICE_ACCEPT(6)
    // string    service name
    buf=session.read(buf);
    int command=buf.getCommand();
    onReply(command);
    boolean result=(command==SshCmds.SSH_MSG_SERVICE_ACCEPT.cmdId);

    if(JSch.getLogger().isEnabled(Logger.INFO)){
      JSch.getLogger().log(Logger.INFO, 
                           "SSH_MSG_SERVICE_ACCEPT received");
    }
    if(!result)
      return false;

    byte[] _username=null;
    _username=Util.str2byte(username);

    // send
    // byte      SSH_MSG_USERAUTH_REQUEST(50)
    // string    user name
    // string    service name ("ssh-connection")
    // string    "none"
    packet.reset();
    buf.putByte((byte)SshCmds.SSH_MSG_USERAUTH_REQUEST.cmdId);
    buf.putString(_username);
    buf.putString(Util.str2byte("ssh-connection"));
    buf.putString(Util.str2byte("none"));
    sendingCommand(SshCmds.SSH_MSG_USERAUTH_REQUEST);
    session.write(packet);

    loop:
    while(true){
      buf=session.read(buf);
      command=buf.getCommand()&0xff;
       onReply(command);
      if(command==SshCmds.SSH_MSG_USERAUTH_SUCCESS.cmdId){
	return true;
      }
      if(command==SshCmds.SSH_MSG_USERAUTH_BANNER.cmdId){
	buf.getInt(); buf.getByte(); buf.getByte();
	byte[] _message=buf.getString();
	byte[] lang=buf.getString();
	String message=Util.byte2str(_message);
	if(userinfo!=null){
          try{
            userinfo.showMessage(message);
          }
          catch(RuntimeException ee){
          }
	}
	continue loop;
      }
      if(command==SshCmds.SSH_MSG_USERAUTH_FAILURE.cmdId){
	buf.getInt(); buf.getByte(); buf.getByte(); 
	byte[] foo=buf.getString();
	int partial_success=buf.getByte();
	methods=Util.byte2str(foo);
//System.err.println("UserAuthNONE: "+methods+
//		   " partial_success:"+(partial_success!=0));
//	if(partial_success!=0){
//	  throw new JSchPartialAuthException(new String(foo));
//	}

        break;
      }
      else{
//      System.err.println("USERAUTH fail ("+command+")");
	throw new JSchException("USERAUTH fail ("+command+")");
      }
    }
   //throw new JSchException("USERAUTH fail");
    return false;
  }
*/
  @Override
  public String getMethods(){
    return methods;
  }
}
