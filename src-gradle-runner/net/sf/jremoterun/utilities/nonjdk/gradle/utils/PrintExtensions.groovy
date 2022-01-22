package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtensionsSchema;

import java.util.logging.Logger;

@CompileStatic
class PrintExtensions {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void printExtensions(Project  project){
        printExtensions(project.getExtensions().getExtensionsSchema())
    }

    void printExtensions(ExtensionsSchema schemas){
        List<ExtensionsSchema.ExtensionSchema> schemas1 = schemas.toList().findAll { shouldPrintExetention(it) }
        log.warning("found extensions ${schemas1.size()}")
        schemas1.each {
            String s = convertToString(it)
            log.warning(s)
        }
    }

    boolean shouldPrintExetention(ExtensionsSchema.ExtensionSchema schema){
        return true
    }

    String convertToString(ExtensionsSchema.ExtensionSchema schema){
        return "${schema.getName()} ${schema.getPublicType()}"
    }

}
