package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository
import net.sf.jremoterun.utilities.nonjdk.classpath.MavenRepositoriesEnum;

import java.util.logging.Logger;

@CompileStatic
enum MavenIdAndRepoCustom implements MavenIdAndRepoContains {
    eclipseGitHubApi(new MavenId('org.eclipse.mylyn.github:org.eclipse.egit.github.core:5.7.0.202003110725-r'), MavenRepositoriesEnum.eclipse),
    /**
     *  Main class :
     * @see org.tmatesoft.sqljet.browser.DBBrowser
     * @see NetbeansMavenIds#netbeans_swing_outline
     */
    sqlLiteBrowser(new MavenId('org.tmatesoft.sqljet:sqljet-browser:1.1.10'), MavenRepositoriesEnum.tmatesoft),
    /**
     * No jars here
     */
    jibGradlePluginMain(new MavenId('com.google.cloud.tools.jib:com.google.cloud.tools.jib.gradle.plugin:3.3.0'), MavenRepositoriesEnum.gradlePluginsPortal),
    jibGradlePluginImpl(new MavenId('com.google.cloud.tools:jib-gradle-plugin:3.3.0'), MavenRepositoriesEnum.gradlePluginsPortal),
    ;

    MavenId m;
    IBiblioRepository repo;

    MavenIdAndRepoCustom(MavenId m, IBiblioRepository repo) {
        this.m = m
        this.repo = repo
    }

    @Override
    MavenIdAndRepo getMavenIdAndRepo() {
        return new MavenIdAndRepo(m, repo)
    }

}
