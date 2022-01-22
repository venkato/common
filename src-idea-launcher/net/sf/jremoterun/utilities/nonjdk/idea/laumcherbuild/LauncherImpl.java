package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import org.jetbrains.jps.cmdline.LauncherOriginal;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

public class LauncherImpl {

    public static void main(String[] args) throws Exception {
        IdeaBuildRunnerSettings.argsPv2 = new ArrayList<>( Arrays.asList(args));
        try {
            IdeaBuilderAddGroovyRuntime1.doRun();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }


    public static void runOriginal() throws Exception {
        String[] aa = IdeaBuildRunnerSettings.argsPv2.toArray(new String[0]);
        LauncherOriginal.main(aa);
    }


}
