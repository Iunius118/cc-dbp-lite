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
