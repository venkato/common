package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic

@CompileStatic
enum JrrDownloadFilesLayout{

    otherLibs(true),
    nonJars(true),
    srcGroovy(true),
    preloadedLibs,
    firstdownload_zip,

    origin,
    copy,
    firstdownload,
    jrrfiles,
    sftpLoader_jar,
    ;


    public String customName;
    public boolean createDirOnly;

    JrrDownloadFilesLayout(boolean b) {
        customName = name().replace('_','.')
        createDirOnly = b
    }

    JrrDownloadFilesLayout() {
        customName = name().replace('_','.')
        createDirOnly = false
    }

}
