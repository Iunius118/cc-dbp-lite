package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.peripheral.IComputerAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionHolder {
    protected final Connection connection;
    protected final Statement statement;
    protected final IComputerAccess computer;

    private boolean isExpired = false;
    private boolean isClosed = false;

    public ConnectionHolder(Connection connection, Statement statement, IComputerAccess computer) {
        this.connection = connection;
        this.statement = statement;
        this.computer = computer;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void markExpired() {
        isExpired = true;
    }

    public void closeConnection() {
        if (!isClosed) {
            try {
                statement.close();
            } catch (SQLException ignored) {}

            try {
                connection.close();
            } catch (SQLException ignored) {}

            isClosed = true;
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isOpenedBy(IComputerAccess computerIn) {
        return computer != null && computer == computerIn;
    }
}
