package net.sf.jremoterun.utilities.nonjdk.classpath.java11;

import jdk.internal.loader.BuiltinClassLoader;
import jdk.internal.loader.URLClassPath;
import net.sf.jremoterun.utilities.NewValueListener;

import java.io.File;

public class AddClassPathEl implements NewValueListener<File> {

    public URLClassPath urlClassPath ;

    public AddClassPathEl(URLClassPath urlClassPath) {
        this.urlClassPath = urlClassPath;
    }
    public AddClassPathEl(BuiltinClassLoader urlClassPath) throws NoSuchFieldException, IllegalAccessException {
        this.urlClassPath = FetchJava11UrlClasspath.fetchUrlClassPath( urlClassPath);
        if(urlClassPath==null){
            throw new IllegalArgumentException("Url classpath is null");
        }
    }



    @Override
    public void newValue(File file) {
        urlClassPath.addFile(file.getAbsolutePath());
    }
}
