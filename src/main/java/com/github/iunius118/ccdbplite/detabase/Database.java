package com.github.iunius118.ccdbplite.detabase;

import com.github.iunius118.ccdbplite.CCDatabasePeripheralLite;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import org.sqlite.JDBC;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Database {
    private static final Set<ConnectionHolder> CONNECTIONS = new HashSet<>();

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
            var luaSQLStatement = new LuaSQLStatement(connection, statement, computer);
            addConnection(luaSQLStatement);
            return luaSQLStatement;
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
            var luaPreparedSQLStatement = new LuaPreparedSQLStatement(connection, preparedStatement, computer);
            addConnection(luaPreparedSQLStatement);
            return luaPreparedSQLStatement;
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    public static void addConnection(ConnectionHolder connection) {
        synchronized (CONNECTIONS) {
            CONNECTIONS.add(connection);
        }
    }

    public static void checkConnections() {
        synchronized (CONNECTIONS) {
            CONNECTIONS.forEach(connection -> {
                if (connection.isExpired()) {
                    // Close all expired connections
                    connection.closeConnection();
                } else {
                    // Mark all unexpired connections as expired to close on the next check
                    connection.markExpired();
                }
            });
            // Remove all closed connections from the set
            CONNECTIONS.removeIf(ConnectionHolder::isClosed);
            CCDatabasePeripheralLite.LOGGER.debug("Check opened database connections: {}", CONNECTIONS.size());
        }
    }

    public static void closeConnection(ConnectionHolder connection) {
        synchronized (CONNECTIONS) {
            if (!connection.isClosed()) {
                connection.closeConnection();
                CONNECTIONS.remove(connection);
            }
        }
    }

    public static void closeConnections(IComputerAccess computer) {
        synchronized (CONNECTIONS) {
            CONNECTIONS.stream().filter(c -> c.isOpenedBy(computer) && !c.isClosed()).forEach(ConnectionHolder::closeConnection);
            CONNECTIONS.removeIf(ConnectionHolder::isClosed);
        }
    }

    public static void closeAllConnections() {
        synchronized (CONNECTIONS) {
            CONNECTIONS.forEach(ConnectionHolder::closeConnection);
            CONNECTIONS.clear();
        }
    }
}
