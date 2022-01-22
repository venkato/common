package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;

@CompileStatic
interface ToFileRefIfDownload {

    File resolveToFileIfDownloaded();

}