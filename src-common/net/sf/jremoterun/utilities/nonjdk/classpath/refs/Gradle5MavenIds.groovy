package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommonDummy
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

import java.util.logging.Logger

@CompileStatic
enum Gradle5MavenIds implements MavenIdAndRepoContains, EnumNameProvider {
//    announce,
    antlr,
    api_metadata,
    architecture_test,
    base_services,
    base_services_groovy,
    bootstrap,
    build_cache,
    build_cache_http,
    build_cache_packaging,
//    build_comparison,
    build_init,
    build_option,
    build_profile,
    cli,
    code_quality,
    composite_builds,
    core,
    core_api,
    dependency_management,
    diagnostics,
    docs,
    ear,
    execution,
    file_collections,
    files,
    hashing,
    ide,
    ide_native,
    ide_play,
    installation_beacon,
    instant_execution,
    ivy,
    jacoco,
    javascript,
    jvm_services,
    kotlin_dsl,
    kotlin_dsl_provider_plugins,
    kotlin_dsl_tooling_builders,
    kotlin_dsl_tooling_models,
    language_groovy,
    language_java,
    language_jvm,
    language_native,
    language_scala,
    launcher,
    logging,
    maven,
    messaging,
    model_core,
    model_groovy,
    native_,
//    osgi,
    persistent_cache,
    pineapple,
    platform_base,
    platform_jvm,
    platform_native,
    platform_play,
    plugin_development,
    plugin_use,
    plugins,
    process_services,
    publish,
    reporting,
    resources,
    resources_gcs,
    resources_http,
    resources_s3,
    resources_sftp,
    runtime_api_info,
    scala,
    signing,
    smoke_test,
    snapshots,
    soak,
    test_kit,
    testing_base,
    testing_junit_platform,
    testing_jvm,
    testing_native,
    tooling_api,
    tooling_api_builders,
    tooling_native,
    version_control,
    worker_processes,
    workers,
    wrapper,

    ;


    MavenId m;


    Gradle5MavenIds() {
        String artifactId;
        if (name() == 'native_') {
            artifactId = 'gradle-native'
        } else {
            artifactId = 'gradle-' + name().replace('_', '-')
        }
        m = new MavenId('org.gradle', artifactId, '6.1.1');
    }

    public static List<Gradle5MavenIds> all = values().toList()

    public static List<Gradle5MavenIds> usedForCompile = [base_services, core_api, core, execution, file_collections, logging, messaging, model_core, process_services, resources_http, resources, tooling_api,native_]


    @Override
    String getCustomName() {
        return m.artifactId
    }


    @Override
    MavenIdAndRepo getMavenIdAndRepo() {
        return new MavenIdAndRepo(m, MavenRepositoriesEnum.gradle);
    }


}
