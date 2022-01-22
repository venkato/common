package timmoson.server;

import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import timmoson.client.ClientSendRequest;
import timmoson.client.StayLocalObject;
import timmoson.common.debug.TcpSessionTrackerBean;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.common.transferedobjects.ReponseBean;
import timmoson.common.transferedobjects.SessionServiceId;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceCallServerInvoker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    // public static ServiceCallServerInvoker serviceCallServerInvoker = new
    // ServiceCallServerInvoker();

    public static boolean passByRef(Object object, Class declareClass) {
        if( object instanceof StayLocalObject) {
            return true;
        }
        if ((object instanceof Serializable) && !(object instanceof Component)) {
            return false;
        }
        if (!Serializable.class.isAssignableFrom(declareClass)
                || (object instanceof Component)) {
            return true;
        } else {
            return false;
        }

    }

    public static TcpServiceObject putObjectToSessionAsServerObject(
            TcpSession tcpSession, Object object) {
        TcpServiceObject tcpServiceObjects = new TcpServiceObject();
        SessionServiceId sessionServiceId = new SessionServiceId(object
                .getClass().getName()
                + "-"
                + tcpSession.reqSeviceCount.incrementAndGet());
        tcpServiceObjects.serviceInSessionId = sessionServiceId;
        tcpServiceObjects.service = object;
        tcpSession.serviceObjectsServer.put(sessionServiceId.id,
                tcpServiceObjects);
        return tcpServiceObjects;
    }

    public void makeCall(TcpCallInfoServer callInfo) {
        TcpSessionTrackerBean sessionTrackerBean = null;
        if (ClientSendRequest.sessionNotifier != null) {
            sessionTrackerBean = new TcpSessionTrackerBean();
            sessionTrackerBean.serverCallInfo = callInfo;
            sessionTrackerBean.session = callInfo.tcpSession;
            ClientSendRequest.sessionNotifier
                    .receiveRequest(sessionTrackerBean);
        }
        RemoteService.callsInfos.set(callInfo);
        ReponseBean reponseBean = new ReponseBean();
        reponseBean.requestId = callInfo.requestBean.requestId;
        try {
            Object result = callInfo.method.invoke(
                    callInfo.serviceSupport.service, callInfo.params);

            if (result == null) {
                if (callInfo.method.getReturnType() != void.class) {
                    log.info("result is null");
                }
                reponseBean.resultNull = true;
                writeResponseGeneral(reponseBean, null, callInfo);
            } else {
                boolean isRef = passByRef(result,
                        callInfo.method.getReturnType());
                if (isRef) {
                    TcpServiceObject tcpServiceObjects = ServiceCallServerInvoker
                            .putObjectToSessionAsServerObject(
                                    callInfo.tcpSession, result);
                    reponseBean.serviceInSessionId = tcpServiceObjects.serviceInSessionId;
                    writeResponseGeneralNotSerializable(reponseBean, result,
                            callInfo);
                } else {
                    writeResponseGeneral(reponseBean, result, callInfo);
                    reponseBean.response = JrrUtils
                            .serialize((Serializable) result);

                }
            }
            if (sessionTrackerBean != null) {
                sessionTrackerBean.result = result;
            }
        } catch (InvocationTargetException e) {
            if (sessionTrackerBean != null) {
                sessionTrackerBean.exception = e.getCause();
            }
            if (!callInfo.requestBean.waitResult) {
                log.log(Level.WARNING,null, e.getCause());
            }
            try {
                reponseBean.exception = JrrUtils.serialize(e.getCause());
            } catch (IOException e1) {
                log.log(Level.WARNING,null, e1.getCause());
            }
            log.log(Level.FINE,null, e);
            writeResponseException(reponseBean, e, callInfo);
        } catch (Exception e) {
            if (sessionTrackerBean != null) {
                sessionTrackerBean.exception = e;
            }
            if (!callInfo.requestBean.waitResult) {
                log.log(Level.WARNING,null, e.getCause());
            }
            try {
                reponseBean.exception = JrrUtils.serialize(e);
            } catch (IOException e1) {
                log.log(Level.WARNING,null, e1);
            }
            log.log(Level.INFO,null, e);
            writeResponseException(reponseBean, e, callInfo);
        } finally {
            if (sessionTrackerBean != null) {
                sessionTrackerBean.responseDate = new Date();
            }
            RemoteService.callsInfos.set(null);
        }
        if (callInfo.requestBean.waitResult) {
            if (callInfo.tcpSession.isClosed()) {
                log.info("session is closed");
            } else {
                try {
                    ServerUtilsStatic.writeResponse(reponseBean,
                            callInfo.tcpSession);
                } catch (IOException e) {
                    log.log(Level.WARNING,null, e);
                }
            }
        }
    }

    public void writeResponseException(ReponseBean reponseBean,
                                       Throwable throwable, TcpCallInfoServer callInfo) {

    }

    public void writeResponseGeneral(ReponseBean reponseBean, Object result,
                                     TcpCallInfoServer callInfo) {

    }

    public void writeResponseGeneralNotSerializable(ReponseBean reponseBean,
                                                    Object result, TcpCallInfoServer callInfo) {

    }

}
