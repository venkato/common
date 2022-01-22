package net.sf.jremoterun.utilities.nonjdk.sql

import groovy.sql.Sql
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement;
import java.util.logging.Logger;

@CompileStatic
class SqlJrr extends Sql{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public SqlJdkUtils sqlJdkUtils=new SqlJdkUtils();

    SqlJrr(DataSource dataSource) {
        super(dataSource)
    }

    SqlJrr(Connection connection) {
        super(connection)
    }

    SqlJrr(Sql parent) {
        super(parent)
    }

    @Override
    protected void closeResources(Connection connection1, Statement statement1, ResultSet results) {
        sqlJdkUtils.closeQuietly(results)
        closeResources(connection1,statement1)
    }

    @Override
    protected void closeResources(Connection connection1, Statement statement1) {
        boolean isCacheStatements1 = isCacheStatements()
        if(isCacheStatements1){

        }else {
            sqlJdkUtils.closeQuietly(statement1)
            closeResources(connection1);
        }
    }

    @Override
    protected void closeResources(Connection connection1) {
        super.closeResources(connection1)
    }
}
