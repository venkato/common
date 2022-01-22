package net.sf.jremoterun.utilities.nonjdk.docker

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.JavaProcessRunner;

import java.util.logging.Logger;

@CompileStatic
class JavaProcessRunnerDocker extends JavaProcessRunner {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DockerHelper dockerHelper;

    JavaProcessRunnerDocker(DockerHelper dockerHelper) {
        this.dockerHelper = dockerHelper
    }

    @Override
    String buildClassPathElement(File f) {
        String path1 = dockerHelper.javaLibsOthers + f.getName()
        if (MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder.mavenLocalDir2.isChildFile(f)) {
            path1 = dockerHelper.javaLibsIvy + MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder.mavenLocalDir2.getPathToParent(f)
        }

        dockerHelper.dockerLayerJrr.addFileOrFolder(new DockerFileJrr(f, path1))
        return path1
    }

    @Override
    void runCmd() {
        throw new IllegalStateException()
    }
}
