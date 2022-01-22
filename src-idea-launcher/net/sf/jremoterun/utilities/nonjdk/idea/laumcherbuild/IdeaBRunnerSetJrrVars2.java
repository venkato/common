package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy;
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2;

import java.util.logging.Logger;


@Deprecated
public class IdeaBRunnerSetJrrVars2 implements Runnable {

    private static final Logger log = Logger.getLogger(IdeaBRunnerSetJrrVars2.class.getName());
    public static AddFilesToUrlClassLoaderGroovy adder;


    public static AddFilesToUrlClassLoaderGroovy getAdder(){
        if(adder==null){
            throw new NullPointerException("adder is null");
        }
        return adder;
    }

    @Override
    public void run() {
        try {
            f1();
            log.fine("test1");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void f1() throws Exception {
        adder = new AddFilesToUrlClassLoaderGroovy(IdeaBuildRunnerSettings.groovyCl);
        if(IdeaBuildRunnerSettings.addUserSettingsDir) {
            adder.add(JrrStarterVariables2.getInstance().classesDir);
        }
    }



}
