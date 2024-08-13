package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LuaPreparedSQLStatement {
    private final Connection connection;
    private final PreparedStatement preparedStatement;

    public LuaPreparedSQLStatement(Connection connection, PreparedStatement preparedStatement) {
        this.connection = connection;
        this.preparedStatement = preparedStatement;
    }

    @LuaFunction
    public final void close() throws LuaException {
        try {
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
