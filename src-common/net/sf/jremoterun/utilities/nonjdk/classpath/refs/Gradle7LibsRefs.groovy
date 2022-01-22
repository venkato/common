package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

// https://github.com/gradle/native-platform/blob/master/native-platform/src/main/java/net/rubygrapefruit/platform/internal/WindowsTerminal.java
@CompileStatic
enum Gradle7LibsRefs implements  EnumNameProvider {
    api_metadata,
    base_annotations,
    base_services,
    base_services_groovy,
    bootstrap,
    build_cache,
    build_cache_base,
    build_cache_packaging,
    build_events,
    build_operations,
    build_option,
    cli,
    core,
    core_api,
    execution,
    file_collections,
    file_temp,
    file_watching,
    files,
    functional,
    hashing,
    installation_beacon,
    jvm_services,
    kotlin_dsl,
    kotlin_dsl_tooling_models,
    launcher,
    logging,
    messaging,
    model_core,
    model_groovy,
    native_,
    normalization_java,
    persistent_cache,
    problems,
    process_services,
    resources,
    runtime_api_info,
    snapshots,
    tooling_api,
    worker_processes,
    wrapper,
    //kotlin_compiler_embeddable_1.5.21_patched_for,

    ;

    String customName;

    Gradle7LibsRefs() {
        if(name() == 'native_'){
            customName = 'native'
        }else {
            customName = name().replace('_', '-')
        }
    }

    public static List<Gradle7LibsRefs> all = values().toList()

    // add resource_http plugin
    public static List<Gradle7LibsRefs> usedForCompile = [base_services,core_api,core,execution,file_collections,logging,
    messaging,model_core,process_services,resources,tooling_api,native_,file_watching,build_option,persistent_cache,launcher,
            base_services_groovy,functional,
    ]


}
