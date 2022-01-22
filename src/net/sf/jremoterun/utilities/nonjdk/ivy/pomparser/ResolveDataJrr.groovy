package net.sf.jremoterun.utilities.nonjdk.ivy.pomparser

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.report.ConfigurationResolveReport
import org.apache.ivy.core.resolve.ResolveData
import org.apache.ivy.core.resolve.ResolveEngine
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.resolve.VisitData;

import java.util.logging.Logger;

@CompileStatic
class ResolveDataJrr extends ResolveData{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ResolveDataJrr(ResolveData data, boolean validate) {
        super(data, validate)
    }

    ResolveDataJrr(ResolveEngine engine, ResolveOptions options) {
        super(engine, options)
    }

    ResolveDataJrr(ResolveEngine engine, ResolveOptions options, ConfigurationResolveReport report) {
        super(engine, options, report)
    }

    ResolveDataJrr(ResolveEngine engine, ResolveOptions options, ConfigurationResolveReport report, Map<ModuleRevisionId, VisitData> visitData) {
        super(engine, options, report, visitData)
    }
}
