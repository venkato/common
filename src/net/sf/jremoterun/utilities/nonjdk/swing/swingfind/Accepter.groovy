package net.sf.jremoterun.utilities.nonjdk.swing.swingfind

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.awt.Component;
import java.util.logging.Logger;

@CompileStatic
interface  Accepter{

    public boolean accept(Component component);

}
