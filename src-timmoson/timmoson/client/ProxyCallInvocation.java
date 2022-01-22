package timmoson.client;

import junit.framework.Assert;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javassist.BaseMethodHandler;
import net.sf.jremoterun.utilities.javassist.InvokcationAccessor;
import net.sf.jremoterun.utilities.javassist.JavassistProxyFactory;

import timmoson.common.transferedobjects.RemoteObjectServer;
import timmoson.common.transferedobjects.ServiceId;
import timmoson.server.TcpServiceObject;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ProxyCallInvocation extends BaseMethodHandler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    final Class clazz;
    public TimmosonSessionStore tcpSession1;
    // String serviceId;
    // boolean sessionObject;

    public TcpServiceObject serviceObject22 = new TcpServiceObject();

    public ProxyCallInvocation(Class clazz, TimmosonSessionStore tcpSession1, ServiceId serviceId) {
        this.clazz = clazz;
        this.tcpSession1 = tcpSession1;
        // this.serviceId = serviceId;
        // this.sessionObject = sessionObject;
        serviceObject22.serviceInSessionId = serviceId;
        // serviceObject22.serviceCallServerInvoker=serviceCallServerInvoker;
        // Assert.assertNotNull(tcpSession1);
        Assert.assertNotNull(serviceId);
        Assert.assertNotNull(clazz);
        handleHashCode = true;
    }

    boolean equalsCalled(Object beanObject, Method methodToInvoke, Method superMethod, Object[] args) {
        if (beanObject == args[0]) {
            return true;
        }
        return false;
    }

    String toStringCalled(Object beanObject, Method methodToInvoke, Method superMethod, Object[] args) {
        return clazz.getName();
    }

    RemoteObjectServer handleProxyParam(ProxyCall param) {
        log.info("param is proxy " + param);
        RemoteObjectServer remoteObject = new RemoteObjectServer();
        // remoteObject.inSession=param.getProxyCallInvocationObject().sessionObject;
        InvokcationAccessor serviceObject2 = (InvokcationAccessor) param;
        JavassistProxyFactory javassistProxyFactory = serviceObject2._getJavassistProxyFactory();
        ProxyCallInvocation methodHandler = (ProxyCallInvocation) javassistProxyFactory.getMethodHandler();

        remoteObject.objectId = methodHandler.serviceObject22.serviceInSessionId;
        //param.getProxyCallInvocationObject().serviceObject22.serviceInSessionId;
        return remoteObject;
    }

    @Override
    public Object invoke2(Object beanObject, Method methodToInvoke, Method superMethod, Object[] args)
            throws Throwable {
        String methodName = methodToInvoke.getName();
        if ("equals".equals(methodName) && args.length == 1) {
            return equalsCalled(beanObject, methodToInvoke, superMethod, args);
        } else if (args.length == 0) {
            if ("toString".equals(methodName)) {
                return toStringCalled(beanObject, methodToInvoke, superMethod, args);
            }
        }
        RequestInfoCleint infoCleint = new RequestInfoCleint();
        infoCleint.method = methodToInvoke;
        infoCleint.params = args;
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            if (object instanceof ProxyCall) {
                ProxyCall param = (ProxyCall) object;
                args[i] = handleProxyParam(param);
            }
        }
        infoCleint.requestBean.methodName = methodName;
        // infoCleint.requestBean.serviceInSession = sessionObject;
        infoCleint.requestBean.serviceId = serviceObject22.serviceInSessionId;
        ClientParams clientParams = ClientSendRequest.getClientParams();
        if (clientParams.overrideTcpSession == null) {
            if (tcpSession1 == null) {
                throw new Exception("can't detect session");
            }
            infoCleint.tcpSession = tcpSession1.getTcpSession();
            if (infoCleint.tcpSession == null) {
                throw new Exception("can't detect session");
            }
        } else {
            infoCleint.tcpSession = clientParams.overrideTcpSession;
        }
        if (clientParams.waitResult == null) {
            clientParams.waitResult = (methodToInvoke.getReturnType() != void.class);
        }
        // if(SampleCient.getClientParams().waitResult) {
        log.info("tid="+Thread.currentThread().getId()+" calling "+methodName);
        Object result = ClientSendRequest.makeCall(infoCleint);
        ClientSendRequest.clientParams.set(null);
        return result;
        // }
        // return null;
    }

}
