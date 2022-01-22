package net.sf.jremoterun.utilities.nonjdk.docker

import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import org.apache.commons.io.FilenameUtils;

import java.util.logging.Logger;

@CompileStatic
class DockerFileJrr {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File file;
    public AbsoluteUnixPath unixPath;

    DockerFileJrr(ToFileRef2 fileRef2, String unixPath) {
        this(fileRef2.resolveToFile(),unixPath)
    }
    DockerFileJrr(File file, String unixPath) {
        this.file = file
        this.unixPath = AbsoluteUnixPath.get(unixPath)
    }

    DockerFileJrr(File file, AbsoluteUnixPath unixPath) {
        this.file = file
        this.unixPath = unixPath
    }


    static String getSimpleNameS(AbsoluteUnixPath path){
        return FilenameUtils.getName(path.toString())

    }

}
