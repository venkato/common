package net.sf.jremoterun.utilities.nonjdk.downloadutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UnzipRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.UrlRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.IffUrlRefs
import net.sf.jremoterun.utilities.nonjdk.sfdownloader.SfLink;

import java.util.logging.Logger;

@CompileStatic
interface  IffZipRefs {

    UnzipRef lessExec = new UnzipRef(new UrlRef(new URL("https://steve.fi/Software/less/less-332.zip")));
    UnzipRef winpty = new UnzipRef(new UrlRef(new URL("https://github.com/rprichard/winpty/releases/download/0.4.3/winpty-0.4.3-msvc2015.zip")));
    UnzipRef vncviewer = new UnzipRef(new UrlRef(new URL("https://www.tightvnc.com/download/2.8.3/tvnjviewer-2.8.3-src-gnugpl.zip")));
    UnzipRef weirdx = new UnzipRef(new SfLink("weirdx/WeirdX/1.0.32/weirdx-1.0.32.zip"));


//    SfLink idwDockingUrl2 =
    UnzipRef idwDockingUrl3 = new UnzipRef(new SfLink("infonode/InfoNode%20Docking%20Windows/1.6.1/idw-gpl-1.6.1.zip"))


}
