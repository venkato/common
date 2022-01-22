package net.sf.jremoterun.utilities.nonjdk.windowsos

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ZeroOverheadFileRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2;

import java.util.logging.Logger;

@CompileStatic
enum Browsers implements File2FileRefWithSupportI, ZeroOverheadFileRef, ToFileRefRedirect2 {
    edge(new File('C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe')),
    chrome(new File('C:/Program Files (x86)/Google/Chrome/Application/chrome.exe')),
    totalCmd(UserHomeClass.APPDATA.childL('GHISLER/WINCMD.INI')),
    wsl(new File('C:/Windows/System32/wsl.exe')),
    chromeData(UserHomeClass.LOCALAPPDATA.childL('Google/Chrome/User Data/Default/')),
    edgeData(UserHomeClass.LOCALAPPDATA.childL('Microsoft/Edge/User Data/Default/')),

    ;

    public File f;
    File2FileRefWithSupportI redirect;

    Browsers(FileChildLazyRef f2) {
        f= f2.resolveToFile()
        redirect = f2
    }

    Browsers(File f) {
        this.f = f
        redirect = new FileToFileRef(f)
    }

    @Override
    File resolveToFile() {
        return f
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this, child);
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this, child)
    }


}
