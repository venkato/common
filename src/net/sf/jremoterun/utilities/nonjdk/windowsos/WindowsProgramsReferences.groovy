package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2
import net.sf.jremoterun.utilities.nonjdk.javalangutils.OsEnvNamesFile

@CompileStatic
enum WindowsProgramsReferences implements ToFileRefRedirect2 {

    taskManager(new File('C:/Windows/System32/Taskmgr.exe')),
    windword(UserHomeClass.ProgramFiles.getRedirect().childL('Microsoft Office/root/Office16/WINWORD.EXE')),
    excel(UserHomeClass.ProgramFiles.getRedirect().childL('Microsoft Office/root/Office16/EXCEL.EXE')),
    startupUserDir(OsEnvNamesFile.USERPROFILE.getRedirect().childL('AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup')),
    startupHostDir(OsEnvNamesFile.ProgramData.getRedirect().childL('Microsoft/Windows/Start Menu/Programs/Startup')),
    //chocolatey(OsEnvNamesFile.ProgramData.getRedirect().childL('chocolatey')),
    //noSuchProp5(OsEnvNamesFile.noSuchProp8.getRedirect().childL('Microsoft99/Windows11/Start Menu/Programs/Startup')),

    ;


    File2FileRefWithSupportI redirect;

    WindowsProgramsReferences(FileChildLazyRef fileRef) {
        redirect = fileRef
    }

    WindowsProgramsReferences(File file) {
        redirect = new FileToFileRef(file)
    }
}
