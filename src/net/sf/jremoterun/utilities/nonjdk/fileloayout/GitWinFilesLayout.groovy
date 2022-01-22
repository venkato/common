package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum GitWinFilesLayout  implements ChildRedirect, EnumNameProvider{


    bashExe('bin/bash.exe'),
    shExe('bin/sh.exe'),
    gitExe('bin/git.exe'),
    gitCmdExe('cmd/git.exe'),
    gitkExe('cmd/git.exe'),
    gitGuiExe('cmd/git-gui.exe'),
    gitLfsExe('cmd/git-lfs.exe'),
    lessExe('usr/bin/less.exe'),
    findExe('usr/bin/find.exe'),
    sedExec('usr/bin/sed.exe'),
    awkExec('usr/bin/awk.exe'),

    cmdDir('cmd/'),
    binDir('bin/'),
    mingwBinDir('mingw64/bin/'),
    usrBinDir('usr/bin/'),
    ;


    @Deprecated
    public String subPath;
    String customName;
    ExactChildPattern ref;

    GitWinFilesLayout(String subPath) {
        this.subPath = subPath
        customName = subPath
        ref = new ExactChildPattern (subPath)
    }

    boolean isDir() {
        return subPath.endsWith('/');
    }

    boolean isExecFile(){
        return name().endsWith('exe')
    }

    @Deprecated
    File buildFile(File baseDir) {
        return new File(baseDir, subPath);
    }

    @Override
    String toString() {
        return subPath
    }
}
