package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import org.jetbrains.jps.cmdline.LauncherOriginal;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.logging.Logger;

public class IdeaBuilderAddGroovyRuntime1 {




    public static void doRun() throws Exception {
        try {
            if(IdeaBuildRunnerSettings.redirectOutToFileAux){
                doRedirect();
            }
            IdeaBuilderAddGroovyRuntime.doRun();
        }catch (Throwable e){
            e.printStackTrace();
            if(IdeaBuildRunnerSettings.startOriginal){
                if(IdeaBuildRunnerSettings.originalTried){
                    throw e;
                }
                LauncherImpl.runOriginal();
            }
        }
    }



    public static void doRedirect() throws Exception {
        //FileOutputStream fous = new FileOutputStream(jrrlibpath);
        PrintStream printStream1= new PrintStream(IdeaBuildRunnerSettings.buildLogBefore);
        IdeaBuildRunnerSettings.jrrOutStream = printStream1;
        System.setOut(printStream1);
        System.setErr(printStream1);
        System.out.println("starting "+new Date());
    }


}
