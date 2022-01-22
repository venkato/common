package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic

import javax.management.ObjectName

@CompileStatic
interface OpenFolderInMuCommander2 {

    ObjectName defaultObjectName1 = new ObjectName('iff:type=muopenfile')


    abstract void openInMuCommander3(String location)

}
