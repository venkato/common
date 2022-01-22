package net.sf.jremoterun.utilities.nonjdk.classpath.checkers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum GroovyMethodsEnum {
    invokeMethod('(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;'),
    getProperty('(Ljava/lang/String;)Ljava/lang/Object;'),
    setProperty('(Ljava/lang/String;Ljava/lang/Object;)V'),
    ;

    public String descriptor;

    GroovyMethodsEnum(String descriptor) {
        this.descriptor = descriptor
    }


    String getNameAndDescriptor(){
        return name()+descriptor
    }
}
