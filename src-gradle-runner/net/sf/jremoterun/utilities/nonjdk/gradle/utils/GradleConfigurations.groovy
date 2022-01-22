package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumCustomNameResolver
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum GradleConfigurations implements EnumNameProvider {
    annotationProcessor
    , apiElements
    , archives
    , compileClasspath
    , compileOnly
    , default_(false)
    , implementation
    , runtimeClasspath
    , runtimeElements
    , runtimeOnly
    , testAnnotationProcessor
    , testCompileClasspath
    , testCompileOnly
    , testImplementation
    , testRuntimeClasspath
    , testRuntimeOnly
    ;

    String customName;

    GradleConfigurations(boolean r) {
        customName = 'default';
    }

    GradleConfigurations() {
        customName = name()
    }
}
