package net.sf.jremoterun.utilities.nonjdk.maven

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum MavenUrlSuffixes implements EnumNameProvider {
    metadata('maven-metadata.xml'),
    pom,
    md5,
    sha1,
    sha256,
    sha512,
    ;

    String customName;

    MavenUrlSuffixes(String custom1) {
        this.customName = custom1;
    }

    MavenUrlSuffixes() {
        this.customName = '.' + name()
    }
}
