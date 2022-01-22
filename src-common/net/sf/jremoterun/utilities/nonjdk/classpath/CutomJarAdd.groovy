package net.sf.jremoterun.utilities.nonjdk.classpath;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.git.GitRef;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class CutomJarAdd {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? extends ToFileRef2> gitRefs =  [
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsta(),
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion(),
    ]



    static void addCustom(AddFilesToClassLoaderCommon adder) {
        adder.addAll gitRefs
//        adder.addAll JeditermBinRefs.all
    }

}
