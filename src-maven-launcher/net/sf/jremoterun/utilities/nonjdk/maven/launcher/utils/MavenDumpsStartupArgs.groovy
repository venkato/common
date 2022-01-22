package net.sf.jremoterun.utilities.nonjdk.maven.launcher.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class MavenDumpsStartupArgs {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm')

    static void dumpDataD(){
        dumpData(net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper.outFileStream)
    }

    static void dumpData(OutputStream os){
        os.write "Date = ${sdf.format(new Date())} \n".getBytes()
        os.write "user dir = ${PropsEnum.user_dir.getValue()} \n".getBytes();
        os.write "args = ${JrrMavenCliWrapper.args1.join(' ')} \n".getBytes();
        os.write "maven home = ${JrrMavenCliWrapper.getMavenHome()} \n".getBytes();
        os.flush()
    }

}
