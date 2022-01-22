package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic

@CompileStatic
enum SharedFolderSettingsEnum {
    ivyCacheDir("ivy/cache/"),

    gitDir("git/"),
    jrrDownloadDir("jrrdownloaddir/"),
    jrrStarter('git/https/github.com/venkato/starter3/git/'),

    InvocationFramework("git/https/github.com/venkato/common/git/"),
    invocationFrameworkJar("git/https/github.com/venkato/common/iffamework.jar"),
    ;


    public String childPath;

    SharedFolderSettingsEnum(String childPath) {
        this.childPath = childPath
    }
}
