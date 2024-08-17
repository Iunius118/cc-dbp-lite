package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LuaSQLStatement extends LuaSQLStatementBase {
    public LuaSQLStatement(Connection connection, Statement statement) {
        super(connection, statement);
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
     * Adds the given SQL command to the current list of commands for this statement.
     * The commands in this list can be executed as a batch by calling the function {@code executeBatch}.
     * @param sql {@code string} Typically this is a SQL {@code INSERT} or {@code UPDATE} statement.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void addBatch(String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error: Empty SQL statement");
        }

        try {
            statement.addBatch(sql);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
