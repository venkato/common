/*
 * Copyright (C) 2008-2010, Google Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0 which is available at
 * https://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jgit.lib.BatchingProgressMonitor;
import org.eclipse.jgit.lib.Constants

import java.time.Duration
import java.util.logging.Logger;

/** Write progress messages out to the sideband channel. */
@CompileStatic
class GitProgressMonitorJrr extends BatchingProgressMonitor {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public GitProgressLogger gitProgressLogger = new GitProgressLogger()

    GitProgressMonitorJrr() {
    }

    void beginTask(String title, int work) {
        log.info "starting ${title} , items: ${work}"
        super.beginTask(title, work)
    }

    void endTask() {
        log.info "task ended"
        super.endTask()
    }

    protected void onUpdate(String taskName, int workCurr) {
        StringBuilder s = new StringBuilder();
        format(s, taskName, workCurr);
        s.append("   \r"); //$NON-NLS-1$
        gitProgressLogger.send(s);
    }


    protected void onEndTask(String taskName, int workCurr) {
        StringBuilder s = new StringBuilder();
        format(s, taskName, workCurr);
        s.append(", done\n"); //$NON-NLS-1$
        gitProgressLogger.send(s);
    }

    private void format(StringBuilder s, String taskName, int workCurr) {
        s.append(taskName);
        s.append(": "); //$NON-NLS-1$
        s.append(workCurr);
    }


    protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
        StringBuilder s = new StringBuilder();
        format(s, taskName, cmp, totalWork, pcnt);
        s.append("   \r"); //$NON-NLS-1$
        gitProgressLogger.send(s);
    }


    protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
        StringBuilder s = new StringBuilder();
        format(s, taskName, cmp, totalWork, pcnt);
        s.append("\n"); //$NON-NLS-1$
        gitProgressLogger.send(s);
    }

    private void format(StringBuilder s, String taskName, int cmp,
                        int totalWork, int pcnt) {
        s.append(taskName);
        s.append(": "); //$NON-NLS-1$
        if (pcnt < 100)
            s.append(' ');
        if (pcnt < 10)
            s.append(' ');
        s.append(pcnt);
        s.append("% ("); //$NON-NLS-1$
        s.append(cmp);
        s.append("/"); //$NON-NLS-1$
        s.append(totalWork);
        s.append(")"); //$NON-NLS-1$
    }

//	protected void send(StringBuilder s) {
//		log.info(s.toString())
//	}


    void onEndTask(String taskName, int workCurr, Duration duration) {
        onEndTask(taskName, workCurr)
    }

     void onUpdate(String taskName, int workCurr,                                     Duration duration){
         onUpdate(taskName,workCurr)
     }

    void onUpdate(String taskName, int workCurr, int workTotal, int percentDone, Duration duration) {
        onUpdate(taskName, workCurr, workTotal, percentDone)
    }

    void onEndTask(String taskName, int workCurr, int workTotal, int percentDone, Duration duration) {
        onEndTask(taskName, workCurr, workTotal, percentDone)
    }
}
