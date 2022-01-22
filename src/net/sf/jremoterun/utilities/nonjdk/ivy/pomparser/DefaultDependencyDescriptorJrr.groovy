package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.core.module.descriptor.ModuleDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId;

import java.util.logging.Logger;

@CompileStatic
class DefaultDependencyDescriptorJrr extends DefaultDependencyDescriptor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DefaultDependencyDescriptorJrr(ModuleDescriptor md, ModuleRevisionId mrid, boolean force, boolean changing, boolean transitive) {
        super(md, mrid, force, changing, transitive)
    }

    DefaultDependencyDescriptorJrr(ModuleRevisionId mrid, boolean force) {
        super(mrid, force)
    }

    DefaultDependencyDescriptorJrr(ModuleRevisionId mrid, boolean force, boolean changing) {
        super(mrid, force, changing)
    }

    DefaultDependencyDescriptorJrr(ModuleDescriptor md, ModuleRevisionId mrid, ModuleRevisionId dynamicConstraint, boolean force, boolean changing, boolean transitive) {
        super(md, mrid, dynamicConstraint, force, changing, transitive)
    }
}
