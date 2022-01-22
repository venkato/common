package net.sf.jremoterun.utilities.nonjdk.maven.launcher.notused;

import com.google.inject.TypeLiteral;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.eclipse.sisu.plexus.PlexusBean;
import org.eclipse.sisu.plexus.PlexusBeanLocator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

@CompileStatic
public class PlexusBeanLocatorCustom implements PlexusBeanLocator{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, Collection<PlexusBean>> oo = new HashMap<>();

    public void addBean(Class role,Class impl){
        PlexusBeanJrr plexusBeanJrr = new PlexusBeanJrr(impl);
        LinkedList<PlexusBean> objectsll = new LinkedList<>();
        objectsll.add(plexusBeanJrr);
        oo.put(role.getName(), objectsll);
    }

    @Override
    public  Iterable<PlexusBean> locate(TypeLiteral role, String... hints) {
        Type type1 = role.getType();
        if (type1 instanceof Class) {
            Class clas = (Class) type1;
            Collection<PlexusBean> value1 = oo.get(clas.getName());
//            LinkedList<PlexusBean> objectsll = new LinkedList<>();
//            return objectsll;
            return value1;
        }

        return null;
    }
}
