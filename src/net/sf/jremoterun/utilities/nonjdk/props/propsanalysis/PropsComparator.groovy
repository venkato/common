package net.sf.jremoterun.utilities.nonjdk.props.propsanalysis

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.LocationElement;

import java.util.logging.Logger;

@CompileStatic
abstract class PropsComparator implements Comparator<Map.Entry<Object, LocationElement>>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

   abstract   boolean needSortByLocation() ;

    @Override
    int compare(Map.Entry<Object, LocationElement> o1, Map.Entry<Object, LocationElement> o2) {
        if (needSortByLocation()) {
            return "${o1.value.location}.${o1.key}".compareTo("${o2.value.location}.${o2.key}".toString())
        }
        return "${o1.key}".compareTo("${o2.key}".toString())
    }

}
