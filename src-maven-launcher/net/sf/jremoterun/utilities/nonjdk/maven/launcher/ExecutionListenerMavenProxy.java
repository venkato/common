package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionListener;

import java.util.ArrayList;
import java.util.List;

public class ExecutionListenerMavenProxy implements ExecutionListener {

    public List<ExecutionListener> nestedListeners = new ArrayList<>();

    @Override
    public void projectDiscoveryStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.projectDiscoveryStarted(event);
        }
    }

    @Override
    public void sessionStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.sessionStarted(event);
        }
    }

    @Override
    public void sessionEnded(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.sessionEnded(event);
        }
    }

    @Override
    public void projectSkipped(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.projectSkipped(event);
        }
    }

    @Override
    public void projectStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.projectStarted(event);
        }
    }

    @Override
    public void projectSucceeded(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.projectSucceeded(event);
        }
    }

    @Override
    public void projectFailed(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.projectFailed(event);
        }
    }

    @Override
    public void mojoSkipped(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.mojoSkipped(event);
        }
    }

    @Override
    public void mojoStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.mojoStarted(event);
        }
    }

    @Override
    public void mojoSucceeded(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.mojoSucceeded(event);
        }
    }

    @Override
    public void mojoFailed(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.mojoFailed(event);
        }
    }

    @Override
    public void forkStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkStarted(event);
        }
    }

    @Override
    public void forkSucceeded(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkSucceeded(event);
        }
    }

    @Override
    public void forkFailed(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkFailed(event);
        }
    }

    @Override
    public void forkedProjectStarted(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkedProjectStarted(event);
        }
    }

    @Override
    public void forkedProjectSucceeded(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkedProjectSucceeded(event);
        }
    }

    @Override
    public void forkedProjectFailed(ExecutionEvent event) {
        for (ExecutionListener el : nestedListeners) {
            el.forkedProjectFailed(event);
        }
    }
}
