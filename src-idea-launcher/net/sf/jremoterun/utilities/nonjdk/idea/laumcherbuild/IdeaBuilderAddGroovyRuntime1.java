package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.io.PrintStream;
import java.util.Date;

public class IdeaBuilderAddGroovyRuntime1 {




    public static void doRun() throws Exception {
        try {
            if(IdeaBuildRunnerSettings.redirectOutToFileAux){
                doRedirect();
            }
            if(IdeaBuildRunnerSettings.jrrIdeaDumpEnvAux){
                IdeaBuilderEnvDump.dumpEnv();
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
        PrintStream printStream1= new PrintStream(IdeaBuildRunnerSettings.buildLogBefore);
        IdeaBuildRunnerSettings.jrrOutStream = printStream1;
        System.setOut(printStream1);
        System.setErr(printStream1);
        System.out.println("starting "+new Date());
    }


}
