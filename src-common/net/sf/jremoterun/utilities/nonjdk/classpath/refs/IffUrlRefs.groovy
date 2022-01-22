package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.classpath.CustomObjectHandler;
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UrlRef;
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.UrlProvided;

import java.io.File;
import java.net.URL;

public enum IffUrlRefs implements UrlProvided {
    less("https://som.aa"),
    ;

    URL url;

    IffUrlRefs(String url) {
        this.url = new URL(url)
    }

    @Override
    URL convertToUrl() {
        return url;
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)
    }



}
