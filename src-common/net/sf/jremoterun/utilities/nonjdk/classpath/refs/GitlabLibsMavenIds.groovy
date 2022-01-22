package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath;

import java.util.logging.Logger;

@CompileStatic
interface GitlabLibsMavenIds {

    LatestMavenIds log4jMavenId = LatestMavenIds.log4jOld

    // 5.6 + need java11
    MavenId coreLib = new MavenId('org.gitlab4j:gitlab4j-api:5.8.1');

    MavenId jsr310 = new MavenId('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3');

    MavenId jakartaWsRsApi = new MavenId('jakarta.ws.rs:jakarta.ws.rs-api:2.1.6');

    // add also : fastXmlsCore
    List<? extends MavenIdContains> jibDockerMavenIds = [
            LatestMavenIds.dockerJibCore,
            LatestMavenIds.dockerJibBuildPlan,
            LatestMavenIds.googleHttpClientCommon,
            LatestMavenIds.googleHttpClientApache,
            new MavenId('io.grpc:grpc-context:1.22.1'),
            LatestMavenIds.opencensusApi,
            new MavenId('io.opencensus:opencensus-contrib-http-util:0.31.1'),
    ];


    List<MavenId> fastXmlsOthers = [
            new MavenId('com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.3'),
            new MavenId('com.fasterxml.jackson.datatype:jackson-datatype-joda:2.13.3'),
            new MavenId('com.fasterxml.woodstox:woodstox-core:6.0.1'),
    ]

    List<MavenId> fastXmlsCore = [
            new MavenId('com.fasterxml.jackson.core:jackson-annotations:2.13.3'),
            new MavenId('com.fasterxml.jackson.core:jackson-core:2.13.3'),
            new MavenId('com.fasterxml.jackson.core:jackson-databind:2.13.3'),
    ]

    @Deprecated
    List<MavenId> fastXmlsCoreOld = [
            new MavenId('com.fasterxml.jackson.core:jackson-annotations:2.12.2'),
            new MavenId('com.fasterxml.jackson.core:jackson-core:2.12.2'),
            new MavenId('com.fasterxml.jackson.core:jackson-databind:2.12.2'),
    ]

    List<MavenId> fastXmls = fastXmlsCore + [
            new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.12.2'),
            new MavenId('com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.12.2'),
            new MavenId('com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.12.2'),
    ];


    List<MavenId> glassfish = [
            new MavenId('org.glassfish.hk2.external:aopalliance-repackaged:2.6.1'),
            new MavenId('org.glassfish.hk2.external:jakarta.inject:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-api:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-locator:2.6.1'),
            new MavenId('org.glassfish.hk2:hk2-utils:2.6.1'),
            new MavenId('org.glassfish.hk2:osgi-resource-locator:1.0.3'),
            new MavenId('org.glassfish.jersey.connectors:jersey-apache-connector:2.30.1'),
            new MavenId('org.glassfish.jersey.core:jersey-client:2.30.1'),
            new MavenId('org.glassfish.jersey.core:jersey-common:2.30.1'),
            new MavenId('org.glassfish.jersey.inject:jersey-hk2:2.30.1'),
            new MavenId('org.glassfish.jersey.media:jersey-media-multipart:2.30.1'),
            new MavenId('org.glassfish.jersey.media:jersey-media-json-jackson:2.35'),
            new MavenId('org.glassfish.jersey.ext:jersey-entity-filtering:2.35'),
            new MavenId('org.jvnet.mimepull:mimepull:1.9.11'),
    ];


    List<MavenId> jakarta = [
            new MavenId('jakarta.activation:jakarta.activation-api:1.2.2'),
            new MavenId('jakarta.annotation:jakarta.annotation-api:1.3.5'),
            new MavenId('jakarta.servlet:jakarta.servlet-api:4.0.3'),
            jakartaWsRsApi,
            new MavenId('jakarta.xml.bind:jakarta.xml.bind-api:2.3.2'),
    ];


}
