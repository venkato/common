package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javaonly.InitInfo;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import net.sf.jremoterun.utilities.nonjdk.log.AddDefaultIgnoreClasses;

import java.util.logging.Handler;

public class JdkLog2Log4jInit {

    public static InitInfo initInfo = new InitInfo(JdkLog2Log4jInit.class);
    public static boolean printMsgOnError = true;

    public static void jdk2log4j() {
        try{
            if(initInfo.isInited()){

            }else {
                jdk2log4jImpl();
                initInfo.setInited();
            }
        }catch (Throwable e){
            if(printMsgOnError) {
                System.err.println("Failed configure log4j2 : "+e);
                e.printStackTrace();
            }
            InitLogTracker.defaultTracker.addException("failed set appender for jdk logger",e);
            throw e;
        }
    }
    public static void jdk2log4jImpl() {

        AddDefaultIgnoreClasses.addIgnoreClasses();
//        org.apache.logging.log4j.jul.DefaultLevelConverter

        JdkIntoLog4j2Converter logHandler = new JdkIntoLog4j2Converter();
        java.util.logging.Logger logger = java.util.logging.Logger
                .getLogger("");
        logger.addHandler(logHandler);
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            logger.removeHandler(handler);
        }
        logger.addHandler(logHandler);
        JrrClassUtils.ignoreClassesForCurrentClass.add(JdkIntoLog4j2Converter.class.getName());

    }



}
