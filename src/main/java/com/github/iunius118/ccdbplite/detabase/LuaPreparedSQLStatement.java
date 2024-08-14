package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LuaPreparedSQLStatement extends LuaSQLStatement {
    private final PreparedStatement preparedStatement;

    public LuaPreparedSQLStatement(Connection connection, PreparedStatement preparedStatement) {
        super(connection, preparedStatement);
        this.preparedStatement = preparedStatement;
    }

    // TODO: Add parameter setter methods

    @LuaFunction
    public final boolean execute() throws LuaException {
        try {
            return preparedStatement.execute();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
