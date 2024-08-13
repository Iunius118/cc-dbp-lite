package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LuaSQLStatement {
    private final Connection connection;
    private final Statement statement;

    public LuaSQLStatement(Connection connection, Statement statement) {
        this.connection = connection;
        this.statement = statement;
    }

    @LuaFunction
    public final boolean execute(String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error (empty SQL statement)");
        }

        try {
            return statement.execute(sql);
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
