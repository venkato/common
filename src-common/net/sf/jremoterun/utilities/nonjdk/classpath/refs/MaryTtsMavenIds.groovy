package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum

@CompileStatic
enum MaryTtsMavenIds implements MavenIdAndRepoContains {

    fast_md5('com.twmacinta:fast-md5:2.7.1'),
    emotionml_checker('de.dfki.mary:emotionml-checker-java:1.2.2'),
    voice_cmu_slt_hsmm('de.dfki.mary:voice-cmu-slt-hsmm:5.2.1'),
    marytts_common('de.dfki.mary:marytts-common:5.2.1'),
    marytts_runtime('de.dfki.mary:marytts-runtime:5.2.1'),
    marytts_lang_en('de.dfki.mary:marytts-lang-en:5.2.1'),
    marytts_lang_ru('de.dfki.mary:marytts-lang-ru:5.2.1'),
//    lexiconEN( 'de.dfki.mary:marytts-lexicon-en_US-cmudict:0.1.1'),
//    lexiconRu( 'de.dfki.mary:marytts-lexicon-ru:0.1.1'),
    marytts_signalproc('de.dfki.mary:marytts-signalproc:5.2.1'),
    jtok_core('de.dfki.lt.jtok:jtok-core:1.9.3'),
    mathJampack('gov.nist.math:Jampack:1.0'),
    ;


    MavenIdAndRepo mavenIdAndRepo;


    MaryTtsMavenIds(String m2) {
        this.mavenIdAndRepo = new MavenIdAndRepo(new MavenId(m2), MavenRepositoriesEnum.jcenter)
    }




}
