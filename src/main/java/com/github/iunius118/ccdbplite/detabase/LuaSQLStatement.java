package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LuaSQLStatement {
    protected final Connection connection;
    private final Statement statement;

    public LuaSQLStatement(Connection connection, Statement statement) {
        this.connection = connection;
        this.statement = statement;
    }

    /**
     * Executes the given SQL statement.
     * @param sql {@code string} An SQL statement.
     * @return {@code boolean} {@code true} if the first result is a {@code ResultSet} table;
     *                         {@code false} if it is an update count or there are no results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean execute(String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error: Empty SQL statement");
        }

        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the current result as a {@code ResultSet} table containing functions for manipulating the result.
     * The result is read-only, and its cursor may move only forward.
     * @return {@code table} The table containing the result and functions that wraps {@code ResultSet} and {@code ResultSetMetaData} of JDBC.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final LuaResultSet getResultSet() throws LuaException {
        try {
            var resultSet = statement.getResultSet();

            if (resultSet != null) {
                return new LuaResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }

        throw new LuaException("The result is update count or there are no more results");
    }

    /**
     * Retrieves the current result as an update count.
     * @return {@code number} The current result as an update count;
     *                        {@code -1} if the current result is a {@code ResultSet} table or there are no more results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getUpdateCount() throws LuaException {
        try {
            return statement.getUpdateCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Moves to the next result.
     *
     * <p>There are no more results when the following is true:
     *
     * <pre><code>
     *     -- stmt is a Statement table
     *     ((stmt.getMoreResults() == false) and (stmt.getUpdateCount() == -1))
     * </code></pre>
     * @return {@code boolean} {@code true} if the next result is a {@code ResultSet} table;
     *                         {@code false} if it is an update count or there are no more results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean getMoreResults() throws LuaException {
        try {
            return statement.getMoreResults();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /// Connection Functions ///////////////////////////////////////////////////

    /**
     * Retrieves this connection's current transaction isolation level.
     * @return {@code number} The current transaction isolation level.
     *                        {@code 0} if transactions are not supported;
     *                        {@code 1} if it is READ UNCOMMITTED;
     *                        {@code 2} if it is READ COMMITTED;
     *                        {@code 4} if it is REPEATABLE READ;
     *                        {@code 8} if it is SERIALIZABLE.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getTransactionIsolation() throws LuaException {
        try {
            return connection.getTransactionIsolation();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Attempts to change the transaction isolation level for this connection to the one given.
     * @param level {@code number} One of the following numbers:
     *                             {@code 1} for READ UNCOMMITTED,
     *                             {@code 2} for READ COMMITTED,
     *                             {@code 4} for REPEATABLE READ,
     *                             {@code 8} for SERIALIZABLE.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setTransactionIsolation(int level) throws LuaException {
        try {
            connection.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the current auto-commit mode for this connection.
     * @return {@code boolean} The current state of this connection's auto-commit mode.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean getAutoCommit() throws LuaException {
        try {
            return connection.getAutoCommit();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets this connection's auto-commit mode to the given state.
     * @param autoCommit {@code boolean} {@code true} to enable auto-commit mode; {@code false} to disable it.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setAutoCommit(boolean autoCommit) throws LuaException {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by this connection.
     * This function should be used only when auto-commit mode has been disabled.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void commit() throws LuaException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Undoes all changes made in the current transaction and releases any database locks currently held by this connection.
     * This function should be used only when auto-commit mode has been disabled.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void rollback() throws LuaException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Releases the connection to the database and JDBC resources immediately.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void close() throws LuaException {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
