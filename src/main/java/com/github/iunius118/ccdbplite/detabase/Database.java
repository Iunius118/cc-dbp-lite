package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.sqlite.JDBC;

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

    public static LuaSQLStatement createStatement(IComputerAccess computer, String databaseURL) {
        return new LuaSQLStatement(computer, JDBC.PREFIX + databaseURL);
    }

    public static LuaPreparedSQLStatement prepareStatement(IComputerAccess computer, String databaseURL, String sql) {
        return new LuaPreparedSQLStatement(computer, JDBC.PREFIX + databaseURL, sql);
    }
}
