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

    @LuaFunction
    public final LuaResultSet getResultSet() throws LuaException {
        try {
            return new LuaResultSet(statement.getResultSet());
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean getMoreResults() throws LuaException {
        try {
            return statement.getMoreResults();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final int getUpdateCount() throws LuaException {
        try {
            return statement.getUpdateCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

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
