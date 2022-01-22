package net.sf.jremoterun.utilities.nonjdk.classpath.helpers;

import net.sf.jremoterun.utilities.classpath.ToFileRef2;
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf;

public interface ClassWhereLocatedAndAlternativeRef extends ToFileRef2 {
    ClassWhereLocatedAndAlternative getRef();
}
