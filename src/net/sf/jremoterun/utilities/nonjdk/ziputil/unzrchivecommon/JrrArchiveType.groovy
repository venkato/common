package net.sf.jremoterun.utilities.nonjdk.ziputil.unzrchivecommon

import groovy.transform.CompileStatic
import org.rauschig.jarchivelib.ArchiveFormat

@CompileStatic
enum JrrArchiveType {
    tarGz(ArchiveFormat.TAR),
    zip(ArchiveFormat.ZIP),

    ;

    org.rauschig.jarchivelib.ArchiveFormat format;

    JrrArchiveType(ArchiveFormat format) {
        this.format = format
    }
}
