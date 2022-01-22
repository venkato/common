package net.sf.jremoterun.utilities.nonjdk.sql

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.SQLWarning
import java.sql.Statement
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class SqlJdkUtils {
    private final Logger logStatic = JrrClassUtils.getJdkLogForCurrentClass();
    public Logger log = logStatic;
    public Level logWarningLevel = Level.WARNING;
    public Level logExceptionsLevel = Level.WARNING;
    public boolean printWarnings = true;
    public static SqlJdkUtils defaultInstance = new SqlJdkUtils();

    static void closeQuietlyAllS(Connection connection, Statement st, ResultSet rs) {
        defaultInstance.closeQuietlyAll(connection, st, rs)
    }

    void closeQuietlyAll(Connection connection, Statement st, ResultSet rs) {
        closeQuietly(connection)
        closeQuietly(st)
        closeQuietly(rs)
    }

    void closeQuietly(Connection st) {
        if (st != null) {
            if (printWarnings) {
                try {
                    printAndCleanWarnings(st)
                } catch (Throwable e) {
                    onException(st, e)
                } finally {
                    JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
                }
            } else {
                JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
            }
        }
    }

    void closeQuietly(Statement st) {
        if (st != null) {
            if (printWarnings) {
                try {
                    printAndCleanWarnings(st)
                } catch (Throwable e) {
                    onException(st, e)
                } finally {
                    JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
                }
            } else {
                JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
            }
        }
    }

    void closeQuietly(ResultSet st) {
        if (st != null) {
            if (printWarnings) {
                try {
                    printAndCleanWarnings(st)
                } catch (Throwable e) {
                    onException(st, e)
                } finally {
                    JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
                }
            } else {
                JrrIoUtils.closeQuietlyImpl(st, log, logExceptionsLevel)
            }
        }
    }


    void onException(Object obj, Throwable e) {
        log.log(logExceptionsLevel, obj.getClass().getSimpleName(), e)
    }

    void printAndCleanWarnings(Connection st) {
        SQLWarning warnings1 = st.getWarnings()
        printWarnings(st, warnings1)
        st.clearWarnings();
    }

    void printAndCleanWarnings(Statement st) {
        SQLWarning warnings1 = st.getWarnings()
        printWarnings(st, warnings1)
        st.clearWarnings();
    }

    void printAndCleanWarnings(ResultSet st) {
        SQLWarning warnings1 = st.getWarnings()
        printWarnings(st, warnings1)
        st.clearWarnings();
    }


    void printWarnings(Object obj, SQLWarning exception) {
        if (exception != null) {
            List<String> msgs = collectExceptionsMsg(exception)
            if (msgs.size() > 0) {
                printMsgs(msgs)
            }
        }
    }

    public String sep = '\n'

    void printMsgs(List<String> msgs) {
        if (msgs.size() > 1) {
            log.log(logWarningLevel, "sql warnings count = ${msgs.size()} :${sep} ${msgs.join(sep)}")
        } else {
            log.log(logWarningLevel, msgs.get(0))
        }
    }

    List<String> collectExceptionsMsg(SQLException exception) {
        int count=0
        List<String> res = []
        while (true) {
            if (exception == null) {
                break
            }
            count++
            try {
                if (isNeedCollect(exception)) {
                    String s = exceptionToString(exception)
                    if (s != null) {
                        res.add(s)
                    }
                }
                exception = getNextException(exception)
            }catch (Throwable e){
                log.warn("failed at count = ${count}")
                throw e;
            }
        }
        return res
    }


    SQLException getNextException(SQLException exception) {
        return exception.getNextException();
    }

    boolean isNeedCollect(SQLException exception) {
        return true
    }

    String exceptionToString(SQLException exception) {
        return exception.getErrorCode() + ":" + exception.getMessage()
    }

}
