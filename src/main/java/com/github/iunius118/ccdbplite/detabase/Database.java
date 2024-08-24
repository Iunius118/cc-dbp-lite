package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import org.sqlite.JDBC;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    static {
        try {
            // Load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Database() {}

    public static LuaSQLStatement createStatement(IComputerAccess computer, String databaseURL) throws LuaException {
        try {
            var connection = DriverManager.getConnection(JDBC.PREFIX + databaseURL);
            var statement = connection.createStatement();
            return new LuaSQLStatement(computer, connection, statement);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    public static LuaPreparedSQLStatement prepareStatement(IComputerAccess computer, String databaseURL, String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error: Empty SQL statement");
        }

        try {
            var connection = DriverManager.getConnection(JDBC.PREFIX + databaseURL);
            var preparedStatement = connection.prepareStatement(sql);
            return new LuaPreparedSQLStatement(computer, connection, preparedStatement);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
