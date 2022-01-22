package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import com.google.inject.TypeLiteral;
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.notused.PlexusBeanLocatorCustom;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.eclipse.sisu.plexus.PlexusBean;
import org.eclipse.sisu.plexus.PlexusBeanLocator;

import java.util.ArrayList;
import java.util.List;

public class PlexusBeanLocatorProxy implements PlexusBeanLocator {

    public List<PlexusBeanLocator> customPlexusBeanLocator = new ArrayList<>();
    public PlexusBeanLocator nestedPlexusBeanLocator;

    public PlexusBeanLocatorCustom plexusBeanLocatorCustom = new PlexusBeanLocatorCustom();

    public PlexusBeanLocatorProxy() {
        customPlexusBeanLocator.add(plexusBeanLocatorCustom);
//        plexusBeanLocatorCustom.addBean(Maven.class, DefaultMaven.class);
//        plexusBeanLocatorCustom.addBean(Maven.class, DefaultMavenJrr.class);
        //this.nestedPlexusBeanLocator = nestedPlexusBeanLocator;
    }

    @Override
    public <T> Iterable<PlexusBean<T>> locate(TypeLiteral<T> role, String... hints) {
        for(PlexusBeanLocator bl:customPlexusBeanLocator){
            Iterable<PlexusBean<T>> res = bl.locate(role, hints);
            if(res!=null){
                return res;
            }
        }
        return nestedPlexusBeanLocator.locate(role,hints);
    }
}
