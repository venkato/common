package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic

@CompileStatic
enum GitRepoType {
    gitHub('/commit/'),
    gitLab('/-/commit/'),
    ;


    public String commitSuffix

    GitRepoType(String commitSuffix) {
        this.commitSuffix = commitSuffix
    }
}
