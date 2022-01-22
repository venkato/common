package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum Gradle7PluginsRefs implements  EnumNameProvider {

    antlr,
    build_cache_http,
    build_init,
    build_profile,
    code_quality,
    composite_builds,
    configuration_cache,
    dependency_management,
    diagnostics,
    ear,
    enterprise,
    ide,
    ide_native,
    ivy,
    jacoco,
    java_compiler_plugin,
    kotlin_dsl_provider_plugins,
    kotlin_dsl_tooling_builders,
    language_groovy,
    language_java,
    language_jvm,
    language_native,
    maven,
    platform_base,
    platform_jvm,
    platform_native,
    plugin_development,
    plugin_use,
    plugins,
    publish,
    reporting,
    resources_gcs,
    resources_http,
    resources_s3,
    resources_sftp,
    scala,
    security,
    signing,
    test_kit,
    testing_base,
    testing_junit_platform,
    testing_jvm,
    testing_native,
    tooling_api_builders,
    tooling_native,
    version_control,
    workers,


    ;

    String customName;


    Gradle7PluginsRefs() {
        customName = name().replace('_', '-')
    }

    public static List<Gradle7PluginsRefs> all = values().toList()
    public static List<Gradle7PluginsRefs> usedForCompile = [resources_http,maven,publish,dependency_management,plugin_use,]



}
