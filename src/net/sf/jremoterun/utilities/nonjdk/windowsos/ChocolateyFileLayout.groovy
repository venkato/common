package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNamesFile

@CompileStatic
enum ChocolateyFileLayout  implements ToFileRefRedirect2 {

    mainDir(OsEnvNamesFile.ProgramData.getRedirect().childL('chocolatey')),
    chocoExec(OsEnvNamesFile.ProgramData.getRedirect().childL('chocolatey/bin/choco.exe')),
    lib(OsEnvNamesFile.ProgramData.getRedirect().childL('chocolatey/lib/')),

    ;



    File2FileRefWithSupportI redirect;

    ChocolateyFileLayout(File2FileRefWithSupportI redirect) {
        this.redirect = redirect
    }
}
