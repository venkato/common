package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

/**
 * -Dsonar.verbose=true -Dsonar.log.level=TRACE -Dsonar.scanner.dumpToFile=sonardump.txt
 * https://docs.sonarcloud.io/advanced-setup/analysis-parameters/
 * @see org.sonarsource.scanner.maven.bootstrap.MavenProjectConverter
 */
@CompileStatic
enum SonarMavenIds implements MavenIdContains {

    pluginApi(  'org.sonarsource.api.plugin:sonar-plugin-api:9.9.0.229')
    ,scannerEngine(  'org.sonarsource.sonarqube:sonar-scanner-engine:10.0.0.68432')
    ,ws(  'org.sonarsource.sonarqube:sonar-ws:10.0.0.68432')
    ,scannerApi(  'org.sonarsource.scanner.api:sonar-scanner-api:2.16.2.588')
    ,scannerBatchApi(   'org.sonarsource.scanner.api:sonar-scanner-api-batch:2.16.2.588')
    ,gradlePlugin(   'org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3')
    //,mavenPlugin(   'org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184')
    ;

    MavenId m;

    SonarMavenIds(String m) {
        this.m = new MavenId(m)
    }

}
