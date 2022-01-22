package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic

@CompileStatic
enum BytesDivider {
    bytes(1),
    kb(1_000),
    mb(1_000_000),
    gb(1_000_000_000),
    tb(1_000_000_000_000),
    ;

    public long divider
    public long dividerBy1000

    BytesDivider(long divider1) {
        divider = divider1
        dividerBy1000 = divider1*1000
    }

    long multBy(long by1){
        return divider*by1
    }
}
