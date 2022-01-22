package net.sf.jremoterun.utilities.nonjdk.maven.http;

import com.google.inject.TypeLiteral;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper;
import org.eclipse.sisu.plexus.PlexusBean;
import org.eclipse.sisu.plexus.PlexusBeanLocator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Main class.
 * JrrMavenCliWrapper.runBefore.add {
 * net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenWagonAdder.add()
 * }
 */
@CompileStatic
public class JrrMavenWagonAdder implements PlexusBeanLocator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class httpHandlerCustom = JrrMavenDownloadHttpHandler.class;

    public Collection<PlexusBean> i;

    public static void add() {
        add1(new JrrMavenWagonAdder());
    }

    public static void add1(PlexusBeanLocator plexusBeanLocator) {
        JrrMavenCliWrapper.cli.plexusBeanLocatorProxy.customPlexusBeanLocator.add(plexusBeanLocator);
    }

    public static void add3(Class httpHandlerCustom1) {
        JrrMavenWagonAdder jrrMavenWagonAdder1 = new JrrMavenWagonAdder();
        jrrMavenWagonAdder1.httpHandlerCustom = httpHandlerCustom1;
        add1(jrrMavenWagonAdder1);
    }

    void init1() {
        if (i == null) {
            LinkedList<PlexusBean> i2 = new LinkedList<>();
            i2.add(new PlexusBeanPerLookupJrr(httpHandlerCustom));
            i = i2;
        }
    }

    @Override
    public <T> Iterable<PlexusBean<T>> locate(TypeLiteral<T> role, String... hints) {
        // log.info "lookup1 ${role}"
        boolean macthed1 = false;
        if (role.getType() == org.apache.maven.wagon.Wagon.class) {
            if (hints == null) {
                macthed1 = true;
            } else {
                for (String s : hints) {
                    if ("http".equals(s) || "https".equals(s)) {
                        macthed1 = true;
                    }
                }

            }
        }
        if (macthed1) {
            //log.info "found 11 ${hints}"
            init1();
            return (Iterable) i;
        }
        return null;
    }


}
